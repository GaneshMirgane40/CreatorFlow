package com.ganesh.creatorflow.dto.youtube;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YouTubeVideoRequest {

    @NotBlank(message = "YouTube URL is required")
    private String youtubeUrl;
}