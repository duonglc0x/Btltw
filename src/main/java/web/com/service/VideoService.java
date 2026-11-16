package web.com.service;

import java.net.URI;
import java.util.List;
import web.com.dao.VideoDAO;
import web.com.models.Video;

public class VideoService {
    private final VideoDAO dao = new VideoDAO();
    public Video get(int id) { return dao.get(id); }
    public List<Video> getAll() { return dao.getAll(); }
    public List<Video> getPage(int page, int size) { return dao.getPage(page, size); }
    public long countActive() { return dao.countActive(); }
    public void save(Video video) { dao.save(video); }
    public boolean delete(int id) { return dao.delete(id); }

    public String validate(Video video) {
        if (video.getTitle().isBlank() || video.getTitle().length() > 255)
            return "Tiêu đề là bắt buộc và không được quá 255 ký tự.";
        if (video.getDescription().length() > 2000) return "Mô tả không được quá 2000 ký tự.";
        if (video.getVideoUrl() == null || video.getVideoUrl().isBlank()) return "Vui lòng chọn tệp video MP4.";
        if (video.getThumbnailUrl() != null && !video.getThumbnailUrl().isEmpty() && !validUrl(video.getThumbnailUrl())
                && new VideoThumbnailStorage().resolve(video.getThumbnailUrl()) == null)
            return "URL ảnh đại diện phải là URL HTTP hoặc HTTPS hợp lệ.";
        return null;
    }
    private boolean validUrl(String value) {
        if (value.length() > 2048) return false;
        try {
            URI uri = URI.create(value);
            return ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                    && uri.getHost() != null && uri.getUserInfo() == null;
        } catch (IllegalArgumentException e) { return false; }
    }
}
