package com.example.easypay.bank.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.example.easypay.bank.enums.BankStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Data
@Table(
    name = "bank",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "bank_name"),
        @UniqueConstraint(columnNames = "bank_code")
    }
)
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bankId;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "bank_code", nullable = false)
    private String bankCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BankStatus status;

    @Column(name = "created_at", updatable = false,insertable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "bank")
    private List<Branch> branches;
}
