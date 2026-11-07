package com.ganesh.creatorflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class SubmissionResponse {

    private Long submissionId;
    private Integer version;

    /** Original file name (without the storage UUID prefix), used for downloads. */
    private String fileName;

    /**
     * Authenticated API path (relative to /api) that streams the video, e.g.
     * /projects/11/submissions/5/video. The browser must send the JWT, so the
     * frontend fetches it through axios rather than using it directly as a src.
     */
    private String videoUrl;

    private String uploadedBy;
    private String uploadedByEmail;
    private LocalDateTime uploadedAt;
    private String message;
}
