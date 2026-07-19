package com.ganesh.creatorflow.service;

import com.ganesh.creatorflow.dto.activity.ActivityResponse;
import com.ganesh.creatorflow.entity.Activity;
import com.ganesh.creatorflow.entity.Project;
import com.ganesh.creatorflow.entity.User;
import com.ganesh.creatorflow.enums.ActivityType;
import com.ganesh.creatorflow.repository.ActivityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;

    public void logActivity(
            Project project,
            User user,
            ActivityType activityType,
            String description
    ) {
        Activity activity = Activity.builder()
                .project(project)
                .user(user)
                .activityType(activityType)
                .description(description)
                .build();

        activityRepository.save(activity);
    }

    public List<ActivityResponse> getProjectActivities(Long projectId) {
        return activityRepository.findByProjectIdOrderByCreatedAtDesc(projectId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ActivityResponse mapToResponse(Activity activity) {
        return ActivityResponse.builder()
                .id(activity.getId())
                .activityType(activity.getActivityType())
                .description(activity.getDescription())
                .userName(activity.getUser().getName())
                .createdAt(activity.getCreatedAt())
                .build();
    }
}
