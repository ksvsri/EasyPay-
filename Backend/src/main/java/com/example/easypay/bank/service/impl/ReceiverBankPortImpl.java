package com.example.easypay.bank.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.easypay.bank.entity.BankAccount;
import com.example.easypay.bank.entity.BankUpiIds;
import com.example.easypay.bank.enums.BankAccountStatus;
import com.example.easypay.bank.repository.BankAccountRepository;
import com.example.easypay.bank.service.ReceiverBankPort;

@Service
public class ReceiverBankPortImpl implements ReceiverBankPort {
	
	@Autowired
	private BankAccountRepository bankAccountRepository;

	/**
	 * UC-04--- SENDING MONEY TO ONE
	 * Validates the Receiver and credit the money.	
	 */
	@Override
	public void receiverAccountBank(BankUpiIds receiverUpi, BigDecimal amount) {
		
		// Getting bank account using UPI Id
		BankAccount receiverAccount = receiverUpi.getBankAccount();

		// Check account status
        if (receiverAccount.getStatus() != BankAccountStatus.ACTIVE) {
            throw new RuntimeException("Receiver account inactive");
        }

        // Credit the money
        receiverAccount.setBalance(receiverAccount.getBalance().add(amount));
        bankAccountRepository.save(receiverAccount);
		
	}

}
