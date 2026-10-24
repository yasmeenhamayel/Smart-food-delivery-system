package com.smartdelivery.domain;

public class Address {
	
	private String city;
	private String area;
	private String street;
	private String details;
	
	public Address(String city ,String area , String street , String details) {
		if(city==null || city.isBlank()) {
			throw new IllegalArgumentException("City must be not empty");
		}
		city = city.strip();
		this .city = city;
		if(area==null || area.isBlank()) {
			throw new IllegalArgumentException("Area must be not empty");
		}
		area = area.strip();
		this .area = area;
		if(street==null || street.isBlank()) {
			throw new IllegalArgumentException("Street must be not empty");
		}
		
		street = street.strip();
		this .street = street;
		if(details!= null) {
			details = details.strip();
		}
		this .details = details;
		
	}
	
	
	public String getCity() {
		return city;
	}
	
	
	public String getArea() {
		return area;
	}
	
	
	public String getStreet() {
		return street;
	}
	
	
	
	public String getDetails() {
		return details;
	}
}
