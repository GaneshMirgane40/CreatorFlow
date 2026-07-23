package com.ganesh.creatorflow.service;

import com.ganesh.creatorflow.dto.CreatorDashboardResponse;
import com.ganesh.creatorflow.entity.User;
import com.ganesh.creatorflow.enums.ProjectStatus;
import com.ganesh.creatorflow.repository.ProjectRepository;
import com.ganesh.creatorflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public CreatorDashboardResponse getCreatorDashboard(String email) {

        User creator = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        Long creatorId = creator.getId();

        return CreatorDashboardResponse.builder()
                .totalProjects(projectRepository.countByCreatorId(creatorId))
                .pending(projectRepository.countByCreatorIdAndStatus(creatorId, ProjectStatus.PENDING))
                .inProgress(projectRepository.countByCreatorIdAndStatus(creatorId, ProjectStatus.IN_PROGRESS))
                .underReview(projectRepository.countByCreatorIdAndStatus(creatorId, ProjectStatus.UNDER_REVIEW))
                .reviewRequested(projectRepository.countByCreatorIdAndStatus(creatorId, ProjectStatus.REVIEW_REQUESTED))
                .approved(projectRepository.countByCreatorIdAndStatus(creatorId, ProjectStatus.APPROVED))
                .published(projectRepository.countByCreatorIdAndStatus(creatorId, ProjectStatus.PUBLISHED))
                .build();
    }
}