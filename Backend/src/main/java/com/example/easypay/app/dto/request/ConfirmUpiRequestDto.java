package com.example.easypay.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * When user chooses one valid UPI Id out of 3  to create the UPI Id in the bank model
  The following DTO will be used .
 */
@Data
public class ConfirmUpiRequestDto {
	@NotNull
    private Integer userId;
	
	@NotNull
    private Integer accountId;
	
	@NotBlank
    private String upiId;
	
	@NotBlank
	private String upiPin;
}

