package com.example.easypay.app.service;


import org.springframework.data.domain.Page;

import com.example.easypay.app.dto.request.GetTransactionsRequestDto;
import com.example.easypay.app.dto.response.GetAllTransactionsResponseDto;

public interface TransactionService {
	
	Page<GetAllTransactionsResponseDto> getAllTransactions(
		    GetTransactionsRequestDto request
		);
}
