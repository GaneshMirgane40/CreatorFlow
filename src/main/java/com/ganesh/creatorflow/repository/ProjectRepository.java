package com.ganesh.creatorflow.repository;

import com.ganesh.creatorflow.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ganesh.creatorflow.enums.ProjectStatus;

import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


@Repository
public interface ProjectRepository extends
        JpaRepository<Project, Long>,
        JpaSpecificationExecutor<Project> {

    List<Project> findByAssignedEditorEmail(String email);

    List<Project> findByCreatorEmail(String email);

    List<Project> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
    List<Project> findByStatus(ProjectStatus status);
}
