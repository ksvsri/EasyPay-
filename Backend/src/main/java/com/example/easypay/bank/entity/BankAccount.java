package com.example.easypay.bank.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.easypay.bank.enums.AccountType;
import com.example.easypay.bank.enums.BankAccountStatus;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Data
@Table(
    name = "bank_accounts",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "account_number")
    }
)
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bankAccountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_customer_id", nullable = false)
    private BankCustomer bankCustomer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    
    private BigDecimal balance;
    
    @Column(name = "per_txn_limit")
    private BigDecimal perTxnLimit;
    
    @Column(name = "per_day_limit")
    private BigDecimal perDayLimit;

    @Column(name = "per_month_limit")
    private BigDecimal perMonthLimit;
    
    @Column(name = "per_day_remaining_limit")
    private BigDecimal perDayRemainingLimit;
    
    @Column(name = "per_month_remaining_limit")
    private BigDecimal perMonthRemainingLimit;
    
    @Column(name = "last_daily_reset_date")
    private LocalDate lastDailyResetDate;

    @Column(name = "last_monthly_reset")
    private String lastMonthlyReset;

    @Enumerated(EnumType.STRING)
    private BankAccountStatus status;

    @Column(name = "created_at",updatable = false,insertable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "bankAccount")
    private List<BankUpiIds> upiIds;
}

