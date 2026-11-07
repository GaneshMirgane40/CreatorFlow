package com.ganesh.creatorflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {

    private Long id;

    private String title;

    private String description;

    private String status;

    private String creatorEmail;

    private String creatorName;

    private Long assignedEditorId;

    private String assignedEditorEmail;

    private String assignedEditorName;

    private LocalDateTime createdAt;

    // Persisted YouTube metadata (null until the project is published)
    private String youtubeVideoId;

    private String youtubeUrl;

    private String thumbnailUrl;

    private String channelTitle;

    private LocalDateTime publishedAt;
}
