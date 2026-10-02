package com.example.easypay.app.dto.request;

import lombok.Data;

/**
 * To get the payment activity of particular month and the year.
 */
@Data
public class PaymentActivityRequestDto {
	
	private Integer userId;
	
	private Integer userLinkedAccountId;
	
	private Integer year;
	
	private Integer month;

}
