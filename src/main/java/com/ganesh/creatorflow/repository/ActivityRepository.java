package com.ganesh.creatorflow.repository;

import com.ganesh.creatorflow.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByProjectIdOrderByCreatedAtDesc(Long projectId);
}
