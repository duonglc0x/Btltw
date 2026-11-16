package web.com.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.regex.Pattern;
import jakarta.servlet.http.Part;
import web.com.utils.Constant;

/** Stores only server-generated file names beneath the configured upload directory. */
public class VideoStorage {
    public static final long MAX_SIZE = 100L * 1024 * 1024;
    private static final Pattern STORED_NAME = Pattern.compile("video/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.mp4");

    public String validate(Part part) throws IOException {
        if (part == null || part.getSize() == 0) return "Vui lòng chọn tệp video MP4.";
        if (part.getSize() > MAX_SIZE) return "Tệp video không được vượt quá 100 MB.";
        String name = part.getSubmittedFileName();
        if (name == null || !name.toLowerCase(java.util.Locale.ROOT).endsWith(".mp4")) return "Chỉ hỗ trợ tệp MP4.";
        try (InputStream in = part.getInputStream()) {
            byte[] header = in.readNBytes(12);
            if (header.length < 12 || header[4] != 'f' || header[5] != 't' || header[6] != 'y' || header[7] != 'p')
                return "Tệp đã chọn không phải video MP4 hợp lệ.";
        }
        return null;
    }
    public String save(Part part) throws IOException {
        String name = "video/" + UUID.randomUUID() + ".mp4";
        Path file = resolve(name);
        Files.createDirectories(file.getParent());
        try (InputStream input = part.getInputStream()) { Files.copy(input, file); }
        catch (IOException e) { Files.deleteIfExists(file); throw e; }
        return name;
    }
    public Path resolve(String name) {
        if (name == null || !STORED_NAME.matcher(name).matches()) return null;
        return Path.of(Constant.DIR).resolve(name).normalize();
    }
    public void delete(String name) throws IOException {
        Path file = resolve(name);
        if (file != null) Files.deleteIfExists(file);
    }
}
