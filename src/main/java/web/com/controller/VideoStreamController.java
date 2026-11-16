package web.com.controller;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.com.models.Video;
import web.com.service.VideoService;
import web.com.service.VideoStorage;

@WebServlet("/video/stream")
public class VideoStreamController extends HttpServlet {
    private final VideoService service = new VideoService();
    private final VideoStorage storage = new VideoStorage();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = VideoController.positiveInt(req.getParameter("id"), 0);
        Video video = id == 0 ? null : service.get(id);
        Path file = video == null || !video.isActive() ? null : storage.resolve(video.getVideoUrl());
        if (file == null || !Files.isRegularFile(file)) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
        long size = Files.size(file);
        if (size == 0) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
        long start = 0, end = size - 1;
        String range = req.getHeader("Range");
        if (range != null) {
            try {
                if (!range.startsWith("bytes=") || range.contains(",")) throw new IllegalArgumentException();
                String[] bounds = range.substring(6).split("-", -1);
                if (bounds.length != 2 || (bounds[0].isEmpty() && bounds[1].isEmpty())) throw new IllegalArgumentException();
                if (bounds[0].isEmpty()) {
                    long suffix = Long.parseLong(bounds[1]);
                    if (suffix <= 0) throw new IllegalArgumentException();
                    start = Math.max(0, size - suffix);
                } else {
                    start = Long.parseLong(bounds[0]);
                    if (!bounds[1].isEmpty()) end = Math.min(end, Long.parseLong(bounds[1]));
                }
                if (start < 0 || start >= size || end < start) throw new IllegalArgumentException();
            } catch (IllegalArgumentException e) {
                resp.setHeader("Content-Range", "bytes */" + size);
                resp.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE); return;
            }
            resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            resp.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + size);
        }
        resp.setContentType("video/mp4");
        resp.setHeader("Accept-Ranges", "bytes");
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setContentLengthLong(end - start + 1);
        try (RandomAccessFile input = new RandomAccessFile(file.toFile(), "r")) {
            input.seek(start);
            byte[] buffer = new byte[64 * 1024];
            long remaining = end - start + 1;
            while (remaining > 0) {
                int read = input.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                if (read < 0) break;
                resp.getOutputStream().write(buffer, 0, read);
                remaining -= read;
            }
        }
    }
}
