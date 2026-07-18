package com.ganesh.creatorflow.repository;

import com.ganesh.creatorflow.entity.ReviewFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewFeedbackRepository extends JpaRepository<ReviewFeedback, Long> {

    List<ReviewFeedback> findByProjectIdOrderByCreatedAtDesc(Long projectId);
}
