package com.example.easypay.exceptions;

public class ExistException  extends Exception{
	private String message;
	
	public ExistException(String message) {
		super(message);
	}

}
