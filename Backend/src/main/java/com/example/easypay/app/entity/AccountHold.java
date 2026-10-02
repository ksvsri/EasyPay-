package com.example.easypay.app.entity;
	
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.easypay.app.enums.HoldStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(
    name = "account_holds",
    indexes = {
        @Index(name = "idx_account_hold_account", columnList = "bank_account_id"),
        @Index(name = "idx_account_hold_status", columnList = "status"),
        @Index(name = "idx_account_hold_txn_ref", columnList = "transaction_ref")
    }
)
public class AccountHold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hold_id")
    private Integer holdId;

    @Column(name = "bank_account_id", nullable = false)
    private Integer bankAccountId;

    
    @Column(name = "amount", nullable = false, precision = 38, scale = 2)
    private BigDecimal amount;

    
    @Column(name = "transaction_ref", nullable = false, unique = true, length = 50)
    private Integer transactionRef;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private HoldStatus status;

    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt=LocalDateTime.now();

    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt=LocalDateTime.now().plusMinutes(5);
}
