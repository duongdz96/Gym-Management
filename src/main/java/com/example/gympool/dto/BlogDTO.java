package com.example.gympool.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogDTO {
    private Long id;
    private String title;
    private String content;
    private String coverImagePath;
    private LocalDateTime publishDate;
    private Long authorId;
    private String authorName;
    private Boolean isPinned;
}
