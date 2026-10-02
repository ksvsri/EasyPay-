package com.example.easypay.bank.service;

import java.math.BigDecimal;

import com.example.easypay.bank.dto.response.AccountResponse;
import com.example.easypay.bank.entity.BankAccount;
import com.example.easypay.exceptions.RecordNotFoundException;
import com.example.easypay.exceptions.UnableToCreateException;

public interface AccountService {
	
	public String maskAccountNo(String accountNo);
	
	public BankAccount accountExistsOrNot(Integer accountId) throws RecordNotFoundException;
	
	public AccountResponse getAccountById(Integer accountId) throws RecordNotFoundException, UnableToCreateException;
	
	public BigDecimal getAvailableBalance(Integer accountId,String pin) throws RecordNotFoundException, UnableToCreateException;
	 
	public boolean resetUpiPin(Integer accountId,String upiPin) throws RecordNotFoundException, UnableToCreateException;
}
