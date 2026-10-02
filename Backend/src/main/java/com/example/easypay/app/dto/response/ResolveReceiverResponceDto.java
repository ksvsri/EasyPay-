package com.example.easypay.app.dto.response;

import lombok.Data;

/**
 * DTO : To get the receiver ban account details along with receiver name.
 */
@Data
public class ResolveReceiverResponceDto {
	
	private String receiverName;
	
	private ResolveReceiverAccountResponseDto accounts;

}
