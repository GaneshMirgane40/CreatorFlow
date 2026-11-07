package com.ganesh.creatorflow.service;

import com.ganesh.creatorflow.dto.SubmissionResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ProjectSubmissionService {

    SubmissionResponse uploadSubmission(
            Long projectId,
            MultipartFile video,
            String editorEmail
    ) throws Exception;

    /** Latest submission of a project, or null when nothing has been submitted yet. */
    SubmissionResponse getLatestSubmission(Long projectId, String userEmail);

    SubmissionVideo getSubmissionVideo(Long projectId, Long submissionId, String userEmail);

    record SubmissionVideo(Resource resource, String fileName, String contentType) {
    }
}
