package com.ganesh.creatorflow.controller;

import com.ganesh.creatorflow.dto.YouTubeConnectRequest;
import com.ganesh.creatorflow.service.YouTubeOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/youtube/oauth")
@RequiredArgsConstructor
public class YouTubeOAuthController {

    private final YouTubeOAuthService youTubeOAuthService;

    @GetMapping("/connect")
    public String getAuthorizationUrl() {

        return youTubeOAuthService.generateAuthorizationUrl();

    }

    @PostMapping("/connect")
    public String connectYouTube(
            @RequestBody YouTubeConnectRequest request,
            Authentication authentication
    ) throws Exception {

        String creatorEmail = authentication.getName();

        youTubeOAuthService.connectAccount(
                request.getCode(),
                creatorEmail
        );

        return "YouTube connected successfully.";

    }
}