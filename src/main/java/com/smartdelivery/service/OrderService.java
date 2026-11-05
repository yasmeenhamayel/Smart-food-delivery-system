
package com.smartdelivery.service;

import com.smartdelivery.observer.OrderObserver;
import com.smartdelivery.strategy.DriverSelectionStrategy;
import java.util.*;

import com.smartdelivery.domain.*;
import java.util.*;
import pricing.PricingStrategy;

public class OrderService {
	private DeliveryCostCalculator calculator;
	private final OrderObserver notificationObserver;
	

	public OrderService(DeliveryCostCalculator calculator) {
        if (calculator == null) {
            throw new IllegalArgumentException("Null Calculator");
        }
        this.calculator = calculator;
        this.notificationObserver = null;
    }

	public OrderService(OrderObserver notificationObserver) {
        if (notificationObserver == null) {
            throw new IllegalArgumentException("Notification observer cannot be null");
        }
        this.notificationObserver = notificationObserver;
        this.calculator = null;
    }

	
	public OrderService(DeliveryCostCalculator calculator , OrderObserver notificationObserver ) {
		 if (calculator == null) {
		        throw new IllegalArgumentException("Null Calculator");
		    }
	    this.calculator = calculator;
		 if (notificationObserver == null) {
            throw new IllegalArgumentException("Notification observer cannot be null");
        }
        this.notificationObserver = notificationObserver;
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
		if (notificationObserver != null) {
            order.addObserver(notificationObserver);
        }
		 order.addObserver(notificationObserver);
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
	
    // US3.1: Assign a specific driver to an order.
    public void assignDriver(Order order, Driver driver) {
        if (order == null) {
            throw new IllegalArgumentException("Null Order");
        }

        if (driver == null) {
            throw new IllegalArgumentException("Null Driver");
        }

        order.assignDriver(driver);
    }

    // US3.2: Select a driver according to the given strategy.
    public Driver assignDriverUsingStrategy(
            Order order,
            List<Driver> drivers,
            double restaurantLatitude,
            double restaurantLongitude,
            DriverSelectionStrategy strategy) {

        if (order == null) {
            throw new IllegalArgumentException("Null Order");
        }

        if (drivers == null) {
            throw new IllegalArgumentException("Null Drivers List");
        }

        if (strategy == null) {
            throw new IllegalArgumentException("Null Driver Selection Strategy");
        }

        Driver selectedDriver = strategy.selectDriver(
                drivers,
                restaurantLatitude,
                restaurantLongitude
        );

        if (selectedDriver == null) {
            throw new IllegalStateException("No available driver");
        }

        assignDriver(order, selectedDriver);

        return selectedDriver;
    }
}

