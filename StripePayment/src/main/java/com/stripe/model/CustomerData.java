package com.stripe.model;

public class CustomerData {
	
	public String name;
	public String email;
	public String customerId;
	
	
	
	public CustomerData() {
		super();
	}
	public CustomerData(String name, String email, String customerId) {
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
