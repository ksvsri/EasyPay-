package com.example.easypay.app.dto.response;

import java.math.BigDecimal;

import lombok.Data;

/**
 * DTO : To get the payment activity response.
 */
@Data
public class PaymentActivityResponseDto {
	
	private BigDecimal amountReceived;
	
	private BigDecimal amountSpent;
	
	private Integer noOfCredits;
	
	private Integer noOfDebits;

}
