package com.example.easypay.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.easypay.app.entity.AppTransaction;
import com.example.easypay.app.entity.AppUser;
import com.example.easypay.app.entity.Notification;
import com.example.easypay.app.entity.ReferralReward;
import com.example.easypay.app.enums.AppTransactionStatus;
import com.example.easypay.app.enums.NotificationEntityType;
import com.example.easypay.app.enums.NotificationType;
import com.example.easypay.app.enums.ReferralRewardStatus;
import com.example.easypay.app.repository.AppTransactionRespository;
import com.example.easypay.app.repository.NotificationRepository;
import com.example.easypay.app.repository.ReferralRewardRepository;
import com.example.easypay.app.service.ReferralRewardService;

@Service
public class ReferralRewardServiceImpl implements ReferralRewardService {
	
	@Autowired
	private ReferralRewardRepository referralRewardRepository;
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private AppTransactionRespository appTransactionRespository;
	
	

	/**
	 * UC-17-- REFERRAL AND REWARD
	 * Checks whether user  making the first payment or not
	 * If referred and making the first success transaction then user get the reward.
	 */
	@Override
	@Transactional
	public void handleFirstPaymentReward(AppUser sender, AppTransaction txn) {
		
		// 1️ Check if sender was referred
		Optional<ReferralReward> referralOpt = referralRewardRepository.findByReferredUser(sender);

		if (referralOpt.isEmpty())
			return;

		ReferralReward referral = referralOpt.get();

		// 2️ Reward already processed
		if (referral.getRewardStatus() != ReferralRewardStatus.PENDING)
			return;

		// 3️ Check FIRST successful transaction
		Integer successCount =appTransactionRespository.countBySenderUser_AppUserIdAndStatus(sender.getAppUserId(),
				AppTransactionStatus.SUCCESS);

		if (successCount != 1)
			return;

		// 4️ Create notification for referrer
		createReferralRewardNotifications(referral, txn);

		// 5️ Mark reward as granted
		referral.setRewardStatus(ReferralRewardStatus.GRANTED);
		referral.setRewardedAt(LocalDateTime.now());

		referralRewardRepository.save(referral);
	}

	/**
	 *  UC-17-- REFERRAL AND REWARD
	 * Creates a notification for the reward.
	 */
	private void createReferralRewardNotifications(ReferralReward referral, AppTransaction txn) {

		//Notification for Referrer
		Notification referrerNotification = new Notification();
		referrerNotification.setAppUser(referral.getReferrerUser());

		referrerNotification.setEntityType(NotificationEntityType.TRANSACTION);
		referrerNotification.setEntityId(txn.getAppTransactionId());

		referrerNotification.setType(NotificationType.SUCCESS);
		referrerNotification.setIsRead(false);

		referrerNotification.setMessage(" You earned ₹" + referral.getRewardAmount() + " referral bonus because "
				+ referral.getReferredUser().getUserName() + " made their first payment.");

		referrerNotification.setCreatedAt(LocalDateTime.now());
		
		//Notification for the Referred
		
		Notification referredNotification = new Notification();
		referredNotification.setAppUser(referral.getReferredUser());
		referredNotification.setEntityType(NotificationEntityType.TRANSACTION);
		referredNotification.setEntityId(txn.getAppTransactionId());
		referredNotification.setType(NotificationType.SUCCESS);
		referredNotification.setIsRead(false);
		referredNotification.setMessage(" You earned ₹" + 10.0 + " as a welcome reward for completing your first payment.");
		referredNotification.setCreatedAt(LocalDateTime.now());
		

		notificationRepository.saveAll(List.of(referrerNotification,referredNotification));
		
	}

}
