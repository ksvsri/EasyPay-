package com.example.easypay.app.dto.request;

import lombok.Data;

/**
 * To validate the OTP
 */
@Data
public class OtpValidateDto {
	
	
	private String mobileNo;
	
	private String receivedOtp;
	
}
