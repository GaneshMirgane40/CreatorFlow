package com.ganesh.creatorflow.dto.notification;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;

    private Long projectId;

    private String title;

    private String message;

    private Boolean isRead;

    private LocalDateTime createdAt;
}