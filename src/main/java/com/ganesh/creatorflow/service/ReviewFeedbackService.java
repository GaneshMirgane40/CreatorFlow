package com.ganesh.creatorflow.service;

import com.ganesh.creatorflow.dto.ReviewFeedbackRequest;
import com.ganesh.creatorflow.dto.ReviewFeedbackResponse;
import com.ganesh.creatorflow.entity.Project;
import com.ganesh.creatorflow.entity.ProjectSubmission;
import com.ganesh.creatorflow.entity.ReviewFeedback;
import com.ganesh.creatorflow.entity.User;
import com.ganesh.creatorflow.enums.ActivityType;
import com.ganesh.creatorflow.enums.ProjectStatus;
import com.ganesh.creatorflow.repository.ProjectRepository;
import com.ganesh.creatorflow.repository.ProjectSubmissionRepository;
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
    private final ProjectSubmissionRepository submissionRepository;
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
            throw new RuntimeException("Only the creator can review this project.");
        }

        if (project.getStatus() != ProjectStatus.UNDER_REVIEW) {
            throw new RuntimeException("Project is not under review.");
        }

        ProjectSubmission submission = submissionRepository
                .findTopByProjectOrderByVersionDesc(project)
                .orElseThrow(() -> new RuntimeException("No submission found."));

        if (reviewFeedbackRepository.findBySubmission(submission).isPresent()) {
            throw new RuntimeException("This submission has already been reviewed.");
        }

        ReviewFeedback feedback = new ReviewFeedback();
        feedback.setSubmission(submission);
        feedback.setCreator(creator);
        feedback.setFeedback(request.getFeedback());
        feedback.setApproved(request.getApproved());

        ReviewFeedback savedFeedback = reviewFeedbackRepository.save(feedback);

        if (Boolean.TRUE.equals(request.getApproved())) {

            project.setStatus(ProjectStatus.APPROVED);

            activityService.logActivity(
                    project,
                    creator,
                    ActivityType.APPROVED,
                    "Approved project \"" + project.getTitle() + "\"."
            );

        } else {

            project.setStatus(ProjectStatus.REVIEW_REQUESTED);

            activityService.logActivity(
                    project,
                    creator,
                    ActivityType.CHANGES_REQUESTED,
                    "Requested changes for \"" + project.getTitle() + "\"."
            );
        }

        projectRepository.save(project);

        return toResponse(savedFeedback);
    }

    public List<ReviewFeedbackResponse> getProjectFeedbacks(Long projectId) {

        return reviewFeedbackRepository
                .findAllBySubmission_Project_IdOrderByReviewedAtDesc(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ReviewFeedbackResponse toResponse(ReviewFeedback feedback) {

        return ReviewFeedbackResponse.builder()
                .id(feedback.getId())
                .feedback(feedback.getFeedback())
                .creatorEmail(feedback.getCreator().getEmail())
                .approved(feedback.getApproved())
                .reviewedAt(feedback.getReviewedAt())
                .build();
    }
}