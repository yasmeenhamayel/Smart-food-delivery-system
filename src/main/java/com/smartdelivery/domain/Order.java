package com.smartdelivery.domain;
import java.util.List;
import java.util.ArrayList;

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
		
		this.items = new ArrayList<>(items);
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
