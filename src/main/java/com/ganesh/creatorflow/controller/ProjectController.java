package com.ganesh.creatorflow.controller;

import com.ganesh.creatorflow.dto.ProjectRequest;
import com.ganesh.creatorflow.dto.ProjectResponse;
import com.ganesh.creatorflow.dto.UpdateProjectStatusRequest;
import com.ganesh.creatorflow.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                projectService.getProjectById(id)
        );
    }
    @PutMapping("/{projectId}/assign-editor/{editorId}")
    public ResponseEntity<ProjectResponse> assignEditor(
            @PathVariable Long projectId,
            @PathVariable Long editorId
    ) {
        return ResponseEntity.ok(
                projectService.assignEditor(projectId, editorId)
        );
    }
    @PutMapping("/{projectId}/status")
    public ResponseEntity<ProjectResponse> updateStatus(
            @PathVariable Long projectId,
            @RequestBody UpdateProjectStatusRequest request
    ) {
        return ResponseEntity.ok(
                projectService.updateProjectStatus(
                        projectId,
                        request.getStatus()
                )
        );
    }

}
