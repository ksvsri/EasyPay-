package com.example.easypay.app.dto.response;

import lombok.Data;

/**
 * DTO: 
 * 1. When sender sent the money. The following dto is uset to get sender and receiver name and bank details
 */
@Data
public class PartyDto {
	
	private String name;
    private String bankName;
    private String maskedAccountNo;
    private String upiId;

}
