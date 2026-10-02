package com.example.easypay.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * DTO  To link an account:
 */
@Data
public class SelectedAccountForLink {
	
	@NotNull
	private Integer userId;
	
	@NotNull
	private Integer accountId;

}
