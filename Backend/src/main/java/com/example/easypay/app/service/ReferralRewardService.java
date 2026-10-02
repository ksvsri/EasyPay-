package com.example.easypay.app.service;

import com.example.easypay.app.entity.AppTransaction;
import com.example.easypay.app.entity.AppUser;

public interface ReferralRewardService {

	public void handleFirstPaymentReward(AppUser sender, AppTransaction txn);
	
}
