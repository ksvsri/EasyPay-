package com.example.easypay.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.easypay.app.dto.request.OtpValidateDto;
import com.example.easypay.app.service.AuthService;
import com.example.easypay.exceptions.ExistException;
import com.example.easypay.exceptions.InvalidOtpException;
import com.example.easypay.exceptions.RecordNotFoundException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private AuthService authService;

	
	// Generate the OTP for Register
	@GetMapping("/generateRegisterOtp")
	public ResponseEntity<String> generateRegisterOtp(@RequestParam String mobileNo) throws ExistException {
		return ResponseEntity.ok(authService.generateRegisterOtp(mobileNo));
	}

	//Generate the OTP for Login
	@GetMapping("/generateLoginOtp")
	public ResponseEntity<String> generateLoginOtp(@RequestParam String mobileNo) throws RecordNotFoundException {
		return ResponseEntity.ok(authService.generateLoginOtp(mobileNo));
	}

	// Validate the OTP
	@PostMapping("/validateOtp")
	public ResponseEntity<Boolean> validate(@RequestBody @Valid OtpValidateDto dto) throws InvalidOtpException {

		return ResponseEntity.ok(authService.validateOtp(dto));
	}
}
