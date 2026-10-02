package com.example.easypay.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.easypay.app.entity.AppLedger;
import com.example.easypay.app.repository.projection.PaymentActivityProjection;

public interface AppLedgerRepository extends JpaRepository<AppLedger, Integer> {

	// Used to get activity of the account in the selected month and the year.
	@Query(value = """
			    SELECT
			        COALESCE(SUM(CASE WHEN l.entry_type = 'CREDIT' THEN l.amount END), 0) AS amountReceived,
			        COALESCE(SUM(CASE WHEN l.entry_type = 'DEBIT' THEN l.amount END), 0) AS amountSpent,
			        COUNT(CASE WHEN l.entry_type = 'CREDIT' THEN 1 END) AS noOfCredits,
			        COUNT(CASE WHEN l.entry_type = 'DEBIT' THEN 1 END) AS noOfDebits
			    FROM app_ledger l
			    JOIN user_linked_account ula
			        ON ula.userLinkedAccountId = l.user_linked_account_id
			    WHERE ula.app_user_id = :userId
					AND l.user_linked_account_id = :userLinkedAccountId
					AND YEAR(l.created_at) = :year
					AND MONTH(l.created_at) = :month
			    """, nativeQuery = true)
	PaymentActivityProjection getPaymentActivity(@Param("userId") Integer userId, @Param("year") Integer year,
			@Param("month") Integer month,@Param("userLinkedAccountId") Integer userLinkedAccountId);

}
