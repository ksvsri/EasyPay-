package com.example.easypay.app.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.easypay.app.enums.ReferralRewardStatus;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Data
@Table(
    name = "referral_reward",
    indexes = {
        @Index(name = "idx_referral_referrer", columnList = "referrer_user_id"),
        @Index(name = "idx_referral_referred", columnList = "referred_user_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"referred_user_id"})
    }
)

public class ReferralReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="referral_reward_id")
    private Integer referralRewardId;

    // Existing user who shared referral
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referrer_user_id", nullable = false)
    private AppUser referrerUser;

    // New user who registered using referral
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referred_user_id", nullable = false)
    private AppUser referredUser;

    @Column(name = "reward_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal rewardAmount;


    @Enumerated(EnumType.STRING)
    @Column(name = "reward_status", nullable = false, length = 20)
    private ReferralRewardStatus rewardStatus=ReferralRewardStatus.PENDING;    // PENDING / GRANTED / EXPIRED
  

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "rewarded_at")
    private LocalDateTime rewardedAt;

	
}
