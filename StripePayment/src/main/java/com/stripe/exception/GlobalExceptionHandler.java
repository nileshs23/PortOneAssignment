package com.stripe.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(StripeException.class)
	public ResponseEntity<MyErrorDetails> stripeExceptionHandler(StripeException se,WebRequest wr){
		MyErrorDetails err = new MyErrorDetails(se.getMessage(), wr.getDescription(true),LocalDateTime.now() );

		return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
	}

}
