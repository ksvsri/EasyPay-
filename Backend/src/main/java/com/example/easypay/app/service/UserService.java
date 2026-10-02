package com.example.easypay.app.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.easypay.app.dto.request.ConfirmUpiRequestDto;
import com.example.easypay.app.dto.request.OtpValidateDto;
import com.example.easypay.app.dto.request.RegisterDto;
import com.example.easypay.app.dto.request.RemoveLinkedAccountRequestDto;
import com.example.easypay.app.dto.request.ResetUpiPinRequestDto;
import com.example.easypay.app.dto.request.SelectedAccountForLink;
import com.example.easypay.app.dto.request.SetPrimaryAccountRequestDto;
import com.example.easypay.app.dto.request.UpdateUserDetails;
import com.example.easypay.app.dto.response.LinkedAccountResponse;
import com.example.easypay.app.dto.response.UserProfileResponseDto;
import com.example.easypay.exceptions.ExistException;
import com.example.easypay.exceptions.InvalidOtpException;
import com.example.easypay.exceptions.RecordNotFoundException;
import com.example.easypay.exceptions.UnableToCreateException;

public interface UserService {
	
	public String registerUser(RegisterDto registerDto) throws RecordNotFoundException, InvalidOtpException, ExistException;
	
	public String login(OtpValidateDto otpValidateDto) throws InvalidOtpException;
	
	public UserProfileResponseDto getProfile(Integer userId) throws RecordNotFoundException;
	
	public String LinkAccount(SelectedAccountForLink dto) throws RecordNotFoundException, ExistException, UnableToCreateException;
	
	public List<LinkedAccountResponse> getLinkedAccounts(Integer userId);
	
	public String confirmUpi(ConfirmUpiRequestDto dto) throws RecordNotFoundException, UnableToCreateException;

	public String updateUserDetails(UpdateUserDetails dto) throws RecordNotFoundException, ExistException;
	
	public String markAccountAsPrimary(SetPrimaryAccountRequestDto dto) throws RecordNotFoundException, UnableToCreateException;

	public String removeAccount(RemoveLinkedAccountRequestDto dto) throws RecordNotFoundException, UnableToCreateException;
	
	public String uploadProfileImage(MultipartFile image,Integer userId) throws IOException;
	
	public String resetUpiPin(ResetUpiPinRequestDto dto) throws RecordNotFoundException, UnableToCreateException;
}
