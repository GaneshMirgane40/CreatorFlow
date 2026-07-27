package com.ganesh.creatorflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublishResponse {

    private Long projectId;
    private String videoId;
    private String youtubeUrl;
    private LocalDateTime publishedAt;
    private String message;
}
