package com.example.easypay.bank.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.easypay.app.repository.AccountHoldRepository;
import com.example.easypay.bank.dto.response.AccountResponse;
import com.example.easypay.bank.entity.BankAccount;
import com.example.easypay.bank.entity.BankUpiIds;
import com.example.easypay.bank.enums.BankAccountStatus;
import com.example.easypay.bank.repository.BankAccountRepository;
import com.example.easypay.bank.repository.BankUpiIdsRepository;
import com.example.easypay.bank.service.AccountService;
import com.example.easypay.exceptions.RecordNotFoundException;
import com.example.easypay.exceptions.UnableToCreateException;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private BankAccountRepository accountRepository;

	@Autowired
	BankUpiIdsRepository bankUpiIdsRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AccountHoldRepository accountHoldRepository;

	/**
	 * Mask bank account number and return
	 */
	@Override
	public String maskAccountNo(String accountNo) {
		if (accountNo == null || accountNo.length() < 4)
			return "XXXX";
		return "XXXXXX" + accountNo.substring(accountNo.length() - 4);
	}

	/**
	 * Check bank account exists or not
	 * 
	 * @throws RecordNotFoundException
	 * @throws UnableToCreateException
	 */
	@Override
	public BankAccount accountExistsOrNot(Integer accountId) throws RecordNotFoundException {

		BankAccount account = accountRepository.findByIdWithBranchAndBank(accountId)
				.orElseThrow(() -> new RecordNotFoundException("Account does not exist"));

		if (account.getStatus() == BankAccountStatus.CLOSED) {
			throw new RecordNotFoundException("Bank account is closed");
		}

		return account;
	}

	public BankAccount getBankAccount(Integer accountId) throws RecordNotFoundException, UnableToCreateException {
		BankAccount account = accountRepository.findById(accountId)
				.orElseThrow(() -> new RecordNotFoundException("Account doesnot exists"));

		if (account.getStatus() == BankAccountStatus.BLOCKED) {
			throw new UnableToCreateException("Account is blocked!");
		}

		// Check account is closed or not If not then only generate
		if (account.getStatus() == BankAccountStatus.CLOSED) {
			throw new UnableToCreateException("Account is closed!");
		}
		return account;
	}

	/**
	 *  Getting bank account by Id
	 */
	@Override
	public AccountResponse getAccountById(Integer accountId) throws RecordNotFoundException, UnableToCreateException {

		BankAccount account = getBankAccount(accountId);

		//Mapping to DTO
		AccountResponse dto = new AccountResponse();
		dto.setAccountId(accountId);
		dto.setAccountType(account.getAccountType());
		dto.setMaskedAccountNo(maskAccountNo(account.getAccountNumber()));
		return dto;
	}

	/**
	 * UC-03--CHECK BALANCE
	 * Getting available balance of account by taking difference of total - sum of hold amount
	 */
	@Override
	public BigDecimal getAvailableBalance(Integer accountId, String pin)
			throws RecordNotFoundException, UnableToCreateException {

		// Getting bank account
		BankAccount account = getBankAccount(accountId);

		//Getting UPI id using the bank account Id
		BankUpiIds upiIds = bankUpiIdsRepository.findByBankAccount_BankAccountId(accountId)
				.orElseThrow(() -> new RecordNotFoundException("Upi Doesnot exists"));

		// Validate the UPI PIN
		if (!passwordEncoder.matches(pin, upiIds.getUpiPinHash()))
			throw new UnableToCreateException("Invalid Pin");

		BigDecimal activeHold = accountHoldRepository.getTotalActiveHold(accountId);

		BigDecimal available = account.getBalance().subtract(activeHold);

		return available;
	}

	/**
	 * UC-01--CREATING AND MANAGING USER PROFILE
	 * Reset the UPI Pin
	 */
	@Override
	public boolean resetUpiPin(Integer accountId, String newUpiPin)
			throws RecordNotFoundException, UnableToCreateException {

		//Getting bank account
		getBankAccount(accountId);

		//Getting UPI id using the bank account Id
		BankUpiIds upiIds = bankUpiIdsRepository.findByBankAccount_BankAccountId(accountId)
				.orElseThrow(() -> new RecordNotFoundException("Upi Doesnot exists"));

		//If old and new UPI PIN is same then enter new PIN
		if (passwordEncoder.matches(newUpiPin, upiIds.getUpiPinHash()))
			throw new UnableToCreateException("Please enter new UpiPin");

		// Store Hashed UPI Pin 
		upiIds.setUpiPinHash(passwordEncoder.encode(newUpiPin));
		bankUpiIdsRepository.save(upiIds);
		return true;
	}

}
