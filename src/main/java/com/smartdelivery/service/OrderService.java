package com.smartdelivery.service;

import com.smartdelivery.domain.*;
import java.util.*;

public class OrderService {
	
	
	public Order placeOrder(Restaurant restaurant , List<OrderItem> items , Address address ) {
		if(restaurant == null) {
			throw new IllegalArgumentException("Null Restaurant");
		}
		if(address == null) {
			throw new IllegalArgumentException("Null Address");
		}
		Order order = new Order(items);
		
		if(!restaurant.getDeliveryAreas().contains(address.getCity())) {
			throw new IllegalArgumentException("The Order Rejected , Your area isn’t covered by the restaurant’s delivery areas");
		}
		
		if(order.getTotal() < restaurant.getMinOrderValue()) {
			throw new IllegalArgumentException("The Order Rejected , Your total order less than the minimum order value in this restaurant");
		}
		return order;
		
		
	}

}
