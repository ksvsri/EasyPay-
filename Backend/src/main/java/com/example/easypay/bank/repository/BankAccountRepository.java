package com.example.easypay.bank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.easypay.bank.entity.BankAccount;

@Repository
public interface BankAccountRepository
        extends JpaRepository<BankAccount, Integer> {
	
	// To get bank accounts based on the mobile no and bankId

    @Query("""
        SELECT ba
        FROM BankAccount ba
        JOIN FETCH ba.branch br
        JOIN FETCH br.bank b
        WHERE ba.bankCustomer.mobile = :mobileNo
          AND b.bankId = :bankId
          AND ba.status = 'ACTIVE'
    """)
    List<BankAccount> findAccountsToLink(
            @Param("mobileNo") String mobileNo,
            @Param("bankId") Integer bankId
    );
    
    // Getting Account based on the accountId along with bank and branch details
    @Query("""
    	    SELECT ba FROM BankAccount ba
    	    JOIN FETCH ba.branch br
    	    JOIN FETCH br.bank
    	    WHERE ba.bankAccountId = :accountId
    	""")
    	Optional<BankAccount> findByIdWithBranchAndBank(
    	        @Param("accountId") Integer accountId
    	);
    
    //Getting account along with bank and customer details
    
    @Query("""
    	    SELECT ba FROM BankAccount ba
    	    JOIN FETCH ba.bankCustomer bc
    	    JOIN FETCH ba.branch br
    	    JOIN FETCH br.bank
    	    WHERE ba.bankAccountId = :accountId
    	""")
    	Optional<BankAccount> findByIdWithCustomerAndBank(
    	        @Param("accountId") Integer accountId
    	);
    
    //Getting Accounts by mobile no
    @Query("""
            SELECT ba
            FROM BankAccount ba
            JOIN FETCH ba.bankCustomer bc
            WHERE bc.mobile = :mobile
              AND ba.status = 'ACTIVE'
        """)
        List<BankAccount> findAccountsByMobile(
                @Param("mobile") String mobile
        );
    
   
}
