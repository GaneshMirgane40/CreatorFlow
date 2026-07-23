package com.ganesh.creatorflow.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.ganesh.creatorflow.dto.youtube.YouTubeVideoRequest;
import com.ganesh.creatorflow.dto.youtube.YouTubeVideoResponse;
import com.ganesh.creatorflow.entity.Project;
import com.ganesh.creatorflow.entity.User;
import com.ganesh.creatorflow.entity.YouTubeVideo;
import com.ganesh.creatorflow.enums.ActivityType;
import com.ganesh.creatorflow.exception.ProjectNotFoundException;
import com.ganesh.creatorflow.mapper.YouTubeVideoMapper;
import com.ganesh.creatorflow.repository.ProjectRepository;
import com.ganesh.creatorflow.repository.UserRepository;
import com.ganesh.creatorflow.repository.YouTubeVideoRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProjectYouTubeService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final YouTubeVideoRepository youTubeVideoRepository;
    private final YouTubeService youTubeApiService;
    private final ActivityService activityService;
    private final YouTubeVideoMapper youTubeVideoMapper;

    public ProjectYouTubeService(
            ProjectRepository projectRepository,
            UserRepository userRepository,
            YouTubeVideoRepository youTubeVideoRepository,
            YouTubeService youTubeApiService,
            ActivityService activityService,
            YouTubeVideoMapper youTubeVideoMapper
    ) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.youTubeVideoRepository = youTubeVideoRepository;
        this.youTubeApiService = youTubeApiService;
        this.activityService = activityService;
        this.youTubeVideoMapper = youTubeVideoMapper;
    }

    public YouTubeVideoResponse addVideoToProject(
            Long projectId,
            YouTubeVideoRequest request,
            String userEmail
    ) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found."));

        if (!isAuthorized(project, userEmail)) {
            throw new AccessDeniedException(
                    "You are not authorized to link a YouTube video to this project."
            );
        }

        if (project.getYoutubeVideo() != null) {
            throw new IllegalStateException("Project already has a linked YouTube video.");
        }

        String videoId = youTubeApiService.extractVideoId(request.getYoutubeUrl());
        if (videoId == null || videoId.isBlank()) {
            throw new IllegalArgumentException("Invalid YouTube URL.");
        }

        JsonNode response = youTubeApiService.fetchVideoDetails(videoId);
        YouTubeVideo video = youTubeVideoMapper.map(response, request.getYoutubeUrl(), project);
        YouTubeVideo savedVideo = youTubeVideoRepository.save(video);
        project.setYoutubeVideo(savedVideo);

        activityService.logActivity(
                project,
                user,
                ActivityType.VIDEO_LINKED,
                "Linked YouTube video \"" + savedVideo.getTitle() + "\"."
        );

        return mapToResponse(savedVideo);
    }

    private boolean isAuthorized(Project project, String userEmail) {
        return belongsTo(project.getCreator(), userEmail)
                || belongsTo(project.getAssignedEditor(), userEmail);
    }

    private boolean belongsTo(User user, String userEmail) {
        return user != null && userEmail != null && userEmail.equals(user.getEmail());
    }

    private YouTubeVideoResponse mapToResponse(YouTubeVideo video) {
        return YouTubeVideoResponse.builder()
                .id(video.getId())
                .videoId(video.getVideoId())
                .youtubeUrl(video.getYoutubeUrl())
                .title(video.getTitle())
                .description(video.getDescription())
                .channelTitle(video.getChannelTitle())
                .thumbnailUrl(video.getThumbnailUrl())
                .duration(video.getDuration())
                .viewCount(video.getViewCount())
                .publishedAt(video.getPublishedAt())
                .build();
    }
}
