package web.com.models;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Video")
@Getter @Setter @NoArgsConstructor
public class Video {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false, length = 255)
    private String title;
    @Column(length = 2000)
    private String description;
    @Column(name = "video_url", nullable = false, length = 2048)
    private String videoUrl;
    @Column(name = "thumbnail_url", length = 2048)
    private String thumbnailUrl;
    @Column(nullable = false)
    private boolean active = true;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
