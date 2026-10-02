package com.example.easypay.app.service;

import java.util.List;

import com.example.easypay.app.dto.response.NotificationCountAndLatestTwoResponseDto;
import com.example.easypay.app.dto.response.NotificationResponsDto;
import com.example.easypay.exceptions.RecordNotFoundException;

public interface NotificationService {
	
	public Integer getUnreadNotificationCount(Integer userId) throws RecordNotFoundException;
	
	public NotificationCountAndLatestTwoResponseDto getCountAndLatestTwo(Integer userId) throws RecordNotFoundException;

	public List<NotificationResponsDto> getAll(Integer userId) throws RecordNotFoundException;
	
	public void markOneAsRead(Integer userId,Integer notificationId) throws RecordNotFoundException;
	
	public void markAllAsRead(Integer userId) throws RecordNotFoundException;
}
