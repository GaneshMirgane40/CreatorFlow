package com.ganesh.creatorflow.controller;

import com.ganesh.creatorflow.dto.PublishResponse;
import com.ganesh.creatorflow.service.PublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class PublishController {

    private final PublishService publishService;

    @PostMapping("/{projectId}/publish")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<PublishResponse> publishProject(
            @PathVariable Long projectId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                publishService.publishProject(projectId, authentication.getName())
        );
    }
}
