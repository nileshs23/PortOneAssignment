package com.stripe.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentIntentCollection;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Refund;
import com.stripe.models.PaymentDTO;
import com.stripe.param.PaymentIntentCaptureParams;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentListParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.PaymentMethodCreateParams;
import com.stripe.param.PaymentMethodListParams;
import com.stripe.param.RefundCreateParams;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {
	
//	Create Payment Id To Checout Order
	@PostMapping("/create_intent")
	public ResponseEntity<PaymentDTO> makePayment(@RequestBody PaymentDTO data, 
													@RequestParam String customerId,
													@RequestParam String apiKey
													) throws StripeException {
		Stripe.apiKey = apiKey;
		
		// Define the parameters to filter the payment methods by customer and type (card)
        PaymentMethodListParams params1 = PaymentMethodListParams.builder()
            .setCustomer(customerId)
            .setType(PaymentMethodListParams.Type.CARD)
            .build();
        
        List<PaymentMethod> paymentMethods = PaymentMethod.list(params1).getData();
        
        String paymentMethodId = paymentMethods.get(0).getId();
        
		
		PaymentIntentCreateParams params =
				  PaymentIntentCreateParams.builder()
				    .setAmount(data.getAmount())
				    .setCurrency(data.getCurrency())
				    .setCustomer(customerId)
				    .setReceiptEmail(data.getRecipients_eMail())
				    .setPaymentMethod(paymentMethodId)
				    .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
				    .setConfirm(false)
				    .build();

		PaymentIntent paymentIntent = PaymentIntent.create(params);	
		
		data.setId(paymentIntent.getId());
		data.setStatus(paymentIntent.getStatus());
		
		return new ResponseEntity<PaymentDTO>(data,HttpStatus.ACCEPTED);
		
	}
	
	//Get All Payments In Database Here We Have Printed Only Top 3 Payments
	@GetMapping("/get_intents")
	public ResponseEntity<List<PaymentDTO>> getAllPayments(@RequestParam String apiKey) throws StripeException{
		
		Stripe.apiKey = apiKey;

		PaymentIntentListParams params =
		  PaymentIntentListParams.builder().setLimit(3L).build();

		PaymentIntentCollection paymentIntents = PaymentIntent.list(params);
		
		List<PaymentDTO> payments = new ArrayList<>();
		
		for(int i=0;i<paymentIntents.getData().size();i++) {
			
			Long amount = paymentIntents.getData().get(i).getAmount();
			String currency = paymentIntents.getData().get(i).getCurrency();
			String id = paymentIntents.getData().get(i).getId();
			String status = paymentIntents.getData().get(i).getStatus();
			String mail =  paymentIntents.getData().get(i).getReceiptEmail();
			
			PaymentDTO pay = new PaymentDTO(id, amount, currency, mail, status);
			payments.add(pay);
		}
		
		return new ResponseEntity<List<PaymentDTO>>(payments,HttpStatus.OK);
		
	}
	
	//Confirming Payment adds Card Details To PaymentId and status is changed from requires_payment_method to succeeded
	//before refund you must complete payment 
	@PostMapping("/confirm_intent")
	public ResponseEntity<String> confirmIntent(@RequestParam String paymentId,@RequestParam String apiKey) throws StripeException {
		Stripe.apiKey = apiKey;

		PaymentIntent resource = PaymentIntent.retrieve(paymentId);

		PaymentIntentConfirmParams params =
		  PaymentIntentConfirmParams.builder()
		    .setPaymentMethod(resource.getPaymentMethod())
		    .setReturnUrl("https://nileshSolanki.in")
		    .setReceiptEmail(resource.getReceiptEmail())
		    .build();

		resource.confirm(params);
		
		return new ResponseEntity<String>("Confirmed Successfully "+paymentId,HttpStatus.OK);
	}
	
//	public String updateIntent(@RequestParam String paymentId) throws StripeException {
//		Stripe.apiKey = apiKey;
//
//		PaymentIntent resource = PaymentIntent.retrieve(paymentId);
//		
//		resource.setStatus();
//		PaymentIntentUpdateParams params =
//		  PaymentIntentUpdateParams.builder().putMetadata("order_id", "6735").build();
//
//		PaymentIntent paymentIntent = resource.update(params);
//		
//		return "Success";
//	}
	
	@PostMapping("/capture_intent")
	public ResponseEntity<String> captureIntent(@RequestParam String paymentId,@RequestParam Long amt,@RequestParam String apiKey) throws StripeException {
		Stripe.apiKey = apiKey;


		PaymentIntent resource = PaymentIntent.retrieve(paymentId);
		
		PaymentIntentCaptureParams params = PaymentIntentCaptureParams.builder()
				.setAmountToCapture(amt)
				.build();
		
		PaymentIntent paymentIntent = resource.capture(params);

		return new ResponseEntity<String>("Succesfully Captured !"+ paymentIntent,HttpStatus.OK);
		
		
	}
	
	//after capturing or confirming payment you can apply for refund
	@PostMapping("/create_refund")
	public ResponseEntity<String> refund(@RequestParam String paymentId,@RequestParam String apiKey) throws StripeException {
		Stripe.apiKey = apiKey;

		PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentId);
		
		RefundCreateParams params =
		  RefundCreateParams.builder().setCharge(paymentIntent.getLatestCharge()).build();

		Refund.create(params);
//		System.err.println(refund);
		return new ResponseEntity<String>("Refunded Successfully "+paymentId,HttpStatus.ACCEPTED);
	}
	
	
	
//	@PostMapping("/card")
//	public CardDTO createCard(@RequestBody CardDTO model) throws StripeException {
//		Stripe.apiKey = apiKey;
//		Map<String, Object> card = new HashMap<>();
//		
//		card.put("number",model.getCardNumber());
//		card.put("exp_month",Integer.parseInt(model.getExpMonth()));
//		card.put("exp_year",Integer.parseInt(model.getExpYear()));
//		card.put("cvc",model.getCvc());
//		
//		Map<String, Object> params = new HashMap<>();
//		params.put("card", card);
//		Token token =  Token.create(params);
//		
//		if(token != null && token.getId() != null) {
//			model.setSuccess(true);
//			model.setToken(token.getId());
//		}
//		
//		return model;
//		
//
//	}
	
	
}
