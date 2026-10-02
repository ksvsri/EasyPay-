package com.example.easypay.app.service;

import com.example.easypay.app.dto.request.OtpValidateDto;
import com.example.easypay.exceptions.ExistException;
import com.example.easypay.exceptions.InvalidOtpException;
import com.example.easypay.exceptions.RecordNotFoundException;

public interface AuthService {
	
	public String generateRegisterOtp(String mobileNo) throws  ExistException;

	public String generateLoginOtp(String mobileNo) throws RecordNotFoundException;

	public boolean validateOtp(OtpValidateDto otpValidateDto) throws InvalidOtpException;


}
