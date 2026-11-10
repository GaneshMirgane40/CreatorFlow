package com.ganesh.creatorflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SubmissionResponse {

    private Long submissionId;
    private Integer version;
    private String videoPath;
    private String message;
}