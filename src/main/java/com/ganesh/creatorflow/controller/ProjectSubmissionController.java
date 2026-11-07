package com.ganesh.creatorflow.controller;

import com.ganesh.creatorflow.dto.SubmissionResponse;
import com.ganesh.creatorflow.service.ProjectSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectSubmissionController {

    private final ProjectSubmissionService submissionService;

    @PostMapping("/{projectId}/submissions")
    @PreAuthorize("hasRole('EDITOR')")
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

    /**
     * Latest submission for the project. Returns 204 No Content when the editor
     * has not uploaded anything yet.
     */
    @GetMapping("/{projectId}/submission")
    public ResponseEntity<SubmissionResponse> getLatestSubmission(
            @PathVariable Long projectId,
            Authentication authentication
    ) {
        SubmissionResponse response =
                submissionService.getLatestSubmission(projectId, authentication.getName());

        return response == null
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(response);
    }

    /**
     * Streams a submission's video to the project's creator or assigned editor.
     * Range requests are supported, so it can also back a seekable player.
     */
    @GetMapping("/{projectId}/submissions/{submissionId}/video")
    public ResponseEntity<Resource> getSubmissionVideo(
            @PathVariable Long projectId,
            @PathVariable Long submissionId,
            @RequestParam(defaultValue = "false") boolean download,
            Authentication authentication
    ) {
        ProjectSubmissionService.SubmissionVideo video =
                submissionService.getSubmissionVideo(projectId, submissionId, authentication.getName());

        ContentDisposition disposition = (download
                ? ContentDisposition.attachment()
                : ContentDisposition.inline())
                .filename(video.fileName(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(video.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .body(video.resource());
    }
}
