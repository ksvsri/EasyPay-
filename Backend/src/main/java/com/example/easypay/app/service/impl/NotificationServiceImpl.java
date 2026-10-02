package com.example.easypay.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.easypay.app.dto.response.NotificationCountAndLatestTwoResponseDto;
import com.example.easypay.app.dto.response.NotificationResponsDto;
import com.example.easypay.app.entity.AppUser;
import com.example.easypay.app.entity.Notification;
import com.example.easypay.app.mapper.NotificationCountAndLatestTwoMapper;
import com.example.easypay.app.repository.AppUserRepository;
import com.example.easypay.app.repository.NotificationRepository;
import com.example.easypay.app.service.NotificationService;
import com.example.easypay.exceptions.RecordNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class NotificationServiceImpl implements NotificationService {
	
	@Autowired
	private AppUserRepository userRepository;
	
	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private NotificationCountAndLatestTwoMapper mapper;
	
	/**
	 * Checks user exists or not
	 * @param userId
	 * @return
	 * @throws RecordNotFoundException
	 */
	public AppUser getAppUser(Integer userId) throws RecordNotFoundException {
		AppUser user=userRepository.findById(userId).
				orElseThrow(()->new RecordNotFoundException("User doesnot exists"));
		return user;
		
	}
	
	/**
	 * UC-15-- NOTIFICATIONS
	 * Gets unread notification count to display in the navbar
	 */
	@Override
	public Integer getUnreadNotificationCount(Integer userId) throws RecordNotFoundException {
		
		getAppUser(userId);
		
		return notificationRepository.countByAppUser_AppUserIdAndIsReadFalse(userId);
	}

	/**
	 * UC-15-- NOTIFICATIONS
	 * Get latest two notifications and count of unread notifications.
	 * These two notifications are displayed in the notification panel when user clicked on.
	 */
	@Override
	@jakarta.transaction.Transactional(Transactional.TxType.REQUIRES_NEW)
	public NotificationCountAndLatestTwoResponseDto getCountAndLatestTwo(Integer userId) throws RecordNotFoundException {
		
		//Checks user exists or not
		AppUser user=getAppUser(userId);
		
		//Unread notification count
		Integer count=notificationRepository.countByAppUser_AppUserIdAndIsReadFalse(userId);
		
		//Get latest two
		List<Notification> latestTwo=notificationRepository.
				findTop2ByAppUser_AppUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
		
		//Mapping to dto
		List<NotificationResponsDto> latestTwoDtos= latestTwo.stream().map(notification->{
			NotificationResponsDto dto=mapper.toDto(notification);
			dto.setAppUserId(user.getAppUserId());
			return dto;
		}).toList();
		
		return new NotificationCountAndLatestTwoResponseDto(count,latestTwoDtos);
	}

	/**
	 * UC-15-- NOTIFICATIONS
	 * Getting all notifications both read and unread
	 */
	@Override
	public List<NotificationResponsDto> getAll(Integer userId) throws RecordNotFoundException {
		
		//Checks user exists or not
		getAppUser(userId);
		
		// Getting all notifications ordered by recent time
		List<Notification> notifications=notificationRepository.findByAppUser_AppUserIdOrderByCreatedAtDesc(userId);
		
		// Mapping to dto
		return notifications.stream().map(notification->{
			NotificationResponsDto dto=mapper.toDto(notification);
			dto.setAppUserId(userId);
			return dto;
		}).toList();
	}

	/**
	 * UC-15-- NOTIFICATIONS
	 * Mark One transaction as read.
	 */
	@Transactional
	@Override
	public void markOneAsRead(Integer userId, Integer notificationId) throws RecordNotFoundException {
		
		//Checking  User existence
		getAppUser(userId);
		
		// Query that updates notification as read notification
		//Note: instead of fetching notification, set it true and then again save is lot of work and db calls
		notificationRepository.markAsRead(notificationId);
		
	}

	/**
	 * UC-15-- NOTIFICATIONS
	 * Mark all transactions as read.
	 */
	@Transactional
	@Override
	public void markAllAsRead(Integer userId) throws RecordNotFoundException {
		
		// Checking user existence
		getAppUser(userId);
		
		//Mark all notifications of particular user as read notifications
		notificationRepository.markAllAsRead(userId);
		
	}
	
	


}
