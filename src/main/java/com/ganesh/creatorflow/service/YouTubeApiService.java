package com.ganesh.creatorflow.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class YouTubeApiService {

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
}