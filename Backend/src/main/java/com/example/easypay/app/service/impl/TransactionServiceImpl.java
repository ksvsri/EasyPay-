package com.example.easypay.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.easypay.app.dto.request.GetTransactionsRequestDto;
import com.example.easypay.app.dto.response.GetAllTransactionsResponseDto;
import com.example.easypay.app.entity.AppTransaction;
import com.example.easypay.app.mapper.TransactionMapper;
import com.example.easypay.app.repository.AppTransactionRespository;
import com.example.easypay.app.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private AppTransactionRespository appTransactionRespository;

	@Autowired
	private TransactionMapper mapper;

	/**
	 * UC-08--TRANSACTION HISTORY Get N transactions of M page when user requested.
	 */
	@Override
	public Page<GetAllTransactionsResponseDto> getAllTransactions(GetTransactionsRequestDto request) {

		Pageable pageable;

		//Check monthly statement or normal filter
		if (Boolean.TRUE.equals(request.getDisablePagination())) {
			
			//Monthly statement need to get all the transactions so pagination.
			pageable = Pageable.unpaged();
		} else {
			
			// Get the data in the pages
			pageable = PageRequest.of(request.getPage(), request.getSize(),
					Sort.by(Sort.Direction.DESC, "completedAt", "initiatedAt"));
		}

		//Calling repository to get the transactions
		Page<AppTransaction> txPage = appTransactionRespository.findAccountTransactionsFiltered(request.getUserId(),
				request.getUserLinkedAccountId(), request.getStatus(), request.getSearch(), request.getMinAmount(),
				request.getMaxAmount(), request.getFromDate(), request.getToDate(), pageable);

		//Mapping transaction to DTO
		List<GetAllTransactionsResponseDto> filtered = txPage.stream().map(txn -> {
			GetAllTransactionsResponseDto dto = mapper.toDto(txn);

			//Check debit or credit 
			//If sender linked account Id and request linked account Id mathches then DEBIT
			boolean isDebit = txn.getSenderLinkedAccount().getUserLinkedAccountId()
					.equals(request.getUserLinkedAccountId());

			dto.setDebit(isDebit);
			dto.setCredit(!isDebit);
			return dto;
			
			// Once getting all transactions if filter IS based on the DEBIT OR CREDIT, then return CREDIT or DEBIT 
			//based transactions
		}).filter(dto -> {
			if ("DEBIT".equals(request.getDirection()))
				return dto.isDebit();
			if ("CREDIT".equals(request.getDirection()))
				return dto.isCredit();
			return true;
		}).toList();

		//For monthly statement
		long totalElements = Boolean.TRUE.equals(request.getDisablePagination()) ? filtered.size()
				: txPage.getTotalElements();

		return new PageImpl<>(filtered, pageable, totalElements);
	}

}
