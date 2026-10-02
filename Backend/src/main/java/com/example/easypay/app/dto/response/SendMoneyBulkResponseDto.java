package com.example.easypay.app.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class SendMoneyBulkResponseDto {
	
	private String bulkRef;

    
    private Integer totalCount;

    
    private Integer successCount;

   
    private Integer failedCount;

    
    private List<SendMoneyResponseDto> transactions;

}
