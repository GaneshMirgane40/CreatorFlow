package com.ganesh.creatorflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditorDashboardResponse {

    private Long assignedProjects;
    private Long inProgress;
    private Long underReview;
    private Long reviewRequested;
    private Long approved;
    private Long published;
}