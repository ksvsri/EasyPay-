package com.example.easypay.bank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.easypay.bank.entity.BankUpiIds;


@Repository
public interface BankUpiIdsRepository extends JpaRepository<BankUpiIds,Integer>{

	//Getting UPI entry by bank account Id
	 Optional<BankUpiIds> findByBankAccount_BankAccountId(
	            Integer bankAccountId
	    );

	 // Checks whether the UPI Id exists or not with the bank account Id
	    boolean existsByBankAccount_BankAccountId(
	            Integer bankAccountId
	    );

	    //Checks UPI Id exists with the customer id and primary account.
	    boolean existsByBankAccount_BankCustomer_BankCustomerIdAndIsPrimaryTrue(
	            Integer bankCustomerId
	    );

	    //Checks UPI Id entry  exists or not with the UPI Id
	    boolean existsByUpiId(String upiId);
	    
	    Optional<BankUpiIds>  findByUpiId(String upiId);
	
	
	
	
}
