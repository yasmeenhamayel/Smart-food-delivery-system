
package com.smartdelivery.service;

import com.smartdelivery.domain.*;
import com.smartdelivery.observer.OrderObserver;
import com.smartdelivery.strategy.DriverSelectionStrategy;

import java.util.List;

public class OrderService {

    private final OrderObserver notificationObserver;

    public OrderService(OrderObserver notificationObserver) {
        if (notificationObserver == null) {
            throw new IllegalArgumentException("Notification observer cannot be null");
        }
        this.notificationObserver = notificationObserver;
    }

    public Order placeOrder(Restaurant restaurant, List<OrderItem> items, Address address) {
        if (restaurant == null) {
            throw new IllegalArgumentException("Null Restaurant");
        }

        if (address == null) {
            throw new IllegalArgumentException("Null Address");
        }

        Order order = new Order(items);

        if (!restaurant.getDeliveryAreas().contains(address.getCity())) {
            throw new IllegalArgumentException(
                    "The Order Rejected , Your area isn’t covered by the restaurant’s delivery areas"
            );
        }

        if (order.getTotal() < restaurant.getMinOrderValue()) {
            throw new IllegalArgumentException(
                    "The Order Rejected , Your total order less than the minimum order value in this restaurant"
            );
        }

        order.addObserver(notificationObserver);

        return order;
    }

    public void cancelOrder(Order order) {
        if (order == null) {
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

