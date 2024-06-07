package com.stripe.models;

import lombok.Data;

@Data
public class CardDTO {
	
	private String cardNumber;
	private String expMonth;
	private String expYear;
	private String cvc;
	private String token;
	private String username;
	private boolean success;
		

}
