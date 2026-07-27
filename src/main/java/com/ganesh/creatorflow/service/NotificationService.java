package com.ganesh.creatorflow.service;

import com.ganesh.creatorflow.dto.notification.NotificationResponse;
import com.ganesh.creatorflow.entity.Notification;
import com.ganesh.creatorflow.entity.Project;
import com.ganesh.creatorflow.entity.User;
import com.ganesh.creatorflow.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void createNotification(
            User recipient,
            Project project,
            String title,
            String message
    ) {

        Notification notification = Notification.builder()
                .recipient(recipient)
                .project(project)
                .title(title)
                .message(message)
                .build();

        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getMyNotifications(User user) {
        return notificationRepository
                .findByRecipientOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public long getUnreadCount(User user) {
        return notificationRepository.countByRecipientAndIsReadFalse(user);
    }

    @Transactional
    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notification not found"
                ));

        if (notification.getRecipient() == null ||
                !notification.getRecipient().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to modify this notification"
            );
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(User user) {
        List<Notification> unreadNotifications = notificationRepository
                .findByRecipientAndIsReadFalseOrderByCreatedAtDesc(user);

        unreadNotifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .projectId(notification.getProject() != null
                        ? notification.getProject().getId()
                        : null)
                .title(notification.getTitle())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
