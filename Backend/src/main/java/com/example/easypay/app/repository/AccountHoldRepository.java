package com.example.easypay.app.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.easypay.app.entity.AccountHold;

public interface AccountHoldRepository extends JpaRepository<AccountHold, Integer> {
	
	
	// To get the total hold amount , so that we can calculate the available balance.
	@Query("""
	        SELECT COALESCE(SUM(h.amount), 0)
	        FROM AccountHold h
	        WHERE h.bankAccountId = :bankAccountId
	          AND h.status = 'ACTIVE'
	    """)
	    BigDecimal getTotalActiveHold(@Param("bankAccountId") Integer bankAccountId);

}
