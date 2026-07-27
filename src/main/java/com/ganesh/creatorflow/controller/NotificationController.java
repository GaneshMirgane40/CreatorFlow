package com.ganesh.creatorflow.controller;

import com.ganesh.creatorflow.dto.notification.NotificationResponse;
import com.ganesh.creatorflow.entity.User;
import com.ganesh.creatorflow.repository.UserRepository;
import com.ganesh.creatorflow.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            Authentication authentication
    ) {
        User user = getCurrentUser(authentication);
        return ResponseEntity.ok(notificationService.getMyNotifications(user));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return ResponseEntity.ok(notificationService.getUnreadCount(user));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = getCurrentUser(authentication);
        notificationService.markAsRead(id, user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        User user = getCurrentUser(authentication);
        notificationService.markAllAsRead(user);
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not found"
                ));
    }
}
