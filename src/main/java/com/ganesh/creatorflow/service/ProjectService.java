package com.ganesh.creatorflow.service;

import com.ganesh.creatorflow.dto.ProjectRequest;
import com.ganesh.creatorflow.dto.ProjectResponse;
import com.ganesh.creatorflow.dto.UpdateProjectRequest;
import com.ganesh.creatorflow.entity.Project;
import com.ganesh.creatorflow.entity.User;
import com.ganesh.creatorflow.entity.YouTubeVideo;
import org.springframework.security.access.AccessDeniedException;
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
    private final NotificationService notificationService;

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

        return convertToProjectResponse(savedProject);
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
        User creator = project.getCreator();
        User editor = project.getAssignedEditor();
        YouTubeVideo video = project.getYoutubeVideo();

        return ProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .status(project.getStatus().name())
                .creatorEmail(creator != null ? creator.getEmail() : null)
                .creatorName(creator != null ? creator.getName() : null)
                .assignedEditorId(editor != null ? editor.getId() : null)
                .assignedEditorEmail(editor != null ? editor.getEmail() : null)
                .assignedEditorName(editor != null ? editor.getName() : null)
                .createdAt(project.getCreatedAt())
                .youtubeVideoId(video != null ? video.getVideoId() : null)
                .youtubeUrl(video != null ? video.getYoutubeUrl() : null)
                .thumbnailUrl(video != null ? video.getThumbnailUrl() : null)
                .channelTitle(video != null ? video.getChannelTitle() : null)
                .publishedAt(video != null ? video.getPublishedAt() : null)
                .build();
    }

    private static boolean isParticipant(Project project, String email) {
        return (project.getCreator() != null && project.getCreator().getEmail().equals(email))
                || (project.getAssignedEditor() != null && project.getAssignedEditor().getEmail().equals(email));
    }

    public ProjectResponse getProjectById(Long id, String userEmail) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ProjectNotFoundException("Project not found"));

        if (!isParticipant(project, userEmail)) {
            throw new AccessDeniedException("You do not have access to this project.");
        }

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
                "Assigned " + editor.getName() + " to \"" + savedProject.getTitle() + "\"."
        );

        if (savedProject.getAssignedEditor() != null) {
            notificationService.createNotification(
                    savedProject.getAssignedEditor(),
                    savedProject,
                    "Project Assigned",
                    "You have been assigned to project \"" + savedProject.getTitle() + "\"."
            );
        }

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

        validateStatusChange(project, status, user);

        project.setStatus(status);

        Project savedProject = projectRepository.save(project);

        switch (status) {
            case IN_PROGRESS -> activityService.logActivity(
                    savedProject,
                    user,
                    ActivityType.WORK_STARTED,
                    "Started working on \"" + savedProject.getTitle() + "\"."
            );
            case REVIEW -> {
                activityService.logActivity(
                        savedProject,
                        user,
                        ActivityType.SUBMITTED_FOR_REVIEW,
                        "Submitted \"" + savedProject.getTitle() + "\" for review."
                );
                if (savedProject.getCreator() != null) {
                    notificationService.createNotification(
                            savedProject.getCreator(),
                            savedProject,
                            "Project Submitted",
                            "Your project \"" + savedProject.getTitle() + "\" has been submitted for review."
                    );
                }
            }
            case APPROVED -> activityService.logActivity(
                    savedProject,
                    user,
                    ActivityType.APPROVED,
                    "Approved \"" + savedProject.getTitle() + "\"."
            );
            case PUBLISHED -> {
                activityService.logActivity(
                        savedProject,
                        user,
                        ActivityType.PUBLISHED,
                        "Published \"" + savedProject.getTitle() + "\"."
                );
                if (savedProject.getCreator() != null) {
                    notificationService.createNotification(
                            savedProject.getCreator(),
                            savedProject,
                            "Project Published",
                            "Your project \"" + savedProject.getTitle() + "\" has been published successfully."
                    );
                }
            }
            default -> {
            }
        }

        return convertToProjectResponse(savedProject);
    }
    /**
     * Direct status changes are limited to starting work. Submitting, approving,
     * requesting changes and publishing all go through their dedicated endpoints
     * (submissions, feedback, publish) so the workflow can't be skipped.
     */
    private void validateStatusChange(Project project, ProjectStatus target, User user) {
        if (!isParticipant(project, user.getEmail())) {
            throw new AccessDeniedException("You are not part of this project.");
        }
        if (target == null) {
            throw new IllegalArgumentException("Status is required.");
        }

        boolean isEditor = project.getAssignedEditor() != null
                && project.getAssignedEditor().getId().equals(user.getId());

        if (target != ProjectStatus.IN_PROGRESS || !isEditor) {
            throw new IllegalStateException(
                    "Status " + target + " can't be set directly. Use the upload, review or publish actions.");
        }

        ProjectStatus current = project.getStatus();
        if (current != ProjectStatus.PENDING && current != ProjectStatus.REVIEW_REQUESTED) {
            throw new IllegalStateException(
                    "Work can only be started on pending projects or projects with requested changes.");
        }
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
