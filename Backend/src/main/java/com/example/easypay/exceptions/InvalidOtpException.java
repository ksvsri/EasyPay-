package com.example.easypay.exceptions;

public class InvalidOtpException extends Exception{
	
	private String message;
	
	public InvalidOtpException(String message) {
		super(message);
	}
	

}
