package com.example.easypay.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.easypay.app.dto.request.SelectedAccountForLink;
import com.example.easypay.app.dto.request.SelectedBank;
import com.example.easypay.app.dto.response.AccountUpiLinkDto;
import com.example.easypay.app.dto.response.BankDataForLogos;
import com.example.easypay.app.service.UIService;
import com.example.easypay.exceptions.RecordNotFoundException;
import com.example.easypay.exceptions.UnableToCreateException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/ui")
public class UIController {
	
	@Autowired
	private UIService uiService;
	
	// Getting the bank details for logos
	@GetMapping("/getBankDataForLogos")
	public ResponseEntity<List<BankDataForLogos>> getBankData() throws RecordNotFoundException{
		return ResponseEntity.ok(uiService.getBankDataForLogos());
	}
	
	// Getting accounts of the selected bank: When user chooses a bank he will get all the accounts linked in that bank.
	@PostMapping("/getAccountsToLink")
	public ResponseEntity<List<AccountUpiLinkDto>> getAccounts(@RequestBody @Valid SelectedBank dto) throws RecordNotFoundException{
		return ResponseEntity.ok(uiService.getAccountMatchedByMobileAndBankId(dto));
	}
	
	//Getting the valid and available upiIds from the bank: To create the upi Id for the user, If no upi yet.
	@PostMapping("/getValidUpis")
	public ResponseEntity<List<String>> getValidUpis(@RequestBody SelectedAccountForLink dto) throws RecordNotFoundException, UnableToCreateException{
		return ResponseEntity.ok(uiService.getValidUpis(dto));
	}

}
