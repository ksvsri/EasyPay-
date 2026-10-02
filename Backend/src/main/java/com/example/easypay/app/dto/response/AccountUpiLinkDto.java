package com.example.easypay.app.dto.response;

import com.example.easypay.bank.enums.AccountType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
	  Used to Get the details of account that has upiId in the bank database 
	  
	  Used this DTO to get details when bank is selected this will set which account has upiId
	   
	  and which is primary whether pin is set or not all these.
	  
	 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountUpiLinkDto {

	
	private Integer bankAccountId;
	private Integer bankId;
	private String bankName;
	private String bankCode;
	private String maskedAccountNo;
	private AccountType accountType;
	private boolean upiExists;
	private String upiId;
	private boolean linked;
	private boolean primary;
	private boolean isPinSet;
}
