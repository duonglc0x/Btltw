package web.com.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.com.models.Video;
import web.com.service.VideoService;
import web.com.utils.Constant;
import web.com.utils.FormValidation;

@WebServlet(urlPatterns = {"/video", "/video/detail"})
public class VideoController extends HttpServlet {
    private final VideoService service = new VideoService();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/video/detail".equals(req.getServletPath())) {
            int id = positiveInt(req.getParameter("id"), 0);
            Video video = id == 0 ? null : service.get(id);
            if (video == null || !video.isActive()) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            req.setAttribute("video", video);
            req.getRequestDispatcher("/views/video-detail.jsp").forward(req, resp);
            return;
        }
        long count = service.countActive();
        int totalPages = (int) Math.min(Integer.MAX_VALUE, (count + Constant.VIDEO_PAGE_SIZE - 1) / Constant.VIDEO_PAGE_SIZE);
        int page = Math.min(positiveInt(req.getParameter("page"), 1), Math.max(1, totalPages));
        req.setAttribute("videos", service.getPage(page, Constant.VIDEO_PAGE_SIZE));
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.getRequestDispatcher("/views/video-list.jsp").forward(req, resp);
    }
    static int positiveInt(String value, int fallback) {
        return FormValidation.positiveInteger(value, fallback);
    }
}
