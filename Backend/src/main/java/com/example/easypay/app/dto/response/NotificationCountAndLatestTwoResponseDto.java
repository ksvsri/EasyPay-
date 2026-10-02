package com.example.easypay.app.dto.response;

import java.util.List;

import lombok.Data;
/**
 * DTO: To get latest two notifications as list and
 * Number of notifications not read yet.
 */
@Data
public class NotificationCountAndLatestTwoResponseDto {
	
	private Integer unreadNotificationCount;
	
	List<NotificationResponsDto> latestTwoUnread;

	public NotificationCountAndLatestTwoResponseDto(Integer unreadNotificationCount,
			List<NotificationResponsDto> latestTwoUnread) {
		super();
		this.unreadNotificationCount = unreadNotificationCount;
		this.latestTwoUnread = latestTwoUnread;
	}
	

}
