package com.ganesh.creatorflow.controller;

import com.ganesh.creatorflow.dto.activity.ActivityResponse;
import com.ganesh.creatorflow.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping("/{projectId}/activities")
    public ResponseEntity<List<ActivityResponse>> getProjectActivities(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(activityService.getProjectActivities(projectId));
    }
}
