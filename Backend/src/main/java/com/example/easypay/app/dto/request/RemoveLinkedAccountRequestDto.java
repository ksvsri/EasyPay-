package com.example.easypay.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Dto to get the details of linked account to remove
 */
@Data
public class RemoveLinkedAccountRequestDto {
	
	@NotNull
	private Integer userId;
	
	@NotNull
	private Integer AccountId;

}
