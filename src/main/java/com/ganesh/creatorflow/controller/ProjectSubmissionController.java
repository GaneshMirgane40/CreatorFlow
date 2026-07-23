package com.ganesh.creatorflow.controller;

import com.ganesh.creatorflow.dto.SubmissionResponse;
import com.ganesh.creatorflow.service.ProjectSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectSubmissionController {

    private final ProjectSubmissionService submissionService;

    @PostMapping("/{projectId}/submissions")
    public SubmissionResponse uploadSubmission(
            @PathVariable Long projectId,
            @RequestParam("video") MultipartFile video,
            Authentication authentication
    ) throws Exception {

        return submissionService.uploadSubmission(
                projectId,
                video,
                authentication.getName()
        );
    }
}