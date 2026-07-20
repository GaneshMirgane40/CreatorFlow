package com.ganesh.creatorflow.service;

import com.ganesh.creatorflow.dto.ReviewFeedbackRequest;
import com.ganesh.creatorflow.dto.ReviewFeedbackResponse;
import com.ganesh.creatorflow.entity.Project;
import com.ganesh.creatorflow.entity.ReviewFeedback;
import com.ganesh.creatorflow.entity.User;
import com.ganesh.creatorflow.enums.ActivityType;
import com.ganesh.creatorflow.enums.ProjectStatus;
import com.ganesh.creatorflow.repository.ProjectRepository;
import com.ganesh.creatorflow.repository.ReviewFeedbackRepository;
import com.ganesh.creatorflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewFeedbackService {

    private final ReviewFeedbackRepository reviewFeedbackRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ActivityService activityService;

    public ReviewFeedbackResponse submitFeedback(
            Long projectId,
            ReviewFeedbackRequest request,
            String creatorEmail
    ) {
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getCreator().getId().equals(creator.getId())) {
            throw new RuntimeException("You are not authorized to submit feedback for this project");
        }

        if (project.getStatus() != ProjectStatus.REVIEW) {
            throw new RuntimeException("Feedback can only be submitted when the project is in review");
        }

        ReviewFeedback savedFeedback = reviewFeedbackRepository.save(
                ReviewFeedback.builder()
                        .feedback(request.getFeedback())
                        .project(project)
                        .creator(creator)
                        .build()
        );
        project.setStatus(ProjectStatus.IN_PROGRESS);
        projectRepository.save(project);

        activityService.logActivity(
                project,
                creator,
                ActivityType.CHANGES_REQUESTED,
                "Requested changes for \"" + project.getTitle() + "\"."
        );

        return toResponse(savedFeedback);
    }

    public List<ReviewFeedbackResponse> getProjectFeedbacks(Long projectId) {
        return reviewFeedbackRepository.findByProjectIdOrderByCreatedAtDesc(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ReviewFeedbackResponse toResponse(ReviewFeedback reviewFeedback) {
        return ReviewFeedbackResponse.builder()
                .id(reviewFeedback.getId())
                .feedback(reviewFeedback.getFeedback())
                .creatorEmail(reviewFeedback.getCreator().getEmail())
                .createdAt(reviewFeedback.getCreatedAt())
                .build();
    }
}
