package com.example.easypay.app.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendMoneyBulkRequestDto {

    @NotNull
    private Integer userId;

    @NotNull
    private Integer fromAccountId;

    @NotBlank
    private String pin;

    @NotNull
    @Size(min = 1)
    private List<BulkSingleReceiverRequestDto> receivers;
}
