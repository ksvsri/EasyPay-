package com.example.easypay.app.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.easypay.app.enums.AppTransactionStatus;

import lombok.Data;

/**
 * Used to get Transactions of
 * 1.User  
 * 2.Bank Account already linked to the user.
 * 3.Page 
 * 4. And No of records  per page.
 */
@Data
public class GetTransactionsRequestDto {
	private Integer userId;
	private Integer userLinkedAccountId;

	// pagination
	private Integer page = 0;
	private Integer size = 10;

	// filters
	private String direction;
	private AppTransactionStatus status;
	private String search;
	private BigDecimal minAmount;
	private BigDecimal maxAmount;
	private LocalDateTime fromDate;
	private LocalDateTime toDate;
	// monthly statement / full data
	private Boolean disablePagination;


}
