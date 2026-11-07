package com.smartdelivery.service;

import com.smartdelivery.domain.*;
import com.smartdelivery.observer.OrderObserver;
import com.smartdelivery.pricing.PricingStrategy;
import com.smartdelivery.strategy.DriverSelectionStrategy;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private final OrderObserver notificationObserver;
    private final DeliveryCostCalculator deliveryCostCalculator;

    public OrderService(OrderObserver notificationObserver,
                        DeliveryCostCalculator deliveryCostCalculator) {

        if (notificationObserver == null) {
            throw new IllegalArgumentException("Notification observer cannot be null");
        }

        if (deliveryCostCalculator == null) {
            throw new IllegalArgumentException("Delivery cost calculator cannot be null");
        }

        this.notificationObserver = notificationObserver;
        this.deliveryCostCalculator = deliveryCostCalculator;
    }

    // US4.1 and US4.2: Original method - kept for backward compatibility
    public Order placeOrder(
            Restaurant restaurant,
            List<OrderItem> items,
            Address address,
            double distance) {

        validateRestaurantAndAddress(restaurant, address);

        double deliveryCost =
                deliveryCostCalculator.calculateDeliveryCost(distance);

        Order order = new Order(items, deliveryCost);

        return completeOrder(restaurant, address, order);
    }

    // Sprint 5: Create a standard order
    public StandardOrder placeStandardOrder(
            Restaurant restaurant,
            List<OrderItem> items,
            Address address,
            double distance) {

        validateRestaurantAndAddress(restaurant, address);

        double deliveryCost =
                deliveryCostCalculator.calculateDeliveryCost(distance);

        StandardOrder order = new StandardOrder(items, deliveryCost);

        return completeOrder(restaurant, address, order);
    }

    // Sprint 5: Create an express order
    public ExpressOrder placeExpressOrder(
            Restaurant restaurant,
            List<OrderItem> items,
            Address address,
            double distance,
            double expressFee) {

        validateRestaurantAndAddress(restaurant, address);

        double deliveryCost =
                deliveryCostCalculator.calculateDeliveryCost(distance);

        ExpressOrder order =
                new ExpressOrder(items, deliveryCost, expressFee);

        return completeOrder(restaurant, address, order);
    }

    // Sprint 5: Create a scheduled order
    public ScheduledOrder placeScheduledOrder(
            Restaurant restaurant,
            List<OrderItem> items,
            Address address,
            double distance,
            LocalDateTime scheduledDeliveryTime) {

        return placeScheduledOrder(
                restaurant,
                items,
                address,
                distance,
                scheduledDeliveryTime,
                Clock.systemDefaultZone()
        );
    }

    // Constructor with Clock to make date/time handling testable
    public ScheduledOrder placeScheduledOrder(
            Restaurant restaurant,
            List<OrderItem> items,
            Address address,
            double distance,
            LocalDateTime scheduledDeliveryTime,
            Clock clock) {

        validateRestaurantAndAddress(restaurant, address);

        double deliveryCost =
                deliveryCostCalculator.calculateDeliveryCost(distance);

        ScheduledOrder order = new ScheduledOrder(
                items,
                deliveryCost,
                scheduledDeliveryTime,
                clock
        );

        return completeOrder(restaurant, address, order);
    }

    // Validate common restaurant and address requirements
    private void validateRestaurantAndAddress(
            Restaurant restaurant,
            Address address) {

        if (restaurant == null) {
            throw new IllegalArgumentException("Null Restaurant");
        }

        if (address == null) {
            throw new IllegalArgumentException("Null Address");
        }
    }

    // Complete order validation and register the observer
    private <T extends Order> T completeOrder(
            Restaurant restaurant,
            Address address,
            T order) {

        if (!restaurant.getDeliveryAreas().contains(address.getCity())) {
            throw new IllegalArgumentException(
                    "The Order Rejected, Your area isn't covered by the restaurant's delivery areas"
            );
        }

        if (order.getItemTotal() < restaurant.getMinOrderValue()) {
            throw new IllegalArgumentException(
                    "The Order Rejected, Your total order is less than the minimum order value"
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

    // US3.1: Assign a specific driver to an order
    public void assignDriver(Order order, Driver driver) {

        if (order == null) {
            throw new IllegalArgumentException("Null Order");
        }

        if (driver == null) {
            throw new IllegalArgumentException("Null Driver");
        }

        order.assignDriver(driver);
    }

    // US3.2: Original method - kept for backward compatibility
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

    // Sprint 5: Select a driver who can handle the order
    public Driver assignDriverUsingStrategy(
            Order order,
            List<Driver> drivers,
            double restaurantLatitude,
            double restaurantLongitude,
            double distance,
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

        if (distance <= 0 || !Double.isFinite(distance)) {
            throw new IllegalArgumentException("Invalid delivery distance");
        }

        List<Driver> eligibleDrivers = new ArrayList<>();

        for (Driver driver : drivers) {
            if (driver != null &&
                    driver.canHandleDelivery(order, distance)) {
                eligibleDrivers.add(driver);
            }
        }

        Driver selectedDriver = strategy.selectDriver(
                eligibleDrivers,
                restaurantLatitude,
                restaurantLongitude
        );

        if (selectedDriver == null) {
            throw new IllegalStateException(
                    "No available driver capable of handling this order"
            );
        }

        assignDriver(order, selectedDriver);

        return selectedDriver;
    }

    /**
     * US4.3: Calculates the final price of an order using the given pricing
     * strategy.
     *
     * <p>The strategy is applied to the food subtotal only, and the delivery
     * cost is then added unchanged. The delivery cost of an express order
     * already contains the Express surcharge, so the surcharge is counted
     * exactly once. The order is not modified, and the result can differ from
     * {@link Order#getTotal()}, which is not affected by pricing strategies.</p>
     *
     * @param order the order to price
     * @param strategy the strategy applied to the food subtotal
     * @return the priced food subtotal plus the delivery cost
     */
    public double calculateTotalWithPricing(
            Order order,
            PricingStrategy strategy) {

        if (order == null) {
            throw new IllegalArgumentException("Null Order");
        }

        if (strategy == null) {
            throw new IllegalArgumentException("Null Pricing Strategy");
        }

        return strategy.calculatePrice(order.getItemTotal())
                + order.getDeliveryCost();
    }
}
