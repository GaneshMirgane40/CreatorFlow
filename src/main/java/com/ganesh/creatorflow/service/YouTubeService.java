package com.ganesh.creatorflow.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.ganesh.creatorflow.dto.YouTubeUploadResult;
import com.ganesh.creatorflow.entity.ProjectSubmission;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class YouTubeService {

    private final RestTemplate restTemplate;

    @Value("${youtube.api.key}")
    private String apiKey;

    private static final String BASE_URL =
            "https://www.googleapis.com/youtube/v3/videos";

    public String extractVideoId(String url) {

        if (url.contains("youtube.com/watch?v=")) {
            return url.substring(url.indexOf("v=") + 2).split("&")[0];
        }

        if (url.contains("youtu.be/")) {
            return url.substring(url.lastIndexOf("/") + 1);
        }

        throw new RuntimeException("Invalid YouTube URL");
    }

    public JsonNode fetchVideoDetails(String videoId) {

        String url = BASE_URL
                + "?part=snippet,statistics,contentDetails"
                + "&id=" + videoId
                + "&key=" + apiKey;

        return restTemplate.getForObject(url, JsonNode.class);
    }

    public YouTubeUploadResult uploadVideo(
            String accessToken,
            ProjectSubmission submission
    ) {
        Path videoPath = Paths.get(submission.getVideoPath());
        if (!Files.isRegularFile(videoPath) || !Files.isReadable(videoPath)) {
            throw new IllegalStateException("The submitted video file is missing or unreadable.");
        }

        try {
            YouTube youtube = new YouTube.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    request -> request.getHeaders().setAuthorization("Bearer " + accessToken)
            ).setApplicationName("CreatorFlow").build();

            Video video = new Video()
                    .setSnippet(new VideoSnippet()
                            .setTitle(submission.getProject().getTitle())
                            .setDescription(submission.getProject().getDescription()))
                    .setStatus(new VideoStatus().setPrivacyStatus("private"));

            File file = videoPath.toFile();
            String contentType = Files.probeContentType(videoPath);
            FileContent mediaContent = new FileContent(
                    contentType != null ? contentType : "video/mp4",
                    file
            );

            Video uploadedVideo = youtube.videos()
                    .insert(java.util.List.of("snippet", "status"), video, mediaContent)
                    .execute();

            if (uploadedVideo.getId() == null || uploadedVideo.getId().isBlank()) {
                throw new IllegalStateException("YouTube did not return an uploaded video ID.");
            }

            String videoId = uploadedVideo.getId();
            return new YouTubeUploadResult(videoId, "https://www.youtube.com/watch?v=" + videoId);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to upload the video to YouTube.", exception);
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to initialize the YouTube upload client.", exception);
        }
    }
}
