package com.smartdelivery.service;

import com.smartdelivery.domain.Order;
import com.smartdelivery.domain.OrderStatus;
import com.smartdelivery.domain.Driver;
import com.smartdelivery.observer.OrderObserver;

public class NotificationService implements OrderObserver {

    @Override
    public void onOrderStatusChanged(Order order, OrderStatus newStatus) {
        if (newStatus == OrderStatus.OUT_FOR_DELIVERY) {
            System.out.println(
                    "Notification: Your order is out for delivery"
            );
        } else if (newStatus == OrderStatus.DELIVERED) {
            System.out.println(
                    "Notification: Your order has been delivered"
            );
        } else {
            System.out.println(
                    "Notification: Order status changed to " + newStatus
            );
        }
    }

    @Override
    public void onDriverAssigned(Order order, Driver driver) {
        System.out.println(
                "Notification: A driver has been assigned to your order"
        );
    }
}