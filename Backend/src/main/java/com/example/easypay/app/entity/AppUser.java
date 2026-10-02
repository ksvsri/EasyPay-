package com.example.easypay.app.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Data
@Table(name = "app_user")
public class AppUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "app_user_id")
	private Integer appUserId;

	@Column(name = "user_name", nullable = false, length = 100)
	private String userName;

	@Column(name = "user_email", unique = true, length = 255)
	private String userEmail;

	@Column(name = "mobile_number", nullable = false, unique = true)
	@Pattern(regexp = "\\d{10}")
	private String userMobileNumber;

	@Column(name = "profile_image_path")
	private String profileImagePath;

	@Column(name = "user_role", nullable = false)
	private String role = "USER";

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	// APPUSER -> USER_PROPERTIES
	@OneToOne(mappedBy = "appUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
	private UserProperties userProperties;

	// APPUSER -> USER_SESSIONS
	@OneToMany(mappedBy = "appUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserSession> sessions;

	// APPUSER ||--o{ USER_LINKED_ACCOUNTS
	@OneToMany(mappedBy = "appUser", fetch = FetchType.LAZY)
	private List<UserLinkedAccount> linkedAccounts;

	// APPUSER ||--o{ TRANSACTIONS (sender / receiver handled in Transaction entity)
	@OneToMany(mappedBy = "senderUser", fetch = FetchType.LAZY)
	private List<AppTransaction> sentTransactions;

	@OneToMany(mappedBy = "receiverUser", fetch = FetchType.LAZY)
	private List<AppTransaction> receivedTransactions;

	// APPUSER ||--o{ SCHEDULED_PAYMENTS
	@OneToMany(mappedBy = "senderUser", fetch = FetchType.LAZY)
	private List<ScheduledPayment> scheduledPayments;

	// APPUSER ||--o{ REFERRAL_REWARDS
	@OneToMany(mappedBy = "referrerUser", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private List<ReferralReward> referralRewards;

	// APPUSER ||--o{ NOTIFICATIONS
	@OneToMany(mappedBy = "appUser", fetch = FetchType.LAZY)
	private List<Notification> notifications;

	// APPUSER ||--o{ MONEY_REQUESTS
	@OneToMany(mappedBy = "requesterUser", fetch = FetchType.LAZY)
	private List<MoneyRequest> moneyRequests;

	// APPUSER ||--o{ USER_FEEDBACK
	@OneToMany(mappedBy = "appUser", fetch = FetchType.LAZY)
	private List<UserFeedback> feedbacks;
}
