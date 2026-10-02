package com.example.easypay.app.dto.response;

import java.time.LocalDateTime;

import com.example.easypay.app.enums.NotificationEntityType;
import com.example.easypay.app.enums.NotificationType;

import lombok.Data;

/**
 * DTO: Notification response to display the notification details when user clicked on notification icon
 */
@Data
public class NotificationResponsDto {

	private Integer notificationId;

	private Integer appUserId;

	private NotificationEntityType entityType;

	private Integer entityId;

	private Boolean isRead;

	private LocalDateTime createdAt;

	private NotificationType type;

	private String message;

	private LocalDateTime readAt;

}
