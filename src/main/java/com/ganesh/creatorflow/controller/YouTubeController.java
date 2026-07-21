package com.ganesh.creatorflow.controller;

import com.ganesh.creatorflow.dto.youtube.YouTubeVideoRequest;
import com.ganesh.creatorflow.dto.youtube.YouTubeVideoResponse;
import com.ganesh.creatorflow.service.ProjectYouTubeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class YouTubeController {

    private final ProjectYouTubeService projectYouTubeService;

    @PostMapping("/{projectId}/youtube")
    public ResponseEntity<YouTubeVideoResponse> addVideoToProject(
            @PathVariable Long projectId,
            @Valid @RequestBody YouTubeVideoRequest request,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        YouTubeVideoResponse response = projectYouTubeService.addVideoToProject(
                projectId,
                request,
                userEmail
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
