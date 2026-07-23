package com.ganesh.creatorflow.repository;

import com.ganesh.creatorflow.entity.Project;
import com.ganesh.creatorflow.entity.ProjectSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectSubmissionRepository extends JpaRepository<ProjectSubmission, Long> {

    List<ProjectSubmission> findByProjectOrderByVersionDesc(Project project);

    Optional<ProjectSubmission> findTopByProjectOrderByVersionDesc(Project project);

}