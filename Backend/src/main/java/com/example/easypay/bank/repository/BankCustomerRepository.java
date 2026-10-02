package com.example.easypay.bank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.easypay.bank.entity.BankCustomer;

@Repository
public interface BankCustomerRepository extends JpaRepository<BankCustomer,Integer>{

	// Getting customer by mobile no
	Optional<BankCustomer> findByMobile(String mobile);
	
	@Query("""
			SELECT DISTINCT bc
			FROM BankCustomer bc
			JOIN FETCH bc.accounts a
			WHERE bc.mobile = :mobile
			   OR EXISTS (
			       SELECT 1 FROM BankUpiIds u
			       WHERE u.bankAccount = a AND u.upiId = :upiId
			   )
			""")
			Optional<BankCustomer> findCustomerWithAccounts(
			        @Param("upiId") String upiId,
			        @Param("mobile") String mobile
			);

}
