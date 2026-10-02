package com.example.easypay.app.entity;

import java.time.LocalDateTime;

import com.example.easypay.app.enums.NotificationEntityType;
import com.example.easypay.app.enums.NotificationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "notification", 
         indexes = { 
        		 @Index(name = "idx_notification_user", columnList = "app_user_id"),
		         @Index(name = "idx_notification_read", columnList = "is_read") 
        })

public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer notificationId;

	// Who should see this notification
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "app_user_id", nullable = false)
	private AppUser appUser;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "entity_type", length = 30)
	private NotificationEntityType entityType;  // TRANSACTION / REQUEST / SCHEDULE / NONE
	
	@Column(name = "entity_id")
	private Integer entityId;
	
	@Column(name = "is_read", nullable = false)
	private Boolean isRead;
	
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt=LocalDateTime.now();

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private NotificationType type;  // SUCCESS / FAILURE/RECEIVED/COMPLETED/ FREEZE / etc.
	
	@Column(nullable = false, columnDefinition = "TEXT")
	private String message;


	@Column(name = "read_at")
	private LocalDateTime readAt;
}
