package com.stripe.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
	
	String id;
	Long amount;
	
	//three letter ISO currency code
	String currency;
	String recipients_eMail;
	String status;

	public PaymentDTO(Long amount, String currency, String id) {
		super();
		this.amount = amount;
		this.currency = currency;
		this.id = id;
	}

}
