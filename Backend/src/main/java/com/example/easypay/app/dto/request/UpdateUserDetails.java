package com.example.easypay.app.dto.request;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import jakarta.validation.constraints.Email;
import lombok.Data;


/**
 * DTO: To update the user details.
 */
@Data
public class UpdateUserDetails {
	
	private Integer userId;
	
	@NotBlank
	private String userName;
	
	@Email
	@NotBlank
	private String userEmail;

}
