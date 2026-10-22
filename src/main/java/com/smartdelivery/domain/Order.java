package com.smartdelivery.domain;
import java.util.List;

public class Order {
	private List<OrderItem> items ;
	private OrderStatus status;
	
	public Order(List<OrderItem>items){
		if(items== null) {
			throw new IllegalArgumentException("Null Order");
		}
		if(items.isEmpty()) {
			throw new IllegalArgumentException("The Order is Empty");
		}
		
		this.items = items;
		this.status= OrderStatus.PENDING;	
	}
	
	
	
	public double getTotal(){
		double total =0;
		
		for(OrderItem item : items) {
			total += item.getSubtotal();
		}
		return total;
	}
	
	
	public OrderStatus getStatus() {
		return status;
	}

	
}
