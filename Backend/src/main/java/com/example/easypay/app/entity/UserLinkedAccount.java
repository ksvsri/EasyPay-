package com.example.easypay.app.entity;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.easypay.app.enums.UserLinkedAccountStatus;
import com.example.easypay.bank.enums.AccountType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Data
@Table(name = "user_linked_account",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"app_user_id", "bank_account_id"})
       })

public class UserLinkedAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userLinkedAccountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id", nullable = false)
    private AppUser appUser;

    // Bank DB reference (no FK constraint at app DB)
    
    @Column(name = "bank_id", nullable = false)
    private Integer bankId;
    
    @Column(name = "bank_name", nullable = false)
    private String bankName;
    
    @Column(name = "bank_account_id", nullable = false)
    private Integer bankAccountId;

    @Column(name = "masked_account_no", nullable = false, length = 20)
    private String maskedAccountNo;

    @Column(name = "account_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    
    
    @Column(name = "is_primary", nullable = false, length = 20)
    private boolean isPrimary;

    @Column(name = "upi_id", length = 100)
    private String upiId;

    @Column(name = "pin_set", nullable = false)
    private Boolean pinSet;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserLinkedAccountStatus status = UserLinkedAccountStatus.ACTIVE;
}
