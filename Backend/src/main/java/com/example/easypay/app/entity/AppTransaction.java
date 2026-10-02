package com.example.easypay.app.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.easypay.app.enums.AppTransactionStatus;
import com.example.easypay.app.enums.AppTransactionType;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "app_transaction",
indexes = {
		@Index(name = "idx_txn_sender", columnList = "sender_user_id"),
		@Index(name = "idx_txn_receiver", columnList = "receiver_user_id"),
		@Index(name = "idx_txn_status", columnList = "status")})

public class AppTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="app_transaction_id")
    private Integer appTransactionId;
    
    @Column(name = "transaction_ref", unique = true, nullable = false, updatable = false)
    private String transactionRef;

    @Column(name = "bulk_ref_id", length = 64)
    private String bulkRefId;
    // Sender user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_id", nullable = false)
    private AppUser senderUser;
    
    // Receiver user (nullable – external user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_user_id")
    private AppUser receiverUser;

    // Sender account
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_linked_account_id", nullable = false)
    private UserLinkedAccount senderLinkedAccount;

    // Receiver account (nullable – external)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_linked_account_id")
    private UserLinkedAccount receiverLinkedAccount;

    // External UPI (if receiver not in app)
    @Column(name = "upi_id", length = 100)
    private String upiId;

    @Column(name = "receiver_name", length = 100)
    private String receiverName;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AppTransactionType type;      // P2P / REQUEST / SCHEDULED

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AppTransactionStatus status;    // INITIATED / PROCESSING / SUCCESS / FAILED / REVERSED

    @Column(length = 255)
    private String description;

    @Column(name = "initiated_at", nullable = false)
    private LocalDateTime initiatedAt=LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTransactionNote> notes;
}
