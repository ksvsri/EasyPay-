package com.example.easypay.app.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.easypay.app.dto.request.BulkSingleReceiverRequestDto;
import com.example.easypay.app.dto.request.PaymentActivityRequestDto;
import com.example.easypay.app.dto.request.SendMoneyBulkRequestDto;
import com.example.easypay.app.dto.request.SendMoneyToOneRequestDto;
import com.example.easypay.app.dto.response.PartyDto;
import com.example.easypay.app.dto.response.PaymentActivityResponseDto;
import com.example.easypay.app.dto.response.ResolveReceiverResponceDto;
import com.example.easypay.app.dto.response.SendMoneyBulkResponseDto;
import com.example.easypay.app.dto.response.SendMoneyResponseDto;
import com.example.easypay.app.entity.AppLedger;
import com.example.easypay.app.entity.AppTransaction;
import com.example.easypay.app.entity.AppUser;
import com.example.easypay.app.entity.UserLinkedAccount;
import com.example.easypay.app.entity.UserProperties;
import com.example.easypay.app.enums.AppLedgerEntryType;
import com.example.easypay.app.enums.AppTransactionStatus;
import com.example.easypay.app.enums.AppTransactionType;
import com.example.easypay.app.enums.AppUserStatus;
import com.example.easypay.app.enums.UserLinkedAccountStatus;
import com.example.easypay.app.mapper.SendMoneyToOneMapper;
import com.example.easypay.app.repository.AppLedgerRepository;
import com.example.easypay.app.repository.AppTransactionRespository;
import com.example.easypay.app.repository.AppUserRepository;
import com.example.easypay.app.repository.UserLinkedAccountRepository;
import com.example.easypay.app.repository.UserPropertiesRepository;
import com.example.easypay.app.repository.projection.PaymentActivityProjection;
import com.example.easypay.app.service.DashboardService;
import com.example.easypay.app.service.ReferralRewardService;
import com.example.easypay.bank.service.AccountService;
import com.example.easypay.bank.service.BankService;
import com.example.easypay.exceptions.RecordNotFoundException;
import com.example.easypay.exceptions.UnableToCreateException;

