package com.example.easypay.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.easypay.app.dto.request.GetTransactionsRequestDto;
import com.example.easypay.app.dto.response.GetAllTransactionsResponseDto;
import com.example.easypay.app.service.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
	
	@Autowired
	private TransactionService transactionService;
	
	
	//Getting transactions as pages
	@PostMapping("/getAllTransactions")
	public ResponseEntity<Page<GetAllTransactionsResponseDto>> getAllTransactions(
	    @RequestBody GetTransactionsRequestDto request
	) {
	    return ResponseEntity.ok(
	        transactionService.getAllTransactions(request)
	    );
	}

}
