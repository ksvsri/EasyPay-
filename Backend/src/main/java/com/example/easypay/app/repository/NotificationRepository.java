package com.example.easypay.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.easypay.app.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification,Integer> {

	//To get the unread notification count.
	public Integer countByAppUser_AppUserIdAndIsReadFalse(Integer userId);
	
	// To display the latest two notifications
	List<Notification> findTop2ByAppUser_AppUserIdAndIsReadFalseOrderByCreatedAtDesc(Integer userId);
	
	//To get All notifications ordered by recently get.
	List<Notification> findByAppUser_AppUserIdOrderByCreatedAtDesc(Integer userId);
	
	//To mark one as read
	@Modifying
	@Query("""
	    UPDATE Notification n
	    SET n.isRead = true,
	        n.readAt = CURRENT_TIMESTAMP
	    WHERE n.notificationId = :id
	""")
	void markAsRead(@Param("id") Integer id);
	
	//To mark all as read
	@Modifying
	@Query("""
	    UPDATE Notification n
	    SET n.isRead = true,
	        n.readAt = CURRENT_TIMESTAMP
	    WHERE n.appUser.appUserId = :userId
	      AND n.isRead = false
	""")
	void markAllAsRead(@Param("userId") Integer userId);
}
