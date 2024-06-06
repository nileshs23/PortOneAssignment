package com.stripe.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
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
import com.stripe.model.CustomerData;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import com.stripe.param.CustomerUpdateParams;

@RestController
@RequestMapping("/v1")
public class CustomerController {
	
	@Value("${stripe.key}")
	String apiKey;
	
	@GetMapping("/test")
	public String testint() {
		return "Tested Succesfully !"+ apiKey;
	}
	
	@PostMapping("/customers")
	public CustomerData addCustomer(@RequestBody CustomerData data) throws StripeException {
		Stripe.apiKey = apiKey;

		CustomerCreateParams params =
		  CustomerCreateParams.builder()
		    .setName(data.getName())
		    .setEmail(data.getEmail())
		    .build();

		Customer customer = Customer.create(params);
		data.setCustomerId(customer.getId());
		
		return data;
	}
	
	@PutMapping("/customers")
	public CustomerData updateCustomer(@RequestParam String id) throws StripeException {
		Stripe.apiKey = apiKey;

		Customer resource = Customer.retrieve(id);

		CustomerUpdateParams params =
		  CustomerUpdateParams.builder().putMetadata("order_id", "6735").build();

		Customer customer = resource.update(params);
		
		CustomerData update = new CustomerData();
		update.setName(customer.getName());
		update.setEmail(customer.getEmail());
		update.setCustomerId(id);
		
		return update;
	}
	
	@GetMapping("/customer")
	public CustomerData getCustomer(@RequestParam String id) throws StripeException {
		Stripe.apiKey = apiKey;

		Customer customer = Customer.retrieve(id);
		CustomerData data = new CustomerData(customer.getName(), customer.getEmail(), customer.getId());
		
		return data;
	}
	
	@DeleteMapping("/customers")
	public String deleteCustomer(@RequestParam String id) throws StripeException {
		Stripe.apiKey =apiKey;

		Customer resource = Customer.retrieve(id);

		Customer customer = resource.delete();
		
		
		return "Deleted Succefully !";
	}
	
	@GetMapping("/customers")
	public List<CustomerData> getAllCustomers() throws StripeException{
		Stripe.apiKey = apiKey;

		CustomerListParams params = CustomerListParams.builder().setLimit(3L).build();

		CustomerCollection customers = Customer.list(params);
		
		List<CustomerData> all = new ArrayList<>();
		
		for(int i=0;i<customers.getData().size();i++) {
			String name = customers.getData().get(i).getName();
			String email = customers.getData().get(i).getEmail();
			String id = customers.getData().get(i).getId();
			CustomerData data = new CustomerData(name, email, id);
			
			all.add(data);
		}
		
		return all;
		
	}
	

}