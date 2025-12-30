package com.example.gympool.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "blogs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(name = "cover_image_path")
    private String coverImagePath;

    @Column(name = "publish_date", nullable = false)
    private LocalDateTime publishDate;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false;

    @PrePersist
    protected void onCreate() {
        if (publishDate == null) {
            publishDate = LocalDateTime.now();
        }
        if (isPinned == null) {
            isPinned = false;
        }
    }
}
