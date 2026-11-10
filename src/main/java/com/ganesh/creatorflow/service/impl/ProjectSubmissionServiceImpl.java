package com.ganesh.creatorflow.service.impl;

import com.ganesh.creatorflow.dto.SubmissionResponse;
import com.ganesh.creatorflow.exception.ProjectNotFoundException;
import com.ganesh.creatorflow.service.ProjectSubmissionService;
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



import com.ganesh.creatorflow.entity.Project;
import com.ganesh.creatorflow.entity.ProjectSubmission;
import com.ganesh.creatorflow.entity.User;
import com.ganesh.creatorflow.enums.ProjectStatus;
import com.ganesh.creatorflow.repository.ProjectRepository;
import com.ganesh.creatorflow.repository.ProjectSubmissionRepository;
import com.ganesh.creatorflow.repository.UserRepository;
import com.ganesh.creatorflow.service.NotificationService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public SubmissionResponse uploadSubmission(Long projectId,
                                               MultipartFile video,
                                               String editorEmail) throws Exception {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User editor = userRepository.findByEmail(editorEmail)
                .orElseThrow(() -> new RuntimeException("Editor not found"));
        if (project.getAssignedEditor() == null ||
                !project.getAssignedEditor().getId().equals(editor.getId())) {

            throw new RuntimeException("You are not assigned to this project.");
        }
        if (video.isEmpty()) {
            throw new RuntimeException("Please upload a video.");
        }
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID() + "_" + video.getOriginalFilename();

        Path filePath = uploadPath.resolve(fileName);

        Files.copy(video.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        ProjectSubmission latestSubmission = submissionRepository
                .findTopByProjectOrderByVersionDesc(project)
                .orElse(null);

        int nextVersion = latestSubmission == null
                ? 1
                : latestSubmission.getVersion() + 1;
        ProjectSubmission submission = ProjectSubmission.builder()
                .project(project)
                .version(nextVersion)
                .videoPath(filePath.toString())
                .uploadedBy(editor)
                .build();
        submission = submissionRepository.save(submission);
        project.setStatus(ProjectStatus.UNDER_REVIEW);

        projectRepository.save(project);

        if (project.getCreator() != null) {
            notificationService.createNotification(
                    project.getCreator(),
                    project,
                    "Project Submitted",
                    "Your project \"" + project.getTitle() + "\" has been submitted for review."
            );
        }

        return SubmissionResponse.builder()
                .submissionId(submission.getId())
                .version(submission.getVersion())
                .videoPath(submission.getVideoPath())
                .message("Submission uploaded successfully.")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionResponse getLatestSubmission(Long projectId, String userEmail) {
        Project project = findProjectForParticipant(projectId, userEmail);
        return submissionRepository.findTopByProjectOrderByVersionDesc(project)
                .map(submission -> toResponse(submission, "Latest submission fetched successfully."))
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionVideo getSubmissionVideo(Long projectId, Long submissionId, String userEmail) {
        Project project = findProjectForParticipant(projectId, userEmail);
        ProjectSubmission submission = submissionRepository.findById(submissionId)
                .filter(value -> value.getProject().getId().equals(project.getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found"));

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

    private Project findProjectForParticipant(Long projectId, String userEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        boolean isCreator = project.getCreator() != null && project.getCreator().getEmail().equals(userEmail);
        boolean isEditor = project.getAssignedEditor() != null
                && project.getAssignedEditor().getEmail().equals(userEmail);
        if (!isCreator && !isEditor) {
            throw new AccessDeniedException("You do not have access to this project's submissions.");
        }
        return project;
    }

    private SubmissionResponse toResponse(ProjectSubmission submission, String message) {
        User uploader = submission.getUploadedBy();
        return SubmissionResponse.builder()
                .submissionId(submission.getId())
                .version(submission.getVersion())
                .videoPath(submission.getVideoPath())
                .message(message)
                .build();
    }

    private static String displayFileName(ProjectSubmission submission) {
        String stored = Paths.get(submission.getVideoPath()).getFileName().toString();
        int separator = stored.indexOf('_');
        return separator >= 0 && separator < stored.length() - 1
                ? stored.substring(separator + 1)
                : stored;
    }
}
