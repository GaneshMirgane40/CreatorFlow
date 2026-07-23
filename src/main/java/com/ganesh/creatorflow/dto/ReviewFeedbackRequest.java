package com.ganesh.creatorflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewFeedbackRequest {

    @NotBlank(message = "Feedback is required")
    private String feedback;

    @NotNull(message = "Approval status is required")
    private Boolean approved;
}