package com.example.easypay.bank.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.easypay.app.dto.response.AccountUpiLinkDto;
import com.example.easypay.app.dto.response.ResolveReceiverAccountResponseDto;
import com.example.easypay.app.dto.response.ResolveReceiverResponceDto;
import com.example.easypay.app.entity.AccountHold;
import com.example.easypay.app.entity.UserLinkedAccount;
import com.example.easypay.app.enums.HoldStatus;
import com.example.easypay.app.enums.UserLinkedAccountStatus;
import com.example.easypay.app.repository.AccountHoldRepository;
import com.example.easypay.app.repository.UserLinkedAccountRepository;
import com.example.easypay.bank.entity.Bank;
import com.example.easypay.bank.entity.BankAccount;
import com.example.easypay.bank.entity.BankCustomer;
import com.example.easypay.bank.entity.BankUpiIds;
import com.example.easypay.bank.enums.BankAccountStatus;
import com.example.easypay.bank.repository.BankAccountRepository;
import com.example.easypay.bank.repository.BankCustomerRepository;
import com.example.easypay.bank.repository.BankUpiIdsRepository;
import com.example.easypay.bank.service.AccountService;
import com.example.easypay.bank.service.BankService;
import com.example.easypay.exceptions.RecordNotFoundException;
import com.example.easypay.exceptions.UnableToCreateException;
import com.example.easypay.npci.service.NpciAllServices;

import jakarta.transaction.Transactional;

@Service
public class BankServiceImpl implements BankService {

	@Autowired
	private BankAccountRepository bankAccountRepository;

	@Autowired
	private BankUpiIdsRepository bankUpiIdRepository;

	@Autowired
	private UserLinkedAccountRepository userLinkedAccountRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AccountService accountService;

	@Autowired
	private NpciAllServices npci;

	@Autowired
	private AccountHoldRepository accountHoldRepository;

	@Autowired
	private BankCustomerRepository bankCustomerRepository;

	/**
	 * UC-02 --  ACCOUNT CREATION AND VALIDATION
	 * When the user click on bank, Get all the accounts of bank of that user
	 * Return the accounts of the particular bank
	 */
	@Override
	public List<AccountUpiLinkDto> getAccountsForLinking(String mobileNo, Integer bankId)
			throws RecordNotFoundException {

		// Fetch matched bank accounts join query
		List<BankAccount> accounts = bankAccountRepository.findAccountsToLink(mobileNo, bankId);

		// Build response per account
		return accounts.stream().map(account -> {

			// ---- Mask account number
			String maskedAccountNo = accountService.maskAccountNo(account.getAccountNumber());

			// Check if UPI exists for this account
			Optional<BankUpiIds> upiOpt = bankUpiIdRepository
					.findByBankAccount_BankAccountId(account.getBankAccountId());

			boolean upiExists = upiOpt.isPresent();
			String upiId = upiOpt.map(BankUpiIds::getUpiId).orElse(null);
			boolean isPinSet = upiOpt.map(BankUpiIds::getUpiPinHash).isEmpty();

			// Check if account already linked in app
			Optional<UserLinkedAccount> linkedOpt =
			        userLinkedAccountRepository.findByBankAccountIdAndStatus(
			                account.getBankAccountId(),
			                UserLinkedAccountStatus.ACTIVE
			        );
			// boolean linked or not
			boolean linked = linkedOpt.isPresent();

			// boolean is primary or not
			boolean primary = linkedOpt.map(UserLinkedAccount::isPrimary).orElse(false);

			// Getting bank
			Bank bank = account.getBranch().getBank();

			return new AccountUpiLinkDto(account.getBankAccountId(), bank.getBankId(), bank.getBankName(),
					bank.getBankCode(), maskedAccountNo, account.getAccountType(), upiExists, upiId, linked, primary,
					isPinSet);

		}).toList();
	}

	/**
	 * UC-02--- ACCOUNT CREATION AND VALIDATION
	 * Generate the UpiIds AND Pass the upiIds To the NPCI  to check available or not
	 */
	@Override
	public List<String> generateAndReturnValidUpis(Integer accountId)
			throws RecordNotFoundException, UnableToCreateException {

		// Check account exist or not
		BankAccount account = bankAccountRepository.findByIdWithCustomerAndBank(accountId)
				.orElseThrow(() -> new RecordNotFoundException("Account does not exist"));

		// Check account is blocked if not then only generate

		if (account.getStatus() == BankAccountStatus.BLOCKED) {
			throw new UnableToCreateException("Account is blocked!");
		}

		// Check account is closed or not If not then only generate
		if (account.getStatus() == BankAccountStatus.CLOSED) {
			throw new UnableToCreateException("Account is closed!");
		}

		// Bank generates UpiIds
		List<String> candidates = generateUpiCandidates(account);

		// NPCI validates
		List<String> validUpis = npci.returnValidUpis(candidates);

		if (validUpis.isEmpty()) {
			throw new UnableToCreateException("No UPI IDs available");
		}

		return validUpis;
	}

	/**
	 * UC-02--- ACCOUNT CREATION AND VALIDATION
	 * Generated the UPI Ids out of which NPCI chooses available UPI Ids.
	 */
	@Override
	public List<String> generateUpiCandidates(BankAccount account) {

		// Customer name as base name
		String base = account.getBankCustomer().getName().toLowerCase().replaceAll("\\s+", "");

		// extension with bank+app name
		String handle = "@" + account.getBranch().getBank().getBankCode().toLowerCase() + "easypay";

		return List.of(base + handle, base + "123" + handle, base + "." + account.getBankAccountId() + handle);
	}

	/**
	 * UC-02--- ACCOUNT CREATION AND VALIDATION
	 * Confirm and create the UPI in Upi Table
	 */
	@Override
	@Transactional
	public String confirmUpi(Integer accountId, String upiId, String upiPin)
			throws UnableToCreateException, RecordNotFoundException {

		// check account exists or not
		BankAccount account = bankAccountRepository.findByIdWithCustomerAndBank(accountId)
				.orElseThrow(() -> new RecordNotFoundException("Account does not exist"));

		// check account is active or not
		if (account.getStatus() == BankAccountStatus.BLOCKED) {
			throw new UnableToCreateException("Account is blocked");
		}
		if (account.getStatus() == BankAccountStatus.CLOSED) {
			throw new UnableToCreateException("Account is closed");
		}

		// check UPI not already created for this account
		if (bankUpiIdRepository.existsByBankAccount_BankAccountId(account.getBankAccountId())) {
			throw new UnableToCreateException("UPI already exists for this account");
		}

		// Normalize UPI ID
		String trimmedUpiId = upiId.toLowerCase().trim();

		// Race condition check (VERY IMPORTANT)
		if (bankUpiIdRepository.existsByUpiId(upiId)) {
			throw new UnableToCreateException("Selected UPI ID is no longer available");
		}

		// Create UPI record
		BankUpiIds upi = new BankUpiIds();
		upi.setBankAccount(account);
		upi.setUpiId(trimmedUpiId);
		upi.setUpiPinHash(passwordEncoder.encode(upiPin));
		boolean hasPrimaryUpi = bankUpiIdRepository.existsByBankAccount_BankCustomer_BankCustomerIdAndIsPrimaryTrue(
				account.getBankCustomer().getBankCustomerId());

		upi.setIsPrimary(!hasPrimaryUpi);

		System.out.println("Before saving the upi id");

		bankUpiIdRepository.save(upi);

		System.out.println("after saving the upi id");

		return "success";

	}

	/**
	 * UC-01--- CREATING AND MANAGING USER PROFILE
	 * Sets an account as primary
	 */
	@Override
	@Transactional
	public void setAccountAsPrimary(Integer prevPrimaryAccountId, Integer newPrimaryAccountId)
			throws RecordNotFoundException {

		// check prev primary and new primary not the same
		if (prevPrimaryAccountId != null && prevPrimaryAccountId.equals(newPrimaryAccountId)) {
			return;
		}

		// check new primary exists or not
		BankUpiIds newPrimaryUpi = bankUpiIdRepository.findByBankAccount_BankAccountId(newPrimaryAccountId)
				.orElseThrow(() -> new RecordNotFoundException("New UPI not found"));

		// if prev primary is there save both prev primary and new primary
		if (prevPrimaryAccountId != null) {
			BankUpiIds prevPrimaryUpi = bankUpiIdRepository.findByBankAccount_BankAccountId(prevPrimaryAccountId)
					.orElseThrow(() -> new RecordNotFoundException("Previous UPI not found"));

			prevPrimaryUpi.setIsPrimary(false);
			newPrimaryUpi.setIsPrimary(true);

			bankUpiIdRepository.saveAll(List.of(prevPrimaryUpi, newPrimaryUpi));

			// save new primary only.
		} else {
			newPrimaryUpi.setIsPrimary(true);
			bankUpiIdRepository.save(newPrimaryUpi);
		}
	}

