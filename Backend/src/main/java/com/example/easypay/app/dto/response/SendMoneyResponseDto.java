package com.example.easypay.app.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;


/**
 * DTO: when sender sent the money , used to track the transaction details
 */
@Data
public class SendMoneyResponseDto {
	
	private String transactionRef;
    private String status;
    private LocalDateTime completedAt;
    private BigDecimal amount;
    private PartyDto sender;
    private PartyDto receiver;

}
