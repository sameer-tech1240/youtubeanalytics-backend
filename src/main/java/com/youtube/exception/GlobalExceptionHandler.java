package com.youtube.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

		String msg = ex.getBindingResult().getFieldError().getDefaultMessage();

		return new ResponseEntity<>(new ApiErrorResponse(400, msg), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex) {

		ex.printStackTrace();

		return new ResponseEntity<>(new ApiErrorResponse(500, ex.getClass().getSimpleName() + " : " + ex.getMessage()),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
