package com.example.easypay.app.dto.response;

import com.example.easypay.app.enums.UserLinkedAccountStatus;
import com.example.easypay.bank.enums.AccountType;

import lombok.Data;

/**
 * DTO: To display the user linked accounts in the user profile page.
 */
@Data
public class LinkedAccountResponse {

	
	/*
	 *  This is used to display in the user Profile to Display 
	    
	    the linked accounts to the user.
	 */
	private String bankName;
	
	private Integer bankAccountId;

	private String maskedAccountNo;

	private AccountType accountType;
	
	private String bankCode;

	private boolean isPrimary;

	private String upiId;
	
	private Integer userLinkedAccountId;
	
	private UserLinkedAccountStatus status;

}
