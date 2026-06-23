package com.ganesh.creatorflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {

    private Long id;

    private String title;

    private String description;

    private String status;

    private String creatorEmail;

    private String assignedEditorEmail;

    private LocalDateTime createdAt;
}
