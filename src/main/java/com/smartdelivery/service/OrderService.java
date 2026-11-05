package com.smartdelivery.service;

import com.smartdelivery.domain.*;
import java.util.*;
import pricing.PricingStrategy;

public class OrderService {
	private DeliveryCostCalculator calculator;
	
	
	
	public OrderService(DeliveryCostCalculator calculator ) {
		 if (calculator == null) {
		        throw new IllegalArgumentException("Null Calculator");
		    }
	    this.calculator = calculator;
	}
	
	
	public Order placeOrder(Restaurant restaurant , List<OrderItem> items , Address address,double distance ) {
		if(restaurant == null) {
			throw new IllegalArgumentException("Null Restaurant");
		}
		if(address == null) {
			throw new IllegalArgumentException("Null Address");
		}
		double deliveryCost = calculator.calculateDeliveryCost(distance);
		Order order = new Order(items ,deliveryCost );
		
		if(!restaurant.getDeliveryAreas().contains(address.getCity())) {
			throw new IllegalArgumentException("The Order Rejected , Your area isn’t covered by the restaurant’s delivery areas");
		}
		
		if(order.getItemTotal() < restaurant.getMinOrderValue()) {
			throw new IllegalArgumentException("The Order Rejected , Your total order less than the minimum order value in this restaurant");
		}
		return order;	
	}
	
	
	
	public double calculateFinalPrice(Order order, PricingStrategy strategy) {

	    if (order == null || strategy == null) {
	        throw new IllegalArgumentException("Null Order or Pricing Strategy");
	    }

	    return strategy.calculatePrice(order.getTotal());
	}
	
	
	public void cancelOrder(Order order) {
		if(order == null) {
			throw new IllegalArgumentException("Null Order");
		}
		order.cancel();
	}
	
	
	

}
