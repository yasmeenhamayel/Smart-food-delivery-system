
package com.smartdelivery.domain;

import com.smartdelivery.observer.OrderObserver;
import java.util.List;
import java.util.ArrayList;

public class Order {

    private List<OrderItem> items;
    private OrderStatus status;
    private double deliveryCost;
    private Driver driver;
    private List<OrderObserver> observers = new ArrayList<>();

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
        this.driver = null;
    }

    public double getItemTotal() {
        double total = 0;

        for (OrderItem item : items) {
            total += item.getSubtotal();
        }

        return total;
    }

    public double getTotal() {
        return getItemTotal() + deliveryCost;
    }

    public double getDeliveryCost() {
        return deliveryCost;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void addObserver(OrderObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }

        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    private void notifyObservers() {
        for (OrderObserver observer : observers) {
            observer.onOrderStatusChanged(this, status);
        }
    }

    private void notifyDriverAssigned() {
        for (OrderObserver observer : observers) {
            observer.onDriverAssigned(this, driver);
        }
    }

    public void cancel() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be cancelled");
        }

        status = OrderStatus.CANCELLED;
        notifyObservers();
    }

    public void confirm() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException();
        }

        status = OrderStatus.CONFIRMED;
        notifyObservers();
    }

    public void startPreparing() {
        if (status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException();
        }

        status = OrderStatus.PREPARING;
        notifyObservers();
    }

    public void markReady() {
        if (status != OrderStatus.PREPARING) {
            throw new IllegalStateException();
        }

        status = OrderStatus.READY;
        notifyObservers();
    }

    public void markOutForDelivery() {
        if (status != OrderStatus.READY) {
            throw new IllegalStateException();
        }

        status = OrderStatus.OUT_FOR_DELIVERY;
        notifyObservers();
    }

    public void markDelivered() {
        if (status != OrderStatus.OUT_FOR_DELIVERY) {
            throw new IllegalStateException();
        }

        status = OrderStatus.DELIVERED;
        notifyObservers();
    }

    public void assignDriver(Driver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("Driver cannot be null");
        }

        if (this.driver != null) {
            throw new IllegalStateException("Order already has a driver");
        }

        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            throw new IllegalStateException("Driver is not available");
        }

        this.driver = driver;
        driver.setStatus(DriverStatus.BUSY);
        notifyDriverAssigned();
    }

    public Driver getDriver() {
        return driver;
    }

    public double getTotalWeight() {
    double totalWeight = 0;

    for (OrderItem item : items) {
        totalWeight += item.getTotalWeight();
    }

    return totalWeight;
}
}
