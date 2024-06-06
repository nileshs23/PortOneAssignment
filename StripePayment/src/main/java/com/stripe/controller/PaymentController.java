package com.stripe.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentData;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentIntentCollection;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentListParams;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {

	@Value("${stripe.key}")
	String apiKey;
	
	@PostMapping("/create_intent")
	public PaymentData makePayment(@RequestBody PaymentData data) throws StripeException {
		Stripe.apiKey = apiKey;

		PaymentIntentCreateParams params =
		  PaymentIntentCreateParams.builder()
		    .setAmount(data.getAmount())
		    .setCurrency(data.getCurrency())
		    .setAutomaticPaymentMethods(
		      PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
		        .setEnabled(true)
		        .build()
		    )
		    .build();

		PaymentIntent paymentIntent = PaymentIntent.create(params);

		data.setId(paymentIntent.getId());
		
		return data;
		
	}
	
	@GetMapping("/get_intents")
	public List<PaymentData> getAllPayments() throws StripeException{
		
		Stripe.apiKey = apiKey;

		PaymentIntentListParams params =
		  PaymentIntentListParams.builder().setLimit(3L).build();

		PaymentIntentCollection paymentIntents = PaymentIntent.list(params);
		
		List<PaymentData> payments = new ArrayList<>();
		
		for(int i=0;i<paymentIntents.getData().size();i++) {
			Long amount = paymentIntents.getData().get(i).getAmount();
			String currency = paymentIntents.getData().get(i).getCurrency();
			String id = paymentIntents.getData().get(i).getId();
			PaymentData pay = new PaymentData(amount, currency, id);
			
			payments.add(pay);
		}
		
		return payments;
		
	}
	
	
}
