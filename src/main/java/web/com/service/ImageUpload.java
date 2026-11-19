package web.com.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import web.com.utils.Constant;

public final class ImageUpload {
    public static final long MAX_SIZE = 5L * 1024 * 1024;
    public static final long MAX_REQUEST_SIZE = MAX_SIZE + 1024 * 1024;
    public static final String SIZE_ERROR = "Ảnh tải lên không được vượt quá 5 MB.";
    private static final String FORMAT_ERROR = "Vui lòng chọn ảnh JPEG, PNG hoặc GIF hợp lệ.";

    private ImageUpload() {
    }

    public static Part readPart(HttpServletRequest request, String field) throws IOException, ServletException {
        String contentType = request.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("multipart/form-data")) {
            return null;
        }
        return request.getPart(field);
    }

    public static String validatedExtension(Part part) {
        if (part == null || (part.getSize() == 0
                && (part.getSubmittedFileName() == null || part.getSubmittedFileName().isBlank()))) {
            return null;
        }
        if (part.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException(SIZE_ERROR);
        }
        try (InputStream input = part.getInputStream();
                ImageInputStream image = ImageIO.createImageInputStream(input)) {
            if (image == null) {
                throw new IllegalArgumentException(FORMAT_ERROR);
            }
            var readers = ImageIO.getImageReaders(image);
            if (!readers.hasNext()) {
                throw new IllegalArgumentException(FORMAT_ERROR);
            }
            ImageReader reader = readers.next();
            try {
                String extension = switch (reader.getFormatName().toLowerCase(Locale.ROOT)) {
                    case "jpeg" -> ".jpg";
                    case "png" -> ".png";
                    case "gif" -> ".gif";
                    default -> throw new IllegalArgumentException(FORMAT_ERROR);
                };
                reader.setInput(image, true, true);
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                if (width <= 0 || height <= 0 || (long) width * height > 40_000_000L) {
                    throw new IllegalArgumentException("Kích thước ảnh không hợp lệ hoặc vượt quá 40 megapixel.");
                }
                if (reader.read(0) == null) {
                    throw new IllegalArgumentException(FORMAT_ERROR);
                }
                return extension;
            } finally {
                reader.dispose();
            }
        } catch (IOException | IllegalStateException e) {
            throw new IllegalArgumentException(FORMAT_ERROR, e);
        }
    }

    public static String save(Part part, String extension, String folder) throws IOException {
        if (extension == null) {
            return null;
        }
        Path directory = Files.createDirectories(Path.of(Constant.DIR, folder));
        Path file = Files.createTempFile(directory, "upload-", extension);
        String relativePath = folder + "/" + file.getFileName();
        try (InputStream input = part.getInputStream()) {
            Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | RuntimeException e) {
            discard(relativePath, e);
            throw e;
        }
        return relativePath;
    }

    public static void discard(String relativePath, Throwable failure) {
        try {
            delete(relativePath);
        } catch (IOException e) {
            failure.addSuppressed(e);
        }
    }

    public static void delete(String relativePath) throws IOException {
        if (relativePath != null) {
            Files.deleteIfExists(Path.of(Constant.DIR, relativePath));
        }
    }
}
