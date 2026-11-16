package web.com.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import jakarta.servlet.http.Part;
import web.com.utils.Constant;

public class VideoThumbnailStorage {
    public static final long MAX_SIZE = 5L * 1024 * 1024;
    private static final Pattern NAME = Pattern.compile("video-thumbnail/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(jpg|png)");

    public String validate(Part part) throws IOException {
        if (part.getSize() > MAX_SIZE) return "Ảnh đại diện không được vượt quá 5 MB.";
        try (InputStream input = part.getInputStream(); ImageInputStream image = ImageIO.createImageInputStream(input)) {
            if (image == null) return "Chỉ hỗ trợ ảnh JPEG hoặc PNG hợp lệ.";
            var readers = ImageIO.getImageReaders(image);
            if (!readers.hasNext()) return "Chỉ hỗ trợ ảnh JPEG hoặc PNG hợp lệ.";
            ImageReader reader = readers.next();
            try {
                String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                if (!format.equals("jpeg") && !format.equals("png")) return "Chỉ hỗ trợ ảnh JPEG hoặc PNG hợp lệ.";
                reader.setInput(image, true, true);
                int width = reader.getWidth(0), height = reader.getHeight(0);
                if (width <= 0 || height <= 0 || (long) width * height > 40_000_000L)
                    return "Kích thước ảnh không hợp lệ hoặc vượt quá 40 megapixel.";
                if (reader.read(0) == null) return "Không thể đọc ảnh đại diện.";
            } finally { reader.dispose(); }
        } catch (IOException | RuntimeException e) { return "Không thể đọc ảnh đại diện. Hãy chọn ảnh JPEG hoặc PNG hợp lệ."; }
        return null;
    }

    public String save(Part part) throws IOException {
        String extension;
        try (InputStream input = part.getInputStream(); ImageInputStream image = ImageIO.createImageInputStream(input)) {
            ImageReader reader = ImageIO.getImageReaders(image).next();
            try { extension = reader.getFormatName().equalsIgnoreCase("png") ? ".png" : ".jpg"; }
            finally { reader.dispose(); }
        }
        String name = "video-thumbnail/" + UUID.randomUUID() + extension;
        Path file = resolve(name);
        Files.createDirectories(file.getParent());
        try (InputStream input = part.getInputStream()) { Files.copy(input, file); }
        catch (IOException e) { Files.deleteIfExists(file); throw e; }
        return name;
    }

    public Path resolve(String name) {
        return name != null && NAME.matcher(name).matches() ? Path.of(Constant.DIR).resolve(name).normalize() : null;
    }
    public void delete(String name) throws IOException {
        Path file = resolve(name);
        if (file != null) Files.deleteIfExists(file);
    }
}
