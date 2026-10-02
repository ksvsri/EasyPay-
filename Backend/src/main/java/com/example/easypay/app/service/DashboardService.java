package com.example.easypay.app.service;

import java.math.BigDecimal;
import java.util.Set;

import com.example.easypay.app.dto.request.PaymentActivityRequestDto;
import com.example.easypay.app.dto.request.SendMoneyBulkRequestDto;
import com.example.easypay.app.dto.request.SendMoneyToOneRequestDto;
import com.example.easypay.app.dto.response.PaymentActivityResponseDto;
import com.example.easypay.app.dto.response.ResolveReceiverResponceDto;
import com.example.easypay.app.dto.response.SendMoneyBulkResponseDto;
import com.example.easypay.app.dto.response.SendMoneyResponseDto;
import com.example.easypay.exceptions.RecordNotFoundException;
import com.example.easypay.exceptions.UnableToCreateException;

public interface DashboardService {
	
	// balance card functions
	public BigDecimal getAvailableBalance(Integer userId,Integer accountId,String pin) throws RecordNotFoundException, UnableToCreateException;

	// send money card functions
	
	public Set<String> getReceiverSuggestions(Integer senderId,String receiverMatchInput);
	
	public SendMoneyResponseDto sendMoney(SendMoneyToOneRequestDto dto) throws RecordNotFoundException, UnableToCreateException;
	
	public ResolveReceiverResponceDto resolveReceiver(String value) throws RecordNotFoundException, UnableToCreateException;
	
	public SendMoneyBulkResponseDto sendMoneyBulk(SendMoneyBulkRequestDto bulkDto);
	
	public PaymentActivityResponseDto getPaymentActivityResponse(PaymentActivityRequestDto dto);
}
