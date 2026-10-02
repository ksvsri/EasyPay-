package com.example.easypay.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO: To get know the selected bank in the model to get available an active bank accounts.
 */
@Data
public class SelectedBank {	
	
	@NotNull
	public Integer bankId;
	
	@NotNull
	public Integer userId;
}
