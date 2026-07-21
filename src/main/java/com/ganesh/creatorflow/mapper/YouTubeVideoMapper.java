package com.ganesh.creatorflow.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.ganesh.creatorflow.entity.Project;
import com.ganesh.creatorflow.entity.YouTubeVideo;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

/**
 * Maps YouTube Data API v3 video responses to {@link YouTubeVideo} entities.
 */
@Component
public class YouTubeVideoMapper {

    public YouTubeVideo map(JsonNode response, String youtubeUrl, Project project) {
        JsonNode video = firstVideo(response);
        JsonNode snippet = child(video, "snippet");
        JsonNode statistics = child(video, "statistics");
        JsonNode contentDetails = child(video, "contentDetails");

        return YouTubeVideo.builder()
                .videoId(text(video, "id"))
                .youtubeUrl(youtubeUrl)
                .title(text(snippet, "title"))
                .description(text(snippet, "description"))
                .channelTitle(text(snippet, "channelTitle"))
                .thumbnailUrl(extractThumbnail(child(snippet, "thumbnails")))
                .duration(text(contentDetails, "duration"))
                .viewCount(toLong(text(statistics, "viewCount")))
                .publishedAt(toLocalDateTime(text(snippet, "publishedAt")))
                .project(project)
                .build();
    }

    private String extractThumbnail(JsonNode thumbnails) {
        for (String quality : new String[]{"maxres", "high", "medium", "default"}) {
            String url = text(child(thumbnails, quality), "url");
            if (url != null && !url.isBlank()) {
                return url;
            }
        }
        return null;
    }

    private JsonNode firstVideo(JsonNode response) {

        if (response == null || response.isNull()) {
            throw new RuntimeException("Empty response from YouTube API");
        }

        JsonNode items = response.path("items");

        if (!items.isArray() || items.isEmpty()) {
            throw new RuntimeException("Video not found on YouTube");
        }

        return items.get(0);
    }

    private JsonNode child(JsonNode parent, String fieldName) {
        if (parent == null || parent.isNull() || parent.isMissingNode()) {
            return null;
        }
        JsonNode child = parent.path(fieldName);
        return child == null || child.isNull() ? null : child;
    }

    private String text(JsonNode parent, String fieldName) {
        JsonNode value = child(parent, fieldName);
        return value == null || !value.isValueNode() ? null : value.asText();
    }

    private Long toLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Long.valueOf(value);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private LocalDateTime toLocalDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Instant.parse(value).atOffset(ZoneOffset.UTC).toLocalDateTime();
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }
}
