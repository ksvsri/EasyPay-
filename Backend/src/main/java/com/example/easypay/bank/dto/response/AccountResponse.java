package com.example.easypay.bank.dto.response;

import com.example.easypay.bank.enums.AccountType;

import lombok.Data;

@Data
public class AccountResponse {
	
	private Integer accountId;
	
	private String maskedAccountNo;
	
	private AccountType accountType;

}
