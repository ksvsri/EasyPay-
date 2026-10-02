package com.example.easypay.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.easypay.error.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(value=RecordNotFoundException.class)
	@ResponseStatus(value=HttpStatus.NOT_FOUND)
	public ErrorResponse handleRecordNotFoundException(RecordNotFoundException e){
		return new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value());
	}
	
	@ExceptionHandler(value=InvalidOtpException.class)
	@ResponseStatus(value=HttpStatus.UNAUTHORIZED)
	public ErrorResponse handleInvalidOtpException(InvalidOtpException e){
		return new ErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED.value());
	}
	@ExceptionHandler(value=ExistException.class)
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	public ErrorResponse handleInvalidOtpException(ExistException e){
		return new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value());
	}
	
	@ExceptionHandler(value=UnableToCreateException.class)
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	public ErrorResponse handleUnableToCreateException(UnableToCreateException e){
		return new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value());
	}
	

	
}