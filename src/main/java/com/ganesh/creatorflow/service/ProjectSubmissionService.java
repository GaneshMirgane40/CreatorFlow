package com.ganesh.creatorflow.service;

import com.ganesh.creatorflow.dto.SubmissionResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProjectSubmissionService {

    SubmissionResponse uploadSubmission(
            Long projectId,
            MultipartFile video,
            String editorEmail
    ) throws Exception;

}