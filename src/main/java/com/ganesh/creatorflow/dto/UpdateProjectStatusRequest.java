package com.ganesh.creatorflow.dto;

import com.ganesh.creatorflow.enums.ProjectStatus;
import lombok.Data;

@Data
public class UpdateProjectStatusRequest {
    private ProjectStatus status;
}