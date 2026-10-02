package com.example.easypay.app.dto.response;

import lombok.Data;


/**
 * DTO: To get the receiver bank account details 
 */
@Data
public class ResolveReceiverAccountResponseDto {
	
	private String upiId;
	
	private String bankName;
	
	private String bankCode;
	
	private String maskedAccountNo;
	
	private boolean primary;

}
