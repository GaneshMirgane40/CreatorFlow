package com.ganesh.creatorflow.dto.activity;

import com.ganesh.creatorflow.enums.ActivityType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityResponse {

    private Long id;
    private ActivityType activityType;
    private String description;
    private String userName;
    private LocalDateTime createdAt;
}
