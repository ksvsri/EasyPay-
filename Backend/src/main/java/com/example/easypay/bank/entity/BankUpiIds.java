package com.example.easypay.bank.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(
    name = "bank_upi_ids",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "upi_id")
    }
)
public class BankUpiIds {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bankUpiId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    @Column(name = "upi_id", nullable = false)
    private String upiId;

    @Column(name = "upi_pin_hash", nullable = false)
    private String upiPinHash;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary;

    @Column(name = "created_at")
    private LocalDateTime createdAt=LocalDateTime.now();
}
