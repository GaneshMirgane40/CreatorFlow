package com.ganesh.creatorflow.repository;

import com.ganesh.creatorflow.entity.ProjectSubmission;
import com.ganesh.creatorflow.entity.ReviewFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewFeedbackRepository extends JpaRepository<ReviewFeedback, Long> {

    Optional<ReviewFeedback> findBySubmission(ProjectSubmission submission);

    List<ReviewFeedback> findAllBySubmission_Project_IdOrderByReviewedAtDesc(Long projectId);
}