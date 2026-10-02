package com.example.easypay.app.dto.response;

import com.example.easypay.bank.enums.AccountType;

import lombok.Data;


/**
 * DTO: When user chooses one bank in the modal. The user will get all the accounts in the bank
 */
@Data
public class AccountsForSelectedBankDto {
	public Integer bankId;
	public String bankName;
	public String bankCode;
	public Integer accountId;
	public String maskedAccountNo;
	public AccountType accountType;
	public AccountsForSelectedBankDto(Integer bankId, String bankName, Integer accountId,AccountType accountType, String maskedAccountNo,
			 String bankCode) {
		super();
		this.bankId = bankId;
		this.bankName = bankName;
		this.accountId = accountId;
		this.maskedAccountNo = maskedAccountNo;
		this.accountType = accountType;
		this.bankCode=bankCode;
	}
	
	
	
}