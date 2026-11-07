package com.ganesh.creatorflow.service.impl;

import com.ganesh.creatorflow.dto.SubmissionResponse;
import com.ganesh.creatorflow.entity.Project;
import com.ganesh.creatorflow.entity.ProjectSubmission;
import com.ganesh.creatorflow.entity.User;
import com.ganesh.creatorflow.enums.ActivityType;
import com.ganesh.creatorflow.enums.ProjectStatus;
import com.ganesh.creatorflow.exception.ProjectNotFoundException;
import com.ganesh.creatorflow.repository.ProjectRepository;
import com.ganesh.creatorflow.repository.ProjectSubmissionRepository;
import com.ganesh.creatorflow.repository.UserRepository;
import com.ganesh.creatorflow.service.ActivityService;
import com.ganesh.creatorflow.service.NotificationService;
import com.ganesh.creatorflow.service.ProjectSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectSubmissionServiceImpl implements ProjectSubmissionService {

    private final ProjectRepository projectRepository;
    private final ProjectSubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ActivityService activityService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public SubmissionResponse uploadSubmission(Long projectId,
                                               MultipartFile video,
                                               String editorEmail) throws Exception {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));

        User editor = userRepository.findByEmail(editorEmail)
                .orElseThrow(() -> new RuntimeException("Editor not found"));

        if (project.getAssignedEditor() == null ||
                !project.getAssignedEditor().getId().equals(editor.getId())) {
            throw new AccessDeniedException("You are not assigned to this project.");
        }

        if (project.getStatus() == ProjectStatus.APPROVED
                || project.getStatus() == ProjectStatus.PUBLISHED) {
            throw new IllegalStateException(
                    "This project is already " + project.getStatus().name().toLowerCase()
                            + " and no longer accepts submissions.");
        }

        if (video == null || video.isEmpty()) {
            throw new IllegalArgumentException("Please upload a video.");
        }

        String contentType = video.getContentType();
        if (contentType != null && !contentType.startsWith("video/")) {
            throw new IllegalArgumentException("Only video files can be submitted.");
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID() + "_" + sanitizeFileName(video.getOriginalFilename());
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(video.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        int nextVersion = submissionRepository
                .findTopByProjectOrderByVersionDesc(project)
                .map(latest -> latest.getVersion() + 1)
                .orElse(1);

        ProjectSubmission submission = ProjectSubmission.builder()
                .project(project)
                .version(nextVersion)
                .videoPath(filePath.toString())
                .uploadedBy(editor)
                .build();
        submission = submissionRepository.save(submission);

        project.setStatus(ProjectStatus.UNDER_REVIEW);
        projectRepository.save(project);

        activityService.logActivity(
                project,
                editor,
                ActivityType.SUBMITTED_FOR_REVIEW,
                "Submitted version " + nextVersion + " of \"" + project.getTitle() + "\" for review."
        );

        if (project.getCreator() != null) {
            notificationService.createNotification(
                    project.getCreator(),
                    project,
                    "Project Submitted",
                    "Your project \"" + project.getTitle() + "\" has been submitted for review."
            );
        }

        return toResponse(submission, "Submission uploaded successfully.");
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionResponse getLatestSubmission(Long projectId, String userEmail) {
        Project project = findProjectForParticipant(projectId, userEmail);

        return submissionRepository
                .findTopByProjectOrderByVersionDesc(project)
                .map(submission -> toResponse(submission, "Latest submission fetched successfully."))
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionVideo getSubmissionVideo(Long projectId, Long submissionId, String userEmail) {
        Project project = findProjectForParticipant(projectId, userEmail);

        ProjectSubmission submission = submissionRepository.findById(submissionId)
                .filter(s -> s.getProject().getId().equals(project.getId()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Submission not found"));

        Path path = Paths.get(submission.getVideoPath());
        if (!Files.isRegularFile(path) || !Files.isReadable(path)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "The submitted video file is missing on the server.");
        }

        String fileName = displayFileName(submission);
        String contentType = MediaTypeFactory.getMediaType(fileName)
                .map(MediaType::toString)
                .orElse("video/mp4");

        return new SubmissionVideo(new FileSystemResource(path), fileName, contentType);
    }

    /** Only the project's creator and its assigned editor may see submissions. */
    private Project findProjectForParticipant(Long projectId, String userEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));

        boolean isCreator = project.getCreator() != null
                && project.getCreator().getEmail().equals(userEmail);
        boolean isEditor = project.getAssignedEditor() != null
                && project.getAssignedEditor().getEmail().equals(userEmail);

        if (!isCreator && !isEditor) {
            throw new AccessDeniedException("You do not have access to this project's submissions.");
        }
        return project;
    }

    private SubmissionResponse toResponse(ProjectSubmission submission, String message) {
        User uploader = submission.getUploadedBy();
        Long projectId = submission.getProject().getId();

        return SubmissionResponse.builder()
                .submissionId(submission.getId())
                .version(submission.getVersion())
                .fileName(displayFileName(submission))
                .videoUrl("/projects/" + projectId + "/submissions/" + submission.getId() + "/video")
                .uploadedBy(uploader != null ? uploader.getName() : null)
                .uploadedByEmail(uploader != null ? uploader.getEmail() : null)
                .uploadedAt(submission.getUploadedAt())
                .message(message)
                .build();
    }

    /** Stored files are named "<uuid>_<original name>"; strip the UUID for display/download. */
    private static String displayFileName(ProjectSubmission submission) {
        String stored = Paths.get(submission.getVideoPath()).getFileName().toString();
        int separator = stored.indexOf('_');
        return separator >= 0 && separator < stored.length() - 1
                ? stored.substring(separator + 1)
                : stored;
    }

    private static String sanitizeFileName(String originalFilename) {
        String name = StringUtils.getFilename(StringUtils.cleanPath(
                originalFilename == null ? "" : originalFilename.replace('\\', '/')));
        if (name == null || name.isBlank()) {
            return "video.mp4";
        }
        return name.replaceAll("[^A-Za-z0-9._ -]", "_");
    }
}
