package com.stripe.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.model.PaymentSource;
import com.stripe.models.CustomerDTO;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import com.stripe.param.PaymentSourceCollectionCreateParams;

@RestController
@RequestMapping("/api/v1")
public class CustomerController {
	
	@PostMapping("/customers")
	public ResponseEntity<CustomerDTO> addCustomer(@RequestBody CustomerDTO data,@RequestParam String apiKey) throws StripeException {
		Stripe.apiKey = apiKey;

		CustomerCreateParams params =
		  CustomerCreateParams.builder()
		    .setName(data.getName())
		    .setEmail(data.getEmail())
		    .setSource("tok_visa")
		    .build();

		Customer customer = Customer.create(params);
		
		data.setCustomerId(customer.getId());
		
		return new ResponseEntity<CustomerDTO>(data,HttpStatus.ACCEPTED);
	}
	
//	@PutMapping("/customers")
//	public CustomerDTO updateCustomer(@RequestParam String id,@RequestParam String apiKey) throws StripeException {
//		Stripe.apiKey = apiKey;
//
//		Customer resource = Customer.retrieve(id);
//
//		CustomerUpdateParams params =
//		  CustomerUpdateParams.builder().putMetadata("order_id", "6735").build();
//
//		Customer customer = resource.update(params);
//		
//		CustomerDTO update = new CustomerDTO();
//		update.setName(customer.getName());
//		update.setEmail(customer.getEmail());
//		update.setCustomerId(id);
//		
//		return update;
//	}
	
	@GetMapping("/customer")
	public ResponseEntity<CustomerDTO> getCustomer(@RequestParam String id,@RequestParam String apiKey) throws StripeException {
		Stripe.apiKey = apiKey;

		Customer customer = Customer.retrieve(id);
		CustomerDTO data = new CustomerDTO(customer.getName(), customer.getEmail(), customer.getId());
		
		return new ResponseEntity<CustomerDTO>(data,HttpStatus.ACCEPTED);
	}
	
	@DeleteMapping("/customers")
	public ResponseEntity<String> deleteCustomer(@RequestParam String id,@RequestParam String apiKey) throws StripeException {
		Stripe.apiKey =apiKey;

		Customer resource = Customer.retrieve(id);
		resource.delete();
		
		
		return new ResponseEntity<String>(id+"Deleted Succefully !",HttpStatus.OK);
	}
	
	@GetMapping("/customers")
	public ResponseEntity<List<CustomerDTO>> getAllCustomers(@RequestParam String apiKey) throws StripeException{
		Stripe.apiKey = apiKey;

		CustomerListParams params = CustomerListParams.builder().setLimit(3L).build();

		CustomerCollection customers = Customer.list(params);
		
		List<CustomerDTO> all = new ArrayList<>();
		
		for(int i=0;i<customers.getData().size();i++) {
			String name = customers.getData().get(i).getName();
			String email = customers.getData().get(i).getEmail();
			String id = customers.getData().get(i).getId();
			CustomerDTO data = new CustomerDTO(name, email, id);
			
			all.add(data);
		}
		
		return new ResponseEntity<List<CustomerDTO>>(all,HttpStatus.OK);
		
	}
	

}
