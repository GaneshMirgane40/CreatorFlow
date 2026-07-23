package com.ganesh.creatorflow.controller;

import com.ganesh.creatorflow.dto.CreatorDashboardResponse;
import com.ganesh.creatorflow.dto.EditorDashboardResponse;
import com.ganesh.creatorflow.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/creator")
    public CreatorDashboardResponse creatorDashboard(Authentication authentication) {

        return dashboardService.getCreatorDashboard(authentication.getName());
    }
    @GetMapping("/editor")
    public EditorDashboardResponse editorDashboard(Authentication authentication) {

        return dashboardService.getEditorDashboard(authentication.getName());
    }
}