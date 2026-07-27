package com.ganesh.creatorflow.service;

import com.ganesh.creatorflow.dto.PublishResponse;
import com.ganesh.creatorflow.dto.YouTubeUploadResult;
import com.ganesh.creatorflow.entity.Project;
import com.ganesh.creatorflow.entity.ProjectSubmission;
import com.ganesh.creatorflow.entity.User;
import com.ganesh.creatorflow.entity.YouTubeAccount;
import com.ganesh.creatorflow.entity.YouTubeVideo;
import com.ganesh.creatorflow.enums.ActivityType;
import com.ganesh.creatorflow.enums.ProjectStatus;
import com.ganesh.creatorflow.exception.ProjectNotFoundException;
import com.ganesh.creatorflow.repository.ProjectRepository;
import com.ganesh.creatorflow.repository.ProjectSubmissionRepository;
import com.ganesh.creatorflow.repository.UserRepository;
import com.ganesh.creatorflow.repository.YouTubeAccountRepository;
import com.ganesh.creatorflow.repository.YouTubeVideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PublishService {

    private final ProjectRepository projectRepository;
    private final ProjectSubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final YouTubeAccountRepository youTubeAccountRepository;
    private final YouTubeVideoRepository youTubeVideoRepository;
    private final YouTubeOAuthService youTubeOAuthService;
    private final YouTubeService youTubeService;
    private final ActivityService activityService;
    private final NotificationService notificationService;

    @Transactional
    public PublishResponse publishProject(Long projectId, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new IllegalStateException("Authenticated creator not found."));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));

        if (!project.getCreator().getId().equals(creator.getId())) {
            throw new AccessDeniedException("Only the project creator can publish this project.");
        }
        if (project.getStatus() != ProjectStatus.APPROVED) {
            throw new IllegalStateException("Only approved projects can be published.");
        }
        if (youTubeVideoRepository.findByProjectId(projectId).isPresent()) {
            throw new IllegalStateException("This project has already been published to YouTube.");
        }

        ProjectSubmission submission = submissionRepository
                .findTopByProjectOrderByVersionDesc(project)
                .orElseThrow(() -> new IllegalStateException("No submitted video is available to publish."));

        Path videoPath = Paths.get(submission.getVideoPath());
        if (!Files.isRegularFile(videoPath) || !Files.isReadable(videoPath)) {
            throw new IllegalStateException("The latest submitted video file is missing or unreadable.");
        }

        YouTubeAccount account = youTubeAccountRepository.findByCreator(creator)
                .orElseThrow(() -> new IllegalStateException(
                        "Connect a YouTube account before publishing a project."
                ));
        String accessToken = youTubeOAuthService.getValidAccessToken(creator);
        YouTubeUploadResult uploadResult = youTubeService.uploadVideo(accessToken, submission);

        LocalDateTime publishedAt = LocalDateTime.now();
        YouTubeVideo youTubeVideo = YouTubeVideo.builder()
                .videoId(uploadResult.videoId())
                .youtubeUrl(uploadResult.youtubeUrl())
                .title(project.getTitle())
                .description(project.getDescription())
                .channelTitle(account.getChannelTitle())
                .publishedAt(publishedAt)
                .project(project)
                .build();
        youTubeVideoRepository.save(youTubeVideo);

        project.setYoutubeVideo(youTubeVideo);
        project.setStatus(ProjectStatus.PUBLISHED);
        projectRepository.save(project);

        activityService.logActivity(
                project,
                creator,
                ActivityType.PUBLISHED,
                "Published \"" + project.getTitle() + "\" to YouTube."
        );
        notificationService.createNotification(
                creator,
                project,
                "Project Published",
                "Your project \"" + project.getTitle() + "\" has been published successfully."
        );

        return PublishResponse.builder()
                .projectId(project.getId())
                .videoId(youTubeVideo.getVideoId())
                .youtubeUrl(youTubeVideo.getYoutubeUrl())
                .publishedAt(publishedAt)
                .message("Project published successfully.")
                .build();
    }
}
