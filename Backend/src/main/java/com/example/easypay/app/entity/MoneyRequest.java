package com.example.easypay.app.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.easypay.app.enums.MoneyRequestStatus;

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
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity
@Data
@Table(
    name = "money_request",
    indexes = {
        @Index(name = "idx_money_request_requester", columnList = "requester_user_id"),
        @Index(name = "idx_money_request_receiver", columnList = "receiver_user_id")
    }
)

public class MoneyRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="money_request_id")
    private Integer moneyRequestId;

    // Who is asking for money
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_user_id", nullable = false)
    private AppUser requesterUser;

    // Who needs to pay
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_user_id", nullable = false)
    private AppUser receiverUser;

    @Column(nullable = false, precision = 15, scale = 2)
    @Positive
    private BigDecimal amount;

    @Column(name = "note_text", length = 255)
    private String noteText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MoneyRequestStatus status; // PENDING / COMPLETED / REJECTED / EXPIRED
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;
}
