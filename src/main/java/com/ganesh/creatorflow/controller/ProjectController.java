package com.ganesh.creatorflow.controller;

import com.ganesh.creatorflow.dto.ProjectRequest;
import com.ganesh.creatorflow.dto.ProjectResponse;
import com.ganesh.creatorflow.dto.UpdateProjectStatusRequest;
import com.ganesh.creatorflow.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication
    ){
        String creatorEmail = authentication.getName();
        ProjectResponse response = projectService.createProject(request, creatorEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/page")
    public ResponseEntity<List<ProjectResponse>> getProjectsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        List<ProjectResponse> projects = projectService.getProjectsPaginated(page, size, sortBy, direction);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProjectResponse>> searchProjects(
            @RequestParam String keyword
    ) {
        List<ProjectResponse> projects = projectService.searchProjects(keyword);
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
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<ProjectResponse> assignEditor(
            @PathVariable Long projectId,
            @PathVariable Long editorId
    ) {
        return ResponseEntity.ok(
                projectService.assignEditor(projectId, editorId)
        );
    }
    @PutMapping("/{projectId}/status")
    @PreAuthorize("hasRole('EDITOR')")
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
    @GetMapping("/editor")
    @PreAuthorize("hasRole('EDITOR')")
    public ResponseEntity<List<ProjectResponse>> getEditorProjects(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                projectService.getProjectsForEditor(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/my-projects")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<List<ProjectResponse>> getMyProjects(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                projectService.getProjectsForCreator(
                        authentication.getName()
                )
        );
    }

}
