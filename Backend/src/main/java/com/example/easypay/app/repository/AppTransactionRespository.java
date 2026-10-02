package com.example.easypay.app.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.easypay.app.entity.AppTransaction;
import com.example.easypay.app.enums.AppTransactionStatus;

public interface AppTransactionRespository extends JpaRepository<AppTransaction,Integer> {
	
	// suggestion for sending money
	@Query("""
	        SELECT DISTINCT t.upiId
	        FROM AppTransaction t
	        WHERE t.senderUser.appUserId = :senderId
	          AND t.upiId IS NOT NULL
	          AND LOWER(t.upiId) LIKE LOWER(CONCAT('%', :keyword, '%'))
	          AND t.status = :status
	    """)
	    List<String> findExternalUpiSuggestions(
	            @Param("senderId") Integer senderId,
	            @Param("keyword") String keyword,
	            @Param("status") AppTransactionStatus status
	    );
	
	// For Referral reward. To check whether user is making the first transaction or not.
	Integer countBySenderUser_AppUserIdAndStatus(
            Integer appUserId,
            AppTransactionStatus status
    );
	
	
	//Getting all transactions of user and corresponding account
	@Query(
			value = """
			SELECT t
			FROM AppTransaction t
			WHERE (
			        (t.senderUser.id = :userId AND t.senderLinkedAccount.id = :linkedAccountId)
			     OR (t.receiverUser.id = :userId AND t.receiverLinkedAccount.id = :linkedAccountId)
			)
			AND (:status IS NULL OR t.status = :status)
			AND (:minAmount IS NULL OR t.amount >= :minAmount)
			AND (:maxAmount IS NULL OR t.amount <= :maxAmount)
			AND (
			    :search IS NULL OR
			    LOWER(t.receiverName) LIKE LOWER(CONCAT('%', :search, '%')) OR
			    LOWER(t.upiId) LIKE LOWER(CONCAT('%', :search, '%'))
			)
			AND (:fromDate IS NULL OR COALESCE(t.completedAt, t.initiatedAt) >= :fromDate)
			AND (:toDate IS NULL OR COALESCE(t.completedAt, t.initiatedAt) <= :toDate)
			ORDER BY COALESCE(t.completedAt, t.initiatedAt) DESC
			""",
			countQuery = """
			SELECT COUNT(t)
			FROM AppTransaction t
			WHERE (
			        (t.senderUser.id = :userId AND t.senderLinkedAccount.id = :linkedAccountId)
			     OR (t.receiverUser.id = :userId AND t.receiverLinkedAccount.id = :linkedAccountId)
			)
			AND (:status IS NULL OR t.status = :status)
			AND (:minAmount IS NULL OR t.amount >= :minAmount)
			AND (:maxAmount IS NULL OR t.amount <= :maxAmount)
			AND (
			    :search IS NULL OR
			    LOWER(t.receiverName) LIKE LOWER(CONCAT('%', :search, '%')) OR
			    LOWER(t.upiId) LIKE LOWER(CONCAT('%', :search, '%'))
			)
			AND (:fromDate IS NULL OR COALESCE(t.completedAt, t.initiatedAt) >= :fromDate)
			AND (:toDate IS NULL OR COALESCE(t.completedAt, t.initiatedAt) <= :toDate)
			"""
			)
			Page<AppTransaction> findAccountTransactionsFiltered(
			    @Param("userId") Integer userId,
			    @Param("linkedAccountId") Integer linkedAccountId,
			    @Param("status") AppTransactionStatus status,
			    @Param("search") String search,
			    @Param("minAmount") BigDecimal minAmount,
			    @Param("maxAmount") BigDecimal maxAmount,
			    @Param("fromDate") LocalDateTime fromDate,
			    @Param("toDate") LocalDateTime toDate,
			    Pageable pageable
			);



}
