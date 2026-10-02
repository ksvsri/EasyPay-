package com.example.easypay.app.dto.request;

import com.example.easypay.app.enums.AppTransactionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO: To send the money to one receiver.
 */
@Data
public class SendMoneyToOneRequestDto {

	@NotNull
	private Integer userId;
	
	@NotNull
	private Integer fromAccountId;
	
	@NotBlank
	private String receiverUpiId;
	
	@NotBlank
	private String receiverName;
	
	@NotBlank
	private String bankName;
	
	@NotBlank
	private String maskedAccountNo;
	
	@NotBlank
	private String amount;
	
	private AppTransactionType transactionType;
	
	private String note;
	
	@NotBlank
	private String pin;


}
