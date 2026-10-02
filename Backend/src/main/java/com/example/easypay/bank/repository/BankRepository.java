package com.example.easypay.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.easypay.bank.entity.Bank;
import com.example.easypay.bank.enums.BankStatus;


@Repository
public interface BankRepository  extends JpaRepository<Bank,Integer>{
	
	// Getting all the active banks
	public List<Bank> findByStatus(BankStatus status);

}
