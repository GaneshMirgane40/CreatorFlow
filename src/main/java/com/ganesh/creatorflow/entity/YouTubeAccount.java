package com.ganesh.creatorflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "youtube_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YouTubeAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String channelId;

    @Column(nullable = false)
    private String channelTitle;

    @Lob
    @Column(nullable = false)
    private String accessToken;

    @Lob
    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime tokenExpiry;

    @Column(nullable = false, updatable = false)
    private LocalDateTime connectedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false, unique = true)
    private User creator;

    @PrePersist
    public void prePersist() {
        connectedAt = LocalDateTime.now();
    }
}