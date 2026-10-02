package com.example.easypay.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.easypay.app.dto.response.NotificationCountAndLatestTwoResponseDto;
import com.example.easypay.app.dto.response.NotificationResponsDto;
import com.example.easypay.app.service.NotificationService;
import com.example.easypay.exceptions.RecordNotFoundException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/notification")
public class NotificationController {
	
	@Autowired
	private NotificationService notificationService;
	

	//Getting latest two notifications and number of unread notifications
	@GetMapping("/getCountAndLatestTwo")
	public ResponseEntity<NotificationCountAndLatestTwoResponseDto> getCountAndLatestTwo(@RequestParam Integer userId) throws RecordNotFoundException {
		return ResponseEntity.ok(notificationService.getCountAndLatestTwo(userId));
	}
	
	//Getting all notifications
	
	@GetMapping("/getAll")
	public ResponseEntity<List<NotificationResponsDto>> getAll(@RequestParam Integer userId) throws RecordNotFoundException {
		return ResponseEntity.ok(notificationService.getAll(userId));
	}
	
	//Mark notification as read notification.
	@PutMapping("/markOneAsRead")
	public ResponseEntity<Void> markOne(@RequestParam Integer userId,@RequestParam Integer notificationId) throws RecordNotFoundException {
		notificationService.markOneAsRead(userId, notificationId);
		return ResponseEntity.noContent().build();
	}
	
	//Mark all notifications as marked notifications
	
	@PutMapping("/markAllAsRead")
	public ResponseEntity<Void> markAll(@RequestParam Integer userId) throws RecordNotFoundException {
		notificationService.markAllAsRead(userId);
		return ResponseEntity.noContent().build();
	}
	
	

}
