package com.ganesh.creatorflow.service;

import com.ganesh.creatorflow.dto.ProjectRequest;
import com.ganesh.creatorflow.dto.ProjectResponse;
import com.ganesh.creatorflow.dto.UpdateProjectRequest;
import com.ganesh.creatorflow.entity.Project;
import com.ganesh.creatorflow.entity.User;
import com.ganesh.creatorflow.enums.ActivityType;
import com.ganesh.creatorflow.enums.ProjectStatus;
import com.ganesh.creatorflow.exception.ProjectNotFoundException;
import com.ganesh.creatorflow.repository.ProjectRepository;
import com.ganesh.creatorflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import com.ganesh.creatorflow.specification.ProjectSpecification;
import org.springframework.stereotype.Service;

import java.util.List;

import com.ganesh.creatorflow.enums.Role;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ActivityService activityService;

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

        activityService.logActivity(
                savedProject,
                creator,
                ActivityType.PROJECT_CREATED,
                "Project \"" + project.getTitle() + "\" was created."
        );

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
    public ProjectResponse assignEditor(
            Long projectId,
            Long editorId,
            String creatorEmail
    ) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));

        // ✅ Ownership validation
        if (!project.getCreator().getEmail().equals(creatorEmail)) {
            throw new RuntimeException("You are not authorized to modify this project");
        }

        User creator = project.getCreator();

        User editor = userRepository.findById(editorId)
                .orElseThrow(() -> new RuntimeException("Editor not found"));

        if (editor.getRole() != Role.EDITOR) {
            throw new RuntimeException("User is not an editor");
        }

        project.setAssignedEditor(editor);

        Project savedProject = projectRepository.save(project);

        activityService.logActivity(
                savedProject,
                creator,
                ActivityType.EDITOR_ASSIGNED,
                "Assigned editor " + editor.getName() + " to the project."
        );

        return convertToProjectResponse(savedProject);
    }
    public ProjectResponse updateProjectStatus(
            Long projectId,
            ProjectStatus status,
            String userEmail
    )
    {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ProjectNotFoundException("Project not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        project.setStatus(status);

        Project savedProject = projectRepository.save(project);

        switch (status) {
            case IN_PROGRESS -> activityService.logActivity(
                    savedProject,
                    user,
                    ActivityType.WORK_STARTED,
                    "Started working on \"" + savedProject.getTitle() + "\"."
            );
            case REVIEW -> activityService.logActivity(
                    savedProject,
                    user,
                    ActivityType.SUBMITTED_FOR_REVIEW,
                    "Submitted \"" + savedProject.getTitle() + "\" for review."

            );
            case APPROVED -> activityService.logActivity(
                    savedProject,
                    user,
                    ActivityType.APPROVED,
                    "Approved \"" + savedProject.getTitle() + "\"."
            );
            case PUBLISHED -> activityService.logActivity(
                    savedProject,
                    user,
                    ActivityType.PUBLISHED,
                    "Published \"" + savedProject.getTitle() + "\"."
            );
            default -> {
            }
        }

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

    public List<ProjectResponse> searchAndFilterProjects(String keyword, ProjectStatus status) {

        Specification<Project> specification = Specification
                .where(ProjectSpecification.hasKeyword(keyword))
                .and(ProjectSpecification.hasStatus(status));

        return projectRepository.findAll(specification)
                .stream()
                .map(this::convertToProjectResponse)
                .toList();
    }

    public ProjectResponse updateProject(
            Long projectId,
            UpdateProjectRequest request,
            String creatorEmail
    ) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));

        if (!project.getCreator().getEmail().equals(creatorEmail)) {
            throw new RuntimeException("You are not authorized to update this project");
        }

        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());

        Project savedProject = projectRepository.save(project);

        return convertToProjectResponse(savedProject);
    }

    public void deleteProject(
            Long projectId,
            String creatorEmail
    ) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));

        if (!project.getCreator().getEmail().equals(creatorEmail)) {
            throw new RuntimeException("You are not authorized to delete this project");
        }

        projectRepository.delete(project);
    }

}
