package com.ganesh.creatorflow.service;

import com.ganesh.creatorflow.dto.ProjectRequest;
import com.ganesh.creatorflow.dto.ProjectResponse;
import com.ganesh.creatorflow.entity.Project;
import com.ganesh.creatorflow.entity.User;
import com.ganesh.creatorflow.enums.ProjectStatus;
import com.ganesh.creatorflow.repository.ProjectRepository;
import com.ganesh.creatorflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectResponse createProject(ProjectRequest request, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .creator(creator)
                .status(ProjectStatus.PENDING)
                .build();

        Project savedProject = projectRepository.save(project);

        return ProjectResponse.builder()
                .id(savedProject.getId())
                .title(savedProject.getTitle())
                .description(savedProject.getDescription())
                .status(savedProject.getStatus().name())
                .creatorEmail(savedProject.getCreator().getEmail())
                .assignedEditorEmail(savedProject.getAssignedEditor() != null ? 
                        savedProject.getAssignedEditor().getEmail() : null)
                .createdAt(savedProject.getCreatedAt())
                .build();
    }
}
