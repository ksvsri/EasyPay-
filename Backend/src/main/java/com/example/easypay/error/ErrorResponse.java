package com.example.easypay.error;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Structured error details in API responses whenever an exception occurs.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
	private String message;
	private Integer statusCode;
}