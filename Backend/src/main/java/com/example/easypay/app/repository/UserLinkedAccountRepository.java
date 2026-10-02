package com.example.easypay.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.easypay.app.entity.UserLinkedAccount;
import com.example.easypay.app.enums.UserLinkedAccountStatus;

public interface UserLinkedAccountRepository extends JpaRepository<UserLinkedAccount, Integer> {

	// Get the user linked accounts by userId
	public List<UserLinkedAccount> getByAppUser_appUserId(Integer id);

	// Get User linked account by accountId
	public Optional<UserLinkedAccount> findByBankAccountId(Integer bankAccountId);

	// Get the primary linked account of the user using userId
	public Optional<UserLinkedAccount> findByAppUser_AppUserIdAndIsPrimaryTrue(Integer userId);

	// Check primary Account exists for the user or not
	boolean existsByAppUser_AppUserIdAndIsPrimaryTrue(Integer userId);

	// Check bank account Exists by the id in user linked account table
	boolean existsByBankAccountId(Integer bankAccountId);
	
	boolean existsByBankAccountIdAndStatus(
	        Integer bankAccountId,
	        UserLinkedAccountStatus status
	);

	// Get the user linked accounts by userId
	List<UserLinkedAccount> findByAppUser_AppUserIdAndStatus(Integer userId,UserLinkedAccountStatus status);
	
	
	Optional<UserLinkedAccount> findByAppUser_AppUserIdAndBankAccountId(Integer userId,Integer bankAccountId);
	
	// suggestion for sending money
	@Query("""
		    SELECT ula
		    FROM UserLinkedAccount ula
		    WHERE ula.status = com.example.easypay.app.enums.UserLinkedAccountStatus.ACTIVE
		      AND ula.upiId IS NOT NULL
		      AND LOWER(ula.upiId) LIKE LOWER(CONCAT('%', :keyword, '%'))
		""")
		List<UserLinkedAccount> searchUpiIds(@Param("keyword") String keyword);
	
    Optional<UserLinkedAccount> findByUpiId(String upiId); 
	
    
    @Modifying
    @Query("""
        UPDATE UserLinkedAccount ula
        SET ula.status = 'REMOVED'
        WHERE ula.userLinkedAccountId = :id
    """)
    int softDelete(@Param("id") Integer id);
    
    //Find by banka account Id and active linked account
    Optional<UserLinkedAccount> findByBankAccountIdAndStatus(
            Integer bankAccountId,
            UserLinkedAccountStatus status
    );

    //Bank account id and status
    Optional<UserLinkedAccount>
    findByAppUser_AppUserIdAndBankAccountIdAndStatus(
        Integer userId,
        Integer bankAccountId,
        UserLinkedAccountStatus status
    );
    
    //UPI id and status
    Optional<UserLinkedAccount> findByUpiIdAndStatus(
            String upiId,
            UserLinkedAccountStatus status
    );
}
