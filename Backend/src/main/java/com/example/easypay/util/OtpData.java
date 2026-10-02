package com.example.easypay.util;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OtpData {

	private String otp;
	private LocalDateTime expiryTime;
	private boolean verified;

}
