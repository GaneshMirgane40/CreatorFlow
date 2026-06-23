package com.ganesh.creatorflow.controller;

import com.ganesh.creatorflow.dto.ProjectRequest;
import com.ganesh.creatorflow.dto.ProjectResponse;
import com.ganesh.creatorflow.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @RequestBody ProjectRequest request,
            Authentication authentication
    ) {
        String creatorEmail = authentication.getName();
        ProjectResponse response = projectService.createProject(request, creatorEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
