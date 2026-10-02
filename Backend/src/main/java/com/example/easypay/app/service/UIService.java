package com.example.easypay.app.service;

import java.util.List;

import com.example.easypay.app.dto.request.SelectedAccountForLink;
import com.example.easypay.app.dto.request.SelectedBank;
import com.example.easypay.app.dto.response.AccountUpiLinkDto;
import com.example.easypay.app.dto.response.BankDataForLogos;
import com.example.easypay.exceptions.RecordNotFoundException;
import com.example.easypay.exceptions.UnableToCreateException;

public interface UIService {
	
	public List<BankDataForLogos> getBankDataForLogos() throws RecordNotFoundException;
	
	public List<AccountUpiLinkDto> getAccountMatchedByMobileAndBankId(SelectedBank dto) throws RecordNotFoundException;

	public List<String> getValidUpis(SelectedAccountForLink dto) throws RecordNotFoundException, UnableToCreateException;
	
}
