package com.ganesh.creatorflow.service.impl;

import com.ganesh.creatorflow.dto.SubmissionResponse;
import com.ganesh.creatorflow.service.ProjectSubmissionService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;



import com.ganesh.creatorflow.entity.Project;
import com.ganesh.creatorflow.entity.ProjectSubmission;
import com.ganesh.creatorflow.entity.User;
import com.ganesh.creatorflow.enums.ProjectStatus;
import com.ganesh.creatorflow.repository.ProjectRepository;
import com.ganesh.creatorflow.repository.ProjectSubmissionRepository;
import com.ganesh.creatorflow.repository.UserRepository;

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
        return SubmissionResponse.builder()
                .submissionId(submission.getId())
                .version(submission.getVersion())
                .videoPath(submission.getVideoPath())
                .message("Submission uploaded successfully.")
                .build();
    }
}
