package com.ganesh.creatorflow.dto.youtube;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YouTubeVideoResponse {

    private Long id;

    private String videoId;

    private String youtubeUrl;

    private String title;

    private String description;

    private String channelTitle;

    private String thumbnailUrl;

    private String duration;

    private Long viewCount;

    private LocalDateTime publishedAt;
}