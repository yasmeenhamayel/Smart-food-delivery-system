
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

    /**
     * Assigns a driver when the delivery distance is not known.
     *
     * <p>Only drivers whose eligibility can be confirmed from the order alone
     * are accepted: plain drivers, and car drivers whose capacity covers the
     * order weight. Bicycle, motorcycle and electric vehicle drivers depend on
     * the delivery distance, so they must be assigned with
     * {@link #assignDriver(Driver, double)}.</p>
     *
     * @param driver the driver to assign
     * @throws IllegalArgumentException if the driver is null
     * @throws IllegalStateException if the order cannot be modified, already
     *         has a driver, the driver is not available, or the driver is not
     *         eligible without a distance
     */
    public void assignDriver(Driver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("Driver cannot be null");
        }

        checkCanAssign(driver);

        if (!driver.canHandleOrderWithoutDistance(this)) {
            throw new IllegalStateException(
                    "Driver is not eligible for this order without a delivery distance");
        }

        completeAssignment(driver);
    }

    /**
     * Assigns a driver after checking that the driver's vehicle type can
     * handle a delivery of the given distance.
     *
     * @param driver the driver to assign
     * @param distance the delivery distance in kilometres
     * @throws IllegalArgumentException if the driver is null or the distance
     *         is not positive and finite
     * @throws IllegalStateException if the order cannot be modified, already
     *         has a driver, the driver is not available, or the driver's
     *         vehicle cannot handle this delivery
     */
    public void assignDriver(Driver driver, double distance) {
        if (driver == null) {
            throw new IllegalArgumentException("Driver cannot be null");
        }

        if (distance <= 0 || !Double.isFinite(distance)) {
            throw new IllegalArgumentException("Invalid delivery distance");
        }

        checkCanAssign(driver);

        if (!driver.canHandleDelivery(this, distance)) {
            throw new IllegalStateException("Driver cannot handle this delivery");
        }

        completeAssignment(driver);
    }

    // Checks the order and driver state shared by every assignment path
    private void checkCanAssign(Driver driver) {
        if (status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Delivered orders cannot be modified");
        }

        if (this.driver != null) {
            throw new IllegalStateException("Order already has a driver");
        }

        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            throw new IllegalStateException("Driver is not available");
        }
    }

    // Records the assignment, marks the driver busy and notifies observers
    private void completeAssignment(Driver driver) {
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