	/**
	 * 
	 * Resets the month and daily limits of bank account 
	 * @param account
	 */
	public void resetLimitsIfRequired(BankAccount account) {

		ZoneId zone = ZoneId.of("Asia/Kolkata");
		LocalDate today = LocalDate.now(zone);
		YearMonth currentMonth = YearMonth.now(zone);

		// DAILY RESET
		if (account.getLastDailyResetDate() == null || !account.getLastDailyResetDate().isEqual(today)) {

			account.setPerDayRemainingLimit(account.getPerDayLimit());
			account.setLastDailyResetDate(today);
		}

		// MONTHLY RESET
		if (account.getLastMonthlyReset() == null
				|| !YearMonth.parse(account.getLastMonthlyReset()).equals(currentMonth)) {

			account.setPerMonthRemainingLimit(account.getPerMonthLimit());
			account.setLastMonthlyReset(currentMonth.toString()); // yyyy-MM
		}
	}

	/**
	 * UC-04-- SEND MONEY TO SINGLE ACCOUNT
	 * 
	 * Validates the sender
	 * 	1. Active or not 
	 * 	2. Has sufficient balance or not
	 *  3.Create Hold entry 
	 * 	3. Then call the NPCI to route the receiver 
	 */
	@Override
	@Transactional

	public void senderAccountBank(Integer senderBankAccountId, String receiverUpiId, BigDecimal amount,
			Integer appTxnId) throws RecordNotFoundException, UnableToCreateException {

		BankAccount senderAccount = bankAccountRepository.findById(senderBankAccountId)
				.orElseThrow(() -> new RecordNotFoundException("Sender bank account not found"));

		if (senderAccount.getStatus() != BankAccountStatus.ACTIVE)
			throw new UnableToCreateException("Sender bank account inactive");

		resetLimitsIfRequired(senderAccount);

		BigDecimal activeHold = accountHoldRepository.getTotalActiveHold(senderBankAccountId);

		BigDecimal available = senderAccount.getBalance().subtract(activeHold);

		if (available.compareTo(amount) < 0)
			throw new UnableToCreateException("Insufficient balance");

		/* 1️ CREATE HOLD */
		AccountHold hold = new AccountHold();
		hold.setBankAccountId(senderBankAccountId);
		hold.setAmount(amount);
		hold.setStatus(HoldStatus.ACTIVE);
		hold.setCreatedAt(LocalDateTime.now());
		hold.setTransactionRef(appTxnId);
		accountHoldRepository.save(hold);

		try {
			/* 2️ ROUTE TO NPCI */
			npci.routeToReceiver(receiverUpiId, amount);

			/* 3️ DEBIT */
			senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
			bankAccountRepository.save(senderAccount);

			/* 4️ RELEASE HOLD */
			hold.setStatus(HoldStatus.RELEASED);
			accountHoldRepository.save(hold);

		} catch (Exception ex) {
			hold.setStatus(HoldStatus.RELEASED);
			accountHoldRepository.save(hold);
			throw ex;
		}
	}

	/**
	 * UC-04--- SEDNING MONEY TO ONE
	 *  Get the receiver details  to send the money
	 */
	@Override
	public ResolveReceiverResponceDto resolveReceiver(String value) throws RecordNotFoundException, UnableToCreateException {

		BankCustomer customer;
		BankUpiIds primaryUpi;

		/*   input type UPI */
		if (value.contains("@")) {

			// Direct UPI lookup
			primaryUpi = bankUpiIdRepository.findByUpiId(value)
					.orElseThrow(() -> new RecordNotFoundException("UPI ID not found"));


			customer = primaryUpi.getBankAccount().getBankCustomer();

		
		} 
		//Input type MOBILE NO
		else if (value.matches("\\d{10}")) {

			// Mobile  customer
			customer = bankCustomerRepository.findByMobile(value)
					.orElseThrow(() -> new UnableToCreateException("Mobile not linked to any bank"));

			// Fetch PRIMARY UPI only
			primaryUpi = customer.getAccounts().stream().filter(acc -> acc.getStatus() == BankAccountStatus.ACTIVE)
					.flatMap(acc -> acc.getUpiIds().stream()).filter(upi -> Boolean.TRUE.equals(upi.getIsPrimary()))
					.findFirst().orElseThrow(() -> new UnableToCreateException("No primary UPI account found"));

		} else {
			throw new UnableToCreateException("Invalid receiver input");
		}

		BankAccount account = primaryUpi.getBankAccount();

		/* response */
		ResolveReceiverAccountResponseDto accountDto = new ResolveReceiverAccountResponseDto();
		accountDto.setUpiId(primaryUpi.getUpiId());
		accountDto.setBankName(account.getBranch().getBank().getBankName());
		accountDto.setBankCode(account.getBranch().getBank().getBankCode());
		accountDto.setMaskedAccountNo(accountService.maskAccountNo(account.getAccountNumber()));
		accountDto.setPrimary(true);

		ResolveReceiverResponceDto response = new ResolveReceiverResponceDto();
		response.setReceiverName(customer.getName());
		response.setAccounts(accountDto);

		return response;
		}

}
