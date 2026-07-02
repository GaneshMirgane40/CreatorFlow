package com.ganesh.creatorflow.service;

import com.ganesh.creatorflow.dto.ProjectRequest;
import com.ganesh.creatorflow.dto.ProjectResponse;
import com.ganesh.creatorflow.entity.Project;
import com.ganesh.creatorflow.entity.User;
import com.ganesh.creatorflow.enums.ProjectStatus;
import com.ganesh.creatorflow.exception.ProjectNotFoundException;
import com.ganesh.creatorflow.repository.ProjectRepository;
import com.ganesh.creatorflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import com.ganesh.creatorflow.enums.Role;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectResponse createProject(ProjectRequest request, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .creator(creator)
                .status(ProjectStatus.PENDING)
                .build();

        Project savedProject = projectRepository.save(project);

        return ProjectResponse.builder()
                .id(savedProject.getId())
                .title(savedProject.getTitle())
                .description(savedProject.getDescription())
                .status(savedProject.getStatus().name())
                .creatorEmail(savedProject.getCreator().getEmail())
                .assignedEditorEmail(savedProject.getAssignedEditor() != null ? 
                        savedProject.getAssignedEditor().getEmail() : null)
                .createdAt(savedProject.getCreatedAt())
                .build();
    }

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(this::convertToProjectResponse)
                .toList();
    }

    public List<ProjectResponse> getProjectsPaginated(int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return projectRepository.findAll(pageable)
                .stream()
                .map(this::convertToProjectResponse)
                .toList();
    }

    public List<ProjectResponse> searchProjects(String keyword) {
        return projectRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(this::convertToProjectResponse)
                .toList();
    }

    private ProjectResponse convertToProjectResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .status(project.getStatus().name())
                .creatorEmail(project.getCreator().getEmail())
                .assignedEditorEmail(project.getAssignedEditor() != null ? 
                        project.getAssignedEditor().getEmail() : null)
                .createdAt(project.getCreatedAt())
                .build();
    }
    public ProjectResponse getProjectById(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ProjectNotFoundException("Project not found"));

        return convertToProjectResponse(project);
    }
    public ProjectResponse assignEditor(Long projectId, Long editorId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));

        User editor = userRepository.findById(editorId)
                .orElseThrow(() -> new RuntimeException("Editor not found"));

        if (editor.getRole() != Role.EDITOR) {
            throw new RuntimeException("User is not an editor");
        }

        project.setAssignedEditor(editor);

        Project savedProject = projectRepository.save(project);

        return convertToProjectResponse(savedProject);
    }
    public ProjectResponse updateProjectStatus(
            Long projectId,
            ProjectStatus status
    )
    {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ProjectNotFoundException("Project not found"));

        project.setStatus(status);

        Project savedProject = projectRepository.save(project);

        return convertToProjectResponse(savedProject);
    }
    public List<ProjectResponse> getProjectsForEditor(String email) {

        return projectRepository
                .findByAssignedEditorEmail(email)
                .stream()
                .map(this::convertToProjectResponse)
                .toList();
    }

    public List<ProjectResponse> getProjectsForCreator(String creatorEmail) {

        return projectRepository
                .findByCreatorEmail(creatorEmail)
                .stream()
                .map(this::convertToProjectResponse)
                .toList();
    }

    public List<ProjectResponse> filterProjectsByStatus(ProjectStatus status) {

        return projectRepository
                .findByStatus(status)
                .stream()
                .map(this::convertToProjectResponse)
                .toList();
    }

}
