package com.example.easypay.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.easypay.app.entity.AppUser;



public interface AppUserRepository extends JpaRepository<AppUser,Integer> {
	
	// Helps to find the user using mobile number
	public Optional<AppUser> findByUserMobileNumber(String userMobileNumber);
	
	//Helps to find the user using the email.
	public Optional<AppUser> findByUserEmail(String email);
	
	// Helps to find the user based on the referral code.
	public Optional<AppUser> findByUserProperties_ReferralCode(String referralId);

	// suggestion for sending money
	@Query("""
	        SELECT u
	        FROM AppUser u
	        WHERE
	            LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%'))
	            OR u.userMobileNumber LIKE CONCAT('%', :keyword, '%')
	    """)
	    List<AppUser> searchUsers(@Param("keyword") String keyword);

}
