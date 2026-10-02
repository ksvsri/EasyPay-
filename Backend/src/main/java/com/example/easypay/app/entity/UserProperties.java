package com.example.easypay.app.entity;


import java.time.LocalDateTime;

import com.example.easypay.app.enums.AppUserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "user_properties")
public class UserProperties {

	@Id
    @Column(name = "app_user_id")
    private Integer userPropertiesId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    @Column(name = "referral_code", length = 20)
    private String referralCode;
    
    @Column(name="app_lock",nullable = false)
    private String appLock;

    @Column(name = "freeze_until")
    private LocalDateTime freezeUntil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppUserStatus status=AppUserStatus.ACTIVE;
}
