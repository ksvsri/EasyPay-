package com.example.easypay.app.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.easypay.app.dto.request.OtpValidateDto;
import com.example.easypay.app.repository.AppUserRepository;
import com.example.easypay.app.service.AuthService;
import com.example.easypay.exceptions.ExistException;
import com.example.easypay.exceptions.InvalidOtpException;
import com.example.easypay.exceptions.RecordNotFoundException;
import com.example.easypay.util.OtpUtil;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private AppUserRepository appUserRepository;
	
	@Autowired
	private OtpUtil otpUtil;
	
	/**
	 *  Generate the OTP for registration 
	 *  Takes mobile number as input
	 */
	@Override
	public String generateRegisterOtp(String mobileNo) throws ExistException {
		
		//Get the user using Id
		if (appUserRepository.findByUserMobileNumber(mobileNo).isPresent()) {
	        throw new ExistException("User already exists with mobile number " +  mobileNo);
	    }
		return otpUtil.generateOtp(mobileNo);
	}

	/**
	 * Generate the OTP for login 
	 * Check the user exists or not then throws exception 
	 * because login OTP should be only for the user who already registered.
	 */
	@Override
	public String generateLoginOtp(String mobileNo) throws RecordNotFoundException {
		
		// Get the user Using Id
		appUserRepository.findByUserMobileNumber(mobileNo)
				.orElseThrow(() -> new RecordNotFoundException("User does not exist"));

		return otpUtil.generateOtp(mobileNo);
	}

	/**
	 * Validate the OTP 
	 * Takes mobile number and entered OTP as input.
	 */
	@Override
	public boolean validateOtp(OtpValidateDto dto) throws InvalidOtpException {
		if (!otpUtil.validateOtp(dto.getMobileNo(), dto.getReceivedOtp()))
			throw new InvalidOtpException("Invalid OTP");
		return true;
	}

}
