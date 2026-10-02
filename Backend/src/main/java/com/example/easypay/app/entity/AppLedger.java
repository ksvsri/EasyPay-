package com.example.easypay.app.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.easypay.app.enums.AppLedgerEntryType;

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
@Table(name = "app_ledger",
       indexes = {
           @Index(name = "idx_ledger_txn", columnList = "transaction_id"),
           @Index(name = "idx_ledger_account", columnList = "user_linked_account_id")
       })

public class AppLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_ledger_id")
    private Integer appLedgerId;

    // Account affected
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_linked_account_id")
    private UserLinkedAccount userLinkedAccount;

    // Business event reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private AppTransaction transaction;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, length = 10)
    private AppLedgerEntryType entryType; // DEBIT / CREDIT

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
