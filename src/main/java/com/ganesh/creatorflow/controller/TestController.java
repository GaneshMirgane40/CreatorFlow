package com.ganesh.creatorflow.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;
import com.ganesh.creatorflow.service.YouTubeService;

import org.springframework.web.bind.annotation.RequestParam;
@RestController
@RequiredArgsConstructor

public class TestController {

    @GetMapping("/api/test/secure")
    public String secureEndpoint() {
        return "JWT Authentication Working!";
    }
    private final YouTubeService youTubeApiService;

    @GetMapping("/youtube")
    public JsonNode testYoutube(@RequestParam String url) {

        String videoId = youTubeApiService.extractVideoId(url);

        return youTubeApiService.fetchVideoDetails(videoId);
    }
}