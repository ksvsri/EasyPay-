package com.example.easypay.app.controller;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.easypay.app.dto.request.PaymentActivityRequestDto;
import com.example.easypay.app.dto.request.SendMoneyBulkRequestDto;
import com.example.easypay.app.dto.request.SendMoneyToOneRequestDto;
import com.example.easypay.app.dto.response.PaymentActivityResponseDto;
import com.example.easypay.app.dto.response.ResolveReceiverResponceDto;
import com.example.easypay.app.dto.response.SendMoneyResponseDto;
import com.example.easypay.app.service.DashboardService;
import com.example.easypay.exceptions.RecordNotFoundException;
import com.example.easypay.exceptions.UnableToCreateException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

	@Autowired
	private DashboardService dashboardService;

	// Balance card
	@GetMapping("/getAvailableBalance")
	public ResponseEntity<BigDecimal> getBalance(@RequestParam Integer userId, @RequestParam Integer accountId,
			@RequestParam String pin) throws RecordNotFoundException, UnableToCreateException {
		return ResponseEntity.ok(dashboardService.getAvailableBalance(userId, accountId, pin));
	}

	// Send money suggestions: when user enter receiver mobile or upi or name he will get suggestions.
	@GetMapping("/getReceiverSuggestions")
	public ResponseEntity<Set<String>> getSuggestions(@RequestParam Integer senderId, @RequestParam String recieverMatchInput) {
		return ResponseEntity.ok(dashboardService.getReceiverSuggestions(senderId, recieverMatchInput));
	}

	// Resolve Receiver : Identifies the receiver when sender enters mobile or upi or name
	@GetMapping("/resolveReceiver")
	public ResponseEntity<ResolveReceiverResponceDto> resolve(@RequestParam String value)
			throws RecordNotFoundException, UnableToCreateException {
		return ResponseEntity.ok(dashboardService.resolveReceiver(value));
	}

	// Send money from sender to the receiver
	@PostMapping("/sendMoneyToOne")
	public ResponseEntity<SendMoneyResponseDto> sendMoney(@RequestBody SendMoneyToOneRequestDto dto)
			throws RecordNotFoundException, UnableToCreateException {
		return ResponseEntity.ok(dashboardService.sendMoney(dto));
	}
	
	@PostMapping("/sendMoneyToMany")
	public ResponseEntity<?> sendMoneyBulk( @Valid @RequestBody SendMoneyBulkRequestDto dto) {
	    return ResponseEntity.ok(null);
	}


	// Payment activity: Gets the user transaction details for a month in year.
	@PostMapping("/paymentActivity")
	public ResponseEntity<PaymentActivityResponseDto> getActivity(@RequestBody PaymentActivityRequestDto dto) {
		return ResponseEntity.ok(dashboardService.getPaymentActivityResponse(dto));
	}

}
