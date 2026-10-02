package com.example.easypay.exceptions;

public class UnableToCreateException extends Exception{
	private String message;
	
	public UnableToCreateException(String message) {
		super(message);
	}

}