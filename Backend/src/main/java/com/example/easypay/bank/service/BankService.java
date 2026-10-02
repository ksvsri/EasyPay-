package com.example.easypay.bank.service;

import java.math.BigDecimal;
import java.util.List;

import com.example.easypay.app.dto.response.AccountUpiLinkDto;
import com.example.easypay.app.dto.response.ResolveReceiverResponceDto;
import com.example.easypay.bank.entity.BankAccount;
import com.example.easypay.exceptions.RecordNotFoundException;
import com.example.easypay.exceptions.UnableToCreateException;

public interface BankService {

	public List<AccountUpiLinkDto> getAccountsForLinking(String mobileNo,Integer bankId) throws RecordNotFoundException;
	
	public List<String> generateAndReturnValidUpis(Integer accountId) throws RecordNotFoundException, UnableToCreateException;
	public List<String> generateUpiCandidates(BankAccount account);
	public String confirmUpi(Integer accountId,String upiId,String upiPin) throws UnableToCreateException, RecordNotFoundException;
	public void setAccountAsPrimary(Integer prevPrimaryAccountId,Integer newPrimaryAccountId) throws RecordNotFoundException;
	public ResolveReceiverResponceDto resolveReceiver(String value) throws RecordNotFoundException, UnableToCreateException;
	public void senderAccountBank(Integer senderBankAccountId,String receiverUpiId,BigDecimal amount,Integer appTxnId) throws RecordNotFoundException, UnableToCreateException;
	
}
