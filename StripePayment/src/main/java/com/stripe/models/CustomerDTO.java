package com.stripe.models;

public class CustomerDTO {
	
	public String name;
	public String email;
	public String customerId;
	
	
	
	public CustomerDTO() {
		super();
	}
	public CustomerDTO(String name, String email, String customerId) {
		super();
		this.name = name;
		this.email = email;
		this.customerId = customerId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	
	
	

}
