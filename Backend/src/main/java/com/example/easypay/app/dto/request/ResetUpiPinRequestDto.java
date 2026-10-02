package com.example.easypay.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO to reset the UPI pin of particular bank account.
 */
@Data
public class ResetUpiPinRequestDto {
	
	@NotNull
	private Integer userId;
	
	@NotNull
	private Integer bankAccountId;
	
	@NotBlank
	private String newPin;

}
