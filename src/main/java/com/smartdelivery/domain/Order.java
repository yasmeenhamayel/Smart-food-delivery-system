package com.smartdelivery.domain;

import java.util.List;
import java.util.ArrayList;

public class Order {
    private List<OrderItem> items;
    private OrderStatus status;
    private double deliveryCost;


    public Order(List<OrderItem> items, double deliveryCost) {
        if (items == null) {
            throw new IllegalArgumentException("Null Order");
        }

        if (items.isEmpty()) {
            throw new IllegalArgumentException("The Order is Empty");
        }

        if (deliveryCost < 0 || !Double.isFinite(deliveryCost)) {
            throw new IllegalArgumentException("Invalid delivery cost");
        }

        for (OrderItem item : items) {
            if (item == null) {
                throw new IllegalArgumentException("Order item cannot be null");
            }
        }

        this.items = new ArrayList<>(items);
        this.deliveryCost = deliveryCost;
        this.status = OrderStatus.PENDING;
    }
    
    
    public double getItemTotal() {
    	 double total = 0;

         for (OrderItem item : items) {
             total += item.getSubtotal();
         }

         return total;
    }

    public double getTotal() {
        return getItemTotal()+ deliveryCost;
    }

    public OrderStatus getStatus() {
        return status;
    }
    
    
    public double getDeliveryCost() {
        return deliveryCost;
    }
    
    
    public void cancel() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be cancelled");
        }

        status = OrderStatus.CANCELLED;
    }

    public void confirm() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException();
        }

        status = OrderStatus.CONFIRMED;
    }

    public void startPreparing() {
        if (status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException();
        }

        status = OrderStatus.PREPARING;
    }
    
    public void markReady() {
		if(status !=OrderStatus.PREPARING) {
			throw new IllegalStateException();
		}
		status = OrderStatus.READY;
	}
	
	public void markOutForDelivery() {
		if(status != OrderStatus.READY) {
			throw new IllegalStateException();
		}
		status = OrderStatus.OUT_FOR_DELIVERY;
	}
	
	public void markDelivered() {
		if(status != OrderStatus.OUT_FOR_DELIVERY) {
			throw new IllegalStateException();
		}
		status = OrderStatus.DELIVERED;
	}
}