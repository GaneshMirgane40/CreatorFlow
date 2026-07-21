package com.ganesh.creatorflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "youtube_videos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YouTubeVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String videoId;

    @Column(nullable = false)
    private String youtubeUrl;

    @Column(nullable = false)
    private String title;

    @Column(length = 5000)
    private String description;

    private String channelTitle;

    private String thumbnailUrl;

    private String duration;

    private Long viewCount;

    private LocalDateTime publishedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", unique = true)
    private Project project;
}