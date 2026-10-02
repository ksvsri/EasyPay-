package com.example.easypay.app.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
import com.example.easypay.app.service.UserService;
import com.example.easypay.exceptions.ExistException;
import com.example.easypay.exceptions.InvalidOtpException;
import com.example.easypay.exceptions.RecordNotFoundException;
import com.example.easypay.exceptions.UnableToCreateException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class AppUserController {

	@Autowired
	private UserService userService;

	
	// Register the user	
	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody @Valid RegisterDto registerDto)
			throws RecordNotFoundException, InvalidOtpException, ExistException {
		return ResponseEntity.ok(userService.registerUser(registerDto));
	}

	//Login the user
	@PostMapping("/login")
	public ResponseEntity<String> loginUser(@RequestBody @Valid OtpValidateDto dto) throws InvalidOtpException {
		return ResponseEntity.ok(userService.login(dto));
	}

	// Get the user Profile
	@GetMapping("/getProfile")
	public ResponseEntity<UserProfileResponseDto> getProfile(@RequestParam Integer userId) throws RecordNotFoundException {
		return ResponseEntity.ok(userService.getProfile(userId));
	}
	
	// Update the user details like email and name
	@PostMapping("/updateUserDetails")
	public ResponseEntity<String> update(@RequestBody @Valid UpdateUserDetails dto) throws RecordNotFoundException, ExistException{
		return ResponseEntity.ok(userService.updateUserDetails(dto));
	}
	
	//Link bank account to the user
	@PostMapping("/linkAccount")
	public ResponseEntity<String> linkAccount(@RequestBody @Valid SelectedAccountForLink dto) throws RecordNotFoundException, ExistException, UnableToCreateException{
		return ResponseEntity.ok(userService.LinkAccount(dto));
	}
	
	// Create and confirm upi
	@PostMapping("/confirmUpi")
	public ResponseEntity<String> confirmUpi(@RequestBody @Valid ConfirmUpiRequestDto dto) throws RecordNotFoundException, ExistException, UnableToCreateException{
		return ResponseEntity.ok(userService.confirmUpi(dto));
	}
	
	//Reset Upi Pin
	@PostMapping("/resetUpiPin")
	public ResponseEntity<String> reset(@RequestBody @Valid ResetUpiPinRequestDto dto) throws RecordNotFoundException, UnableToCreateException {
		return ResponseEntity.ok(userService.resetUpiPin(dto));
	}
	
	
	// Get Linked accounts
	@GetMapping("/getLinkedAccounts")
	public ResponseEntity<List<LinkedAccountResponse>> getLinkedAccounts(@RequestParam Integer userId){
		return ResponseEntity.ok(userService.getLinkedAccounts(userId));
	}
	
	//Make account as primary
	@PostMapping("/makePrimaryAccount")
	public ResponseEntity<String> markAsPrimary(@RequestBody @Valid SetPrimaryAccountRequestDto dto) throws RecordNotFoundException, UnableToCreateException{
		return ResponseEntity.ok(userService.markAccountAsPrimary(dto));
	}
	
	//Remove linked account from the user linked accounts
	@PostMapping("/removeLinkedAccount")
	public ResponseEntity<String> remove(@RequestBody @Valid RemoveLinkedAccountRequestDto dto) throws RecordNotFoundException, UnableToCreateException{
		return ResponseEntity.ok(userService.removeAccount(dto));
	}
	
	// Upload the profile image
	@PostMapping("/uploadProfileImage")
	public ResponseEntity<String>  uploadProfileImage(@RequestParam("image") MultipartFile image, @RequestParam("userId") Integer userId) throws IOException {
		return ResponseEntity.ok(userService.uploadProfileImage(image, userId));
	}
	
	
}