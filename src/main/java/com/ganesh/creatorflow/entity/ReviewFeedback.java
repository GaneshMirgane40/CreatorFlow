package com.ganesh.creatorflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private ProjectSubmission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(nullable = false)
    private Boolean approved;

    @Column(nullable = false)
    private LocalDateTime reviewedAt;

    @PrePersist
    public void prePersist() {
        reviewedAt = LocalDateTime.now();
    }
}