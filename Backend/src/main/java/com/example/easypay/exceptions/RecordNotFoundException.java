package com.example.easypay.exceptions;

public class RecordNotFoundException extends Exception {
	
	private String message;
	
	public RecordNotFoundException(String message) {
		super(message);
	}
}
