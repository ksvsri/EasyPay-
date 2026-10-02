package com.example.easypay.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.easypay.app.entity.AppUser;
import com.example.easypay.app.entity.ReferralReward;

public interface ReferralRewardRepository extends JpaRepository<ReferralReward,Integer> {
	
	// To know how many times the user referred .To get that related profit.
	 Integer countByReferrerUser_AppUserId(Integer referredId);
	 
	 // To get referrer
	 Optional<ReferralReward> findByReferredUser(AppUser referredUser);


}
