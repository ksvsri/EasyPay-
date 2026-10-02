package com.example.easypay.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Dto to Register the User.
 */
@Data
public class RegisterDto {
	
	@NotBlank
    private String userName;

	@NotBlank
    private String userEmail;

	@NotBlank
    @Pattern(regexp = "^[6-9]\\d{9}$")
    private String userMobileNumber;
	
	private String referralCode;
	
	@NotBlank
	private String appLock;
	
	private String receivedOtp;
	

	

}
