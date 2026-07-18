package com.ganesh.creatorflow.controller;

import com.ganesh.creatorflow.dto.ReviewFeedbackRequest;
import com.ganesh.creatorflow.dto.ReviewFeedbackResponse;
import com.ganesh.creatorflow.service.ReviewFeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ReviewFeedbackController {

    private final ReviewFeedbackService reviewFeedbackService;

    @PostMapping("/{projectId}/feedback")
    public ResponseEntity<ReviewFeedbackResponse> submitFeedback(
            @PathVariable Long projectId,
            @Valid @RequestBody ReviewFeedbackRequest request,
            Authentication authentication
    ) {
        System.out.println("Controller reached");
        String creatorEmail = authentication.getName();
        ReviewFeedbackResponse response = reviewFeedbackService.submitFeedback(
                projectId,
                request,
                creatorEmail
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{projectId}/feedbacks")
    public ResponseEntity<List<ReviewFeedbackResponse>> getProjectFeedbacks(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(reviewFeedbackService.getProjectFeedbacks(projectId));
    }
}
