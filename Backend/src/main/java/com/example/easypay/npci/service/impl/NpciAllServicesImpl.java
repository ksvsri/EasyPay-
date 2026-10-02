package com.example.easypay.npci.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.easypay.bank.entity.Bank;
import com.example.easypay.bank.entity.BankUpiIds;
import com.example.easypay.bank.enums.BankStatus;
import com.example.easypay.bank.repository.BankRepository;
import com.example.easypay.bank.repository.BankUpiIdsRepository;
import com.example.easypay.bank.service.ReceiverBankPort;
import com.example.easypay.npci.service.NpciAllServices;

@Service
public class NpciAllServicesImpl implements NpciAllServices {

    

	@Autowired
	private BankUpiIdsRepository bankUpiIdsRepository;
	
	@Autowired
	private ReceiverBankPort receiverBankPort;
	
    

	@Autowired
	private BankRepository bankRepository;

	/**
	 * UC-02--- ACCOUNT CREATION AND VALIDATION
	 * Checks all the UPI Ids and return only available UPI Ids
	 */
	@Override
	public List<String> returnValidUpis(List<String> upis) {

		if (upis == null || upis.isEmpty()) {
			return List.of();
		}

		return upis.stream().filter(upi -> !bankUpiIdsRepository.existsByUpiId(upi)).toList();
	}

	/**
	 * Calls the receiver bank service to credit the money
	 */
	@Override
public void routeToReceiver(String receiverUpiId, BigDecimal amount) {

    // 1️ Resolve receiver bank
    BankUpiIds receiverUpi =
            bankUpiIdsRepository.findByUpiId(receiverUpiId)
                .orElseThrow(() -> new RuntimeException("Receiver UPI not found"));

    // Getting bank account Id 
    Integer receiverBankId =
            receiverUpi.getBankAccount()
                       .getBranch()
                       .getBank()
                       .getBankId();
    
    Bank bank=bankRepository.findById(receiverBankId).orElseThrow(()->new RuntimeException());
    
    if(bank.getStatus()!=BankStatus.ACTIVE) throw new RuntimeException();

    // 2️ Forward request to receiver bank
    receiverBankPort.receiverAccountBank(
            receiverUpi,
            amount
    );
}

	

}
