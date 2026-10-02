package com.example.easypay.bank.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Data
@Table(name = "bank_customers",uniqueConstraints = {
        @UniqueConstraint(columnNames = "customer_aadhaar")
    })

public class BankCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bankCustomerId;

    @Column(name = "customer_name",nullable = false)
    private String name;
    
    @Column(name = "customer_mobile",nullable = false)
    private String mobile;
    
    @Column(name = "customer_email")
    private String email;
    
    @Column(name = "customer_aadhaar",nullable = false, updatable = false)
    private String aadhaarNo;

    @Column(name = "created_at",updatable = false,insertable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "bankCustomer")
    private List<BankAccount> accounts;
}
