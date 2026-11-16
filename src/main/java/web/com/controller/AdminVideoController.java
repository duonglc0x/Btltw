package web.com.controller;

import java.io.IOException;
import java.util.UUID;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import web.com.models.Video;
import web.com.service.VideoService;
import web.com.service.VideoStorage;
import web.com.service.VideoThumbnailStorage;

@WebServlet(urlPatterns = {"/admin/video/list", "/admin/video/add", "/admin/video/edit", "/admin/video/delete"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = VideoStorage.MAX_SIZE, maxRequestSize = VideoStorage.MAX_SIZE + VideoThumbnailStorage.MAX_SIZE + 1024 * 1024)
public class AdminVideoController extends HttpServlet {
    private final VideoService service = new VideoService();
    private final VideoStorage storage = new VideoStorage();
    private final VideoThumbnailStorage thumbnails = new VideoThumbnailStorage();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if (path.endsWith("/delete")) { resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED); return; }
        csrfToken(req.getSession());
        if (path.endsWith("/list")) {
            req.setAttribute("videos", service.getAll());
            req.getRequestDispatcher("/views/admin/list-video.jsp").forward(req, resp);
            return;
        }
        Video video = path.endsWith("/edit") ? find(req) : new Video();
        if (video == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
        req.setAttribute("video", video);
        req.setAttribute("editing", path.endsWith("/edit"));
        req.getRequestDispatcher("/views/admin/video-form.jsp").forward(req, resp);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try { handlePost(req, resp); }
        catch (IllegalStateException e) { resp.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, "Tệp video không được vượt quá 100 MB."); }
    }
    private void handlePost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("videoCsrf") == null
                || !session.getAttribute("videoCsrf").equals(req.getParameter("csrf"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN); return;
        }
        String path = req.getServletPath();
        if (path.endsWith("/list")) { resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED); return; }
        if (path.endsWith("/delete")) {
            int id = VideoController.positiveInt(req.getParameter("id"), 0);
            Video video = id == 0 ? null : service.get(id);
            if (video == null || !service.delete(id)) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            deleteFile(video.getVideoUrl());
            deleteThumbnail(video.getThumbnailUrl());
        } else {
            boolean editing = path.endsWith("/edit");
            Video video = editing ? find(req) : new Video();
            if (video == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            video.setTitle(value(req, "title"));
            video.setDescription(value(req, "description"));
            video.setActive("on".equals(req.getParameter("active")));
            Part part = req.getPart("videoFile");
            Part image = req.getPart("thumbnailFile");
            boolean hasImage = image != null && image.getSize() > 0;
            String oldImage = video.getThumbnailUrl();
            boolean hasFile = part != null && part.getSize() > 0;
            String oldFile = video.getVideoUrl();
            String fileError = hasFile || !editing ? storage.validate(part) : null;
            if (hasFile && fileError == null) video.setVideoUrl("pending");
            String error = service.validate(video);
            if (fileError != null) error = fileError;
            if (error == null && hasImage) error = thumbnails.validate(image);
            if (error != null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                req.setAttribute("error", error); req.setAttribute("video", video); req.setAttribute("editing", editing);
                req.getRequestDispatcher("/views/admin/video-form.jsp").forward(req, resp); return;
            }
            String newFile = null, newImage = null;
            try {
                if (hasFile) { newFile = storage.save(part); video.setVideoUrl(newFile); }
                if (hasImage) { newImage = thumbnails.save(image); video.setThumbnailUrl(newImage); }
                service.save(video);
            } catch (IOException | RuntimeException e) {
                deleteFile(newFile); deleteThumbnail(newImage); throw e;
            }
            if (newFile != null) deleteFile(oldFile);
            if (newImage != null) deleteThumbnail(oldImage);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/video/list");
    }
    private void deleteFile(String name) {
        try { storage.delete(name); }
        catch (IOException e) { getServletContext().log("Không thể xóa tệp video cũ", e); }
    }
    private void deleteThumbnail(String name) {
        try { thumbnails.delete(name); }
        catch (IOException e) { getServletContext().log("Không thể xóa ảnh đại diện cũ", e); }
    }
    private Video find(HttpServletRequest req) {
        int id = VideoController.positiveInt(req.getParameter("id"), 0);
        return id == 0 ? null : service.get(id);
    }
    private String value(HttpServletRequest req, String name) {
        String value = req.getParameter(name); return value == null ? "" : value.trim();
    }
    private void csrfToken(HttpSession session) {
        if (session.getAttribute("videoCsrf") == null) session.setAttribute("videoCsrf", UUID.randomUUID().toString());
    }
}
