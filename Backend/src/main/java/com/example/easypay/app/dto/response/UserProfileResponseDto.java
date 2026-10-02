package com.example.easypay.app.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * DTO: To get the user details for the user profile page
 */
@Data
public class UserProfileResponseDto {
	
	public String userName;
	
	public String userEmail;
	
	public String mobileNo;
	
	public LocalDateTime createdAt;
	
	public String profileImagePath;
	
	public List<LinkedAccountResponse> linkedAccounts;

}
