package com.ganesh.creatorflow.repository;

import com.ganesh.creatorflow.entity.YouTubeVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface YouTubeVideoRepository extends JpaRepository<YouTubeVideo, Long> {

    Optional<YouTubeVideo> findByProjectId(Long projectId);

    Optional<YouTubeVideo> findByVideoId(String videoId);
}