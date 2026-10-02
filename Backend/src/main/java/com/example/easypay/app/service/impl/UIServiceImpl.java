package com.example.easypay.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.easypay.app.dto.request.SelectedAccountForLink;
import com.example.easypay.app.dto.request.SelectedBank;
import com.example.easypay.app.dto.response.AccountUpiLinkDto;
import com.example.easypay.app.dto.response.BankDataForLogos;
import com.example.easypay.app.entity.AppUser;
import com.example.easypay.app.repository.AppUserRepository;
import com.example.easypay.app.service.UIService;
import com.example.easypay.bank.entity.Bank;
import com.example.easypay.bank.enums.BankStatus;
import com.example.easypay.bank.repository.BankRepository;
import com.example.easypay.bank.service.BankService;
import com.example.easypay.exceptions.RecordNotFoundException;
import com.example.easypay.exceptions.UnableToCreateException;

@Service
public class UIServiceImpl implements UIService {

	@Autowired
	private BankRepository bankRepository;

	@Autowired
	private BankService bankService;
	
	@Autowired
	private AppUserRepository appUserRepository;

	//Validating the User
	public AppUser getAppUser(Integer userId) throws RecordNotFoundException {
	     return	appUserRepository.findById(userId)
       .orElseThrow(() -> new RecordNotFoundException("User not found"));
	}
	
	/**
	 * UC-02--- ACCOUNT LINK AND CREATION
	 *   Return the bank data to display the bank images when user try to add account.
	 *   Request will be passed to bank bank will give the bank details 
	 *   App does not have direct access to the bank data.
	 */
	@Override
	public List<BankDataForLogos> getBankDataForLogos() throws RecordNotFoundException {
		
		// Bank service is calling, bank returns the active banks 
		List<Bank> banks = bankRepository.findByStatus(BankStatus.ACTIVE);
		
		//if empty throw no banks available exception.
		if (banks.isEmpty())
			throw new RecordNotFoundException("No Banks available");
		
		// Return bank entity as bank response data for logos(Mapping entity to DTO)
		return banks.stream().map(bank -> {
			return new BankDataForLogos(bank.getBankId(), bank.getBankName(), bank.getBankCode());
		}).toList();
	}

	/**
	 * UC-02--- ACCOUNT LINK AND CREATION
	 * Return the Accounts in the selected bank 
	 * App does not have direct access the bank so it will forward the request to the bank.
	 */
	@Override
	public List<AccountUpiLinkDto> getAccountMatchedByMobileAndBankId(SelectedBank selectedBank)
			throws RecordNotFoundException {

		// Get the user
		AppUser user = getAppUser(selectedBank.getUserId());
		        
		// Sending request to the bank and return response.
		List<AccountUpiLinkDto> matchedAccounts = bankService.getAccountsForLinking(user.getUserMobileNumber(),
				selectedBank.getBankId());
		
		// If no accounts found it will display no accounts for the selected bank! instead of exception
		return matchedAccounts;

	}

	/**
	 * UC-02--- ACCOUNT LINK AND CREATION
	 *  App will send the request for upiIds to allow the user to choose one of them
	 *  
	 *  App will ask the bank, bank will generate the some upiIds given to the NPCI
	 *  
	 *  NPCI checks the upiId table and returns only valid upiIds.
	 */
	@Override
	public List<String> getValidUpis(SelectedAccountForLink dto) throws RecordNotFoundException, UnableToCreateException {
		
		// Validating the user.
		getAppUser(dto.getUserId());
		
		return bankService.generateAndReturnValidUpis(dto.getAccountId());
	}
	
	

}
