package com.example.easypay.app.dto.response;

import lombok.Data;

/**
 * DTO: To get the available banks in the db to displays the banks in modal.
 */
@Data
public class BankDataForLogos {

	private Integer bankId;

	private String bankName;

	private String bankCode;

	public BankDataForLogos(Integer bankId, String bankName, String bankCode) {
		super();
		this.bankId = bankId;
		this.bankName = bankName;
		this.bankCode = bankCode;
	}

}
