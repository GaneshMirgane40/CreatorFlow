package com.ganesh.creatorflow.controller;

import com.ganesh.creatorflow.dto.ProjectRequest;
import com.ganesh.creatorflow.dto.ProjectResponse;
import com.ganesh.creatorflow.dto.UpdateProjectRequest;
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
import com.ganesh.creatorflow.enums.ProjectStatus;
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
    @GetMapping("/filter")
    public ResponseEntity<List<ProjectResponse>> filterProjectsByStatus(
            @RequestParam ProjectStatus status
    ) {
        return ResponseEntity.ok(
                projectService.filterProjectsByStatus(status)
        );
    }
    @GetMapping("/search-filter")
    public ResponseEntity<List<ProjectResponse>> searchAndFilterProjects(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ProjectStatus status
    ) {
        return ResponseEntity.ok(
                projectService.searchAndFilterProjects(keyword, status)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                projectService.getProjectById(id)
        );
    }
    @PutMapping("/{projectId}/status")
//    @PreAuthorize("hasAnyRole('EDITOR','CREATOR')")
    public ResponseEntity<ProjectResponse> updateStatus(
            @PathVariable Long projectId,
            @RequestBody UpdateProjectStatusRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                projectService.updateProjectStatus(
                        projectId,
                        request.getStatus(),
                        authentication.getName()
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

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProjectRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                projectService.updateProject(
                        id,
                        request,
                        authentication.getName()
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long id,
            Authentication authentication
    ) {
        projectService.deleteProject(
                id,
                authentication.getName()
        );
        return ResponseEntity.noContent().build();
    }

}
