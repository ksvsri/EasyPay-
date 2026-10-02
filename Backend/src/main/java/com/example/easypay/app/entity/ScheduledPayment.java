package com.example.easypay.app.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.easypay.app.enums.ScheduledPaymentStatus;

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
@Table(
    name = "scheduled_payment",
    indexes = {
        @Index(name = "idx_sched_sender", columnList = "sender_user_id"),
        @Index(name = "idx_sched_execute_at", columnList = "execute_at")
    }
)

public class ScheduledPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="scheduled_payment_id")
    private Integer scheduledPaymentId;

    // Who schedules the payment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_id", nullable = false)
    private AppUser senderUser;

    // From which account
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_linked_account_id", nullable = false)
    private UserLinkedAccount senderLinkedAccount;

    // Receiver (nullable – external)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_user_id")
    private AppUser receiverUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_linked_account_id")
    private UserLinkedAccount receiverLinkedAccount;

    @Column(name = "upi_id", length = 100)
    private String upiId;

    @Column(name = "receiver_name", length = 100)
    private String receiverName;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ScheduledPaymentStatus status;
    // SCHEDULED / PROCESSING / EXECUTED / FAILED / CANCELLED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "execute_at", nullable = false)
    private LocalDateTime executeAt;
    
    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
}
