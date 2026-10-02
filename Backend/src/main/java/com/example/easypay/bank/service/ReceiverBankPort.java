package com.example.easypay.bank.service;

import java.math.BigDecimal;

import com.example.easypay.bank.entity.BankUpiIds;

public interface ReceiverBankPort {
    void receiverAccountBank(BankUpiIds receiverUpi, BigDecimal amount);
}