@Service
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	private AccountService accountService;

	@Autowired
	private UserLinkedAccountRepository userLinkedAccountRepository;

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private AppTransactionRespository appTransactionRespository;

	@Autowired
	private UserPropertiesRepository userPropertiesRepository;

	@Autowired
	private BankService bankService;

	@Autowired
	private AppLedgerRepository appLedgerRepository;

	@Autowired
	private ReferralRewardService referralRewardService;
	
	@Autowired
	private SendMoneyToOneMapper sendMoneyToOneMapper;
	
	/**
	 * Checks the user Status like user exists, active or frozen
	 * @param userId
	 * @return
	 * @throws RecordNotFoundException
	 * @throws UnableToCreateException
	 */
	public boolean checkUserStatus(Integer userId) throws RecordNotFoundException, UnableToCreateException {

		UserProperties userProperties = userPropertiesRepository.findById(userId)
				.orElseThrow(() -> new RecordNotFoundException("User does not exist"));

		if (userProperties.getFreezeUntil() != null && userProperties.getFreezeUntil().isAfter(LocalDateTime.now())) {
			throw new UnableToCreateException("User is frozen until " + userProperties.getFreezeUntil());
		}

		if (userProperties.getStatus() != AppUserStatus.ACTIVE) {
			throw new UnableToCreateException("User is inactive");
		}

		return true;
	}

	/**
	 * UC-03--- CHECK BALANCE
	 * Getting the available balance of particular account, After validating the UPI pin.
	 * 
	 */
	@Override
	public BigDecimal getAvailableBalance(Integer userId, Integer accountId, String pin)
			throws RecordNotFoundException, UnableToCreateException {

		checkUserStatus(userId);

		userLinkedAccountRepository
				.findByAppUser_AppUserIdAndBankAccountId(userId, accountId)
				.orElseThrow(() -> new RecordNotFoundException("Account not linked to this user"));

		// 3 Fetch balance from bank service
		return accountService.getAvailableBalance(accountId, pin);
	}

	
	/**
	 * UC-04 -- SEND MONEY TO SINGLE ACCOUNT
	 * Used to get the receiver suggestions when user types the text in the input field of send money
	 */
	@Override
	public Set<String> getReceiverSuggestions(Integer senderId, String receiverMatchInput) {

		Set<String> suggestions = new LinkedHashSet<>();

		/* 1️⃣ UPI IDs from linked accounts */
		userLinkedAccountRepository.searchUpiIds(receiverMatchInput).forEach(acc -> suggestions.add(acc.getUpiId()));

		/* 2️⃣ App users */
		appUserRepository.searchUsers(receiverMatchInput)
				.forEach(u -> suggestions.add(u.getUserName() + " • " + u.getUserMobileNumber()));

		/* 3️⃣ External receivers from past transactions */
		appTransactionRespository.findExternalUpiSuggestions(senderId, receiverMatchInput, AppTransactionStatus.SUCCESS)
				.forEach(suggestions::add);

		return suggestions;
	}

	public boolean isValid(String receiver) {
		return receiver.matches("\\d{10}") || receiver.matches("^[a-zA-Z0-9._-]+@[a-zA-Z]+$");
	}

	/**
	 * UC-04 --SEND MONEY TO SINGLE ACCOUNT
	 * 
	 * Sends the money 
	 * 1. Checks the user availability
	 * 2. Account active or not 
	 * 3. Checks receiver within application or external
	 * 4. Pass the request dto to sender bank to debit the money.
	 * 5.If success create success transaction or failed transaction.
	 * 6. creates ledger for sender and for receiver if he is internal user.
	 */
	@Override
	@Transactional
	public SendMoneyResponseDto sendMoney(SendMoneyToOneRequestDto dto) throws RecordNotFoundException, UnableToCreateException {

		
		/* 1 Validate sender user */
		AppUser sender = appUserRepository.findById(dto.getUserId())
				.orElseThrow(() -> new RecordNotFoundException("User not found")); 

		UserProperties props = sender.getUserProperties();

		if (props.getStatus() != AppUserStatus.ACTIVE)
			throw new UnableToCreateException("User account is not active");

		if (props.getFreezeUntil() != null && props.getFreezeUntil().isAfter(LocalDateTime.now()))
			throw new UnableToCreateException("User account is temporarily frozen");

		/* 2️ Validate sender linked account */
		UserLinkedAccount senderLinkedAccount = userLinkedAccountRepository
				.findByAppUser_AppUserIdAndBankAccountIdAndStatus(
					    dto.getUserId(),
					    dto.getFromAccountId(),
					    UserLinkedAccountStatus.ACTIVE
					)
				.orElseThrow(() -> new RecordNotFoundException("Invalid sender account"));

		if (!Boolean.TRUE.equals(senderLinkedAccount.getPinSet()))
			throw new UnableToCreateException("UPI PIN not set");

		/* 3️ Parse amount (STRING → BigDecimal) */
		BigDecimal amount;
		try {
			amount = new BigDecimal(dto.getAmount()).setScale(2, RoundingMode.HALF_UP);
		} catch (Exception e) {
			throw new UnableToCreateException("Invalid amount format");
		}

		if (amount.compareTo(BigDecimal.ZERO) <= 0)
			throw new UnableToCreateException("Amount must be greater than zero");

		/* 4️ Resolve receiver (INTERNAL vs EXTERNAL) */
		AppUser receiverUser = null;
		UserLinkedAccount receiverLinkedAccount = null;
		String receiverName;

		System.out.println(" Reciever UpiId "+dto.getReceiverUpiId());
		Optional<UserLinkedAccount> receiverLinkedOpt =
		        userLinkedAccountRepository.findByUpiIdAndStatus(
		                dto.getReceiverUpiId(),
		                UserLinkedAccountStatus.ACTIVE
		        );
		
		// Check receiver withing the application or external 
		if (receiverLinkedOpt.isPresent()) {
			System.out.println(" Receiver found");
			receiverLinkedAccount = receiverLinkedOpt.get();
			receiverUser = receiverLinkedAccount.getAppUser();
			receiverName = receiverUser.getUserName();
		} else {
			//  External UPI
			System.out.println("Receiver not found");
			receiverName = dto.getReceiverName(); // resolved earlier
		}

		
		// Checks whether sender and receiver different or not.
		if (receiverLinkedAccount != null) {
		    if (senderLinkedAccount.getBankAccountId()
		            .equals(receiverLinkedAccount.getBankAccountId())) {
		        throw new UnableToCreateException("Cannot send money to the same account");
		    }
		}

		/* 5 Create App Transaction */
		AppTransaction txn = new AppTransaction();
		txn.setSenderUser(sender);
		txn.setSenderLinkedAccount(senderLinkedAccount);

		// nullable
		txn.setReceiverUser(receiverUser); 
		// nullable
		txn.setReceiverLinkedAccount(receiverLinkedAccount); 

		txn.setUpiId(dto.getReceiverUpiId());
		txn.setReceiverName(receiverName);

		txn.setAmount(amount);
		txn.setType(dto.getTransactionType() != null ? dto.getTransactionType() : AppTransactionType.P2P);
		txn.setStatus(AppTransactionStatus.INITIATED);
		txn.setDescription(dto.getNote());
		txn.setInitiatedAt(LocalDateTime.now());

		txn.setTransactionRef(generateTxnRef(txn));
		appTransactionRespository.save(txn);
		

		try {
			/* 6️ Move to processing */
			txn.setStatus(AppTransactionStatus.PROCESSING);
			appTransactionRespository.save(txn);

			//App is not able to debit or credit so passing request to sender bank to debit the money.
			/* 7️ BANK SIDE (HOLD + DEBIT + NPCI ROUTING) */
			bankService.senderAccountBank(senderLinkedAccount.getBankAccountId(), dto.getReceiverUpiId(), amount,
					txn.getAppTransactionId());

			/* 8️ LEDGER (APP SIDE ONLY) */
			createLedgerEntries(txn);

			txn.setStatus(AppTransactionStatus.SUCCESS);
			txn.setCompletedAt(LocalDateTime.now());
			appTransactionRespository.save(txn);
			
			//Referral Reward entry: If user makes the first transaction create update reward entry for the referrer.
			referralRewardService.handleFirstPaymentReward(sender, txn);

		} catch (Exception ex) {
			txn.setStatus(AppTransactionStatus.FAILED);
			appTransactionRespository.save(txn);
			throw ex;
		}
		
		
		//Creating response
		SendMoneyResponseDto responseDto=sendMoneyToOneMapper.toDto(txn);
		

		responseDto.setSender(createPartyDto(senderLinkedAccount.getAppUser().getUserName(),
				senderLinkedAccount.getMaskedAccountNo(),senderLinkedAccount.getBankName(),
				senderLinkedAccount.getUpiId()));

		// If receiver is External then get receiver details from the transaction.
		responseDto.setReceiver(createPartyDto(txn.getReceiverName(),dto.getMaskedAccountNo(),
				dto.getBankName(),txn.getUpiId()));
			
		return responseDto;
	}

	/**
	 * UC-04 --SEND MONEY TO SINGLE ACCOUNT
	 * Create ledger entries for both sender and receiver
	 * @param txn
	 */
	private void createLedgerEntries(AppTransaction txn) {

		/* 1 Sender ledger  */
		AppLedger senderLedger = new AppLedger();
	    senderLedger.setTransaction(txn);
	    senderLedger.setUserLinkedAccount(txn.getSenderLinkedAccount());
	    senderLedger.setAmount(txn.getAmount());
	    senderLedger.setEntryType(AppLedgerEntryType.DEBIT);
	    senderLedger.setCreatedAt(LocalDateTime.now());
	    appLedgerRepository.save(senderLedger);

	    /* 2️ Receiver ledger (ONLY IF INTERNAL USER) */
	    if (txn.getReceiverLinkedAccount() != null) {
	        AppLedger receiverLedger = new AppLedger();
	        receiverLedger.setTransaction(txn);
	        receiverLedger.setUserLinkedAccount(txn.getReceiverLinkedAccount());
	        receiverLedger.setAmount(txn.getAmount());
	        receiverLedger.setEntryType(AppLedgerEntryType.CREDIT);
	        receiverLedger.setCreatedAt(LocalDateTime.now());
	        appLedgerRepository.save(receiverLedger);
	    }
	}

	/**
	 * UC-04 --SEND MONEY TO SINGLE ACCOUNT
	 * Get the receiver bank details to confirm before sending the money.
	 * These details are get from the bank, so request will be forwarded to the bank.
	 */
	@Override
	@Transactional(readOnly = true)
	public ResolveReceiverResponceDto resolveReceiver(String value) throws RecordNotFoundException, UnableToCreateException {
		return bankService.resolveReceiver(value);
	}

	/**
	 * UC-11 --SPEND ANALYTICS
	 * 
	 * Used to get the transaction history of particular month and the year of particular user.
	 * like 1. Number of debts and credits and the total amount spent and received
	 */
	@Override
	public PaymentActivityResponseDto getPaymentActivityResponse(PaymentActivityRequestDto req) {
		
		//Get the details from the repository.
		//This is custom query with different fields so Projection helps to map these fields.
		PaymentActivityProjection p =
		        appLedgerRepository.getPaymentActivity(
		            req.getUserId(),
		            req.getYear(),
		            req.getMonth(),
		            req.getUserLinkedAccountId()
		        );

		// Map the getting response from repo to dto.
		    PaymentActivityResponseDto dto = new PaymentActivityResponseDto();
		    dto.setAmountReceived(p.getAmountReceived());
		    dto.setAmountSpent(p.getAmountSpent());
		    dto.setNoOfCredits(p.getNoOfCredits());
		    dto.setNoOfDebits(p.getNoOfDebits());
		    return dto;
	}

	/**
	 * UC-04 --SEND MONEY TO SINGLE ACCOUNT
	 * Generate the Transaction ref a unique Id for each transaction.
	 * @param type
	 * @return
	 */
	private String generateTxnRef(AppTransaction type) {
	    return String.format(
	        "EP-%s-%s-%s",
	        type.getType(),
	        LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE),
	        UUID.randomUUID()
	            .toString()
	            .substring(0, 6)
	            .toUpperCase()
	    );
	}
	
	/**
	 * UC-04 --SEND MONEY TO SINGLE ACCOUNT
	 * Creates dto for sender and receiver.
	 * Once Transaction success helps to display the success result from sender to receiver.
	 * @param name
	 * @param maskedAccountNo
	 * @param bankName
	 * @param upiId
	 * @return
	 */
	private PartyDto createPartyDto(String name,String maskedAccountNo,String bankName,String upiId) {
		PartyDto dto=new PartyDto();
		dto.setBankName(bankName);
		dto.setMaskedAccountNo(maskedAccountNo);
		dto.setName(name);
		dto.setUpiId(upiId);
		return dto;
	}

	@Override
	@Transactional
	public SendMoneyBulkResponseDto sendMoneyBulk(SendMoneyBulkRequestDto bulkDto) {

	    String bulkRef = generateBulkRef();

	    int success = 0;
	    int failed = 0;

	    List<SendMoneyResponseDto> results = new ArrayList<>();

	    for (BulkSingleReceiverRequestDto r : bulkDto.getReceivers()) {

	        try {
	            SendMoneyToOneRequestDto one = new SendMoneyToOneRequestDto();

	            one.setUserId(bulkDto.getUserId());
	            one.setFromAccountId(bulkDto.getFromAccountId());
	            one.setPin(bulkDto.getPin());

	            one.setReceiverUpiId(r.getReceiverUpiId());
	            one.setReceiverName(r.getReceiverName());
	            one.setBankName(r.getBankName());
	            one.setMaskedAccountNo(r.getMaskedAccountNo());
	            one.setAmount(r.getAmount());
	            one.setNote(r.getNote());
	            one.setTransactionType(AppTransactionType.P2P);

	            SendMoneyResponseDto res = sendMoney(one);
	            results.add(res);
	            success++;

	        } catch (Exception e) {
	            failed++;
	           
	        }
	    }

	    SendMoneyBulkResponseDto resp = new SendMoneyBulkResponseDto();
	    resp.setBulkRef(bulkRef);
	    resp.setTotalCount(results.size() + failed);
	    resp.setSuccessCount(success);
	    resp.setFailedCount(failed);
	    resp.setTransactions(results);

	    return resp;
	}

	private String generateBulkRef() {
	    return "BULK-" + LocalDate.now() + "-" +
	           UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}

}
