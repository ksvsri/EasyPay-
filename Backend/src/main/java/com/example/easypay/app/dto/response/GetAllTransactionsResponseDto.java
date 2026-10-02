package com.example.easypay.app.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.easypay.app.enums.AppTransactionStatus;
import com.example.easypay.app.enums.AppTransactionType;

import lombok.Data;


/**
 * DTO: To get Transactions details response
 */
@Data
public class GetAllTransactionsResponseDto {
	
	private String transactionRef;

    private boolean debit;     
    private boolean credit;    

    private String receiverName;
    private String upiId;

 
    private BigDecimal amount;

    // Status & type
    private AppTransactionStatus status;
    private AppTransactionType type;

    // Dates
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;

    // Optional note
    private String description;

}
