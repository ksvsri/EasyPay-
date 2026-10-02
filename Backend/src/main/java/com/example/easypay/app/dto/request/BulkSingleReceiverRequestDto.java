package com.example.easypay.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BulkSingleReceiverRequestDto {

    @NotBlank
    private String receiverUpiId;

    @NotBlank
    private String receiverName;

    @NotBlank
    private String bankName;

    @NotBlank
    private String maskedAccountNo;

    @NotBlank
    private String amount;

    private String note;
}