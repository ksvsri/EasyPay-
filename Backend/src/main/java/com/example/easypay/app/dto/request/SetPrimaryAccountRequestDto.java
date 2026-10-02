package com.example.easypay.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO: to mark account as primary.
 */
@Data
public class SetPrimaryAccountRequestDto {

	@NotNull
	private Integer userId;
	
	@NotNull
	private Integer newPrimaryAccountId;
}
