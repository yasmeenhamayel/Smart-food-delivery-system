
package com.smartdelivery.service;

import com.smartdelivery.domain.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

    private Order createOrder() {
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 2);
        return new Order(List.of(item));
    }

    @Test
    void notifiesCustomerWhenDriverIsAssigned() {
        NotificationService service = new NotificationService();
        Order order = createOrder();
        Driver driver = new Driver(
                "D1", "Ahmad", 32.2211, 35.2544, 15
        );

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));
            service.onDriverAssigned(order, driver);
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(output.toString(StandardCharsets.UTF_8)
                .contains("A driver has been assigned"));
    }

    @Test
    void notifiesCustomerWhenOrderIsOutForDelivery() {
        NotificationService service = new NotificationService();
        Order order = createOrder();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));
            service.onOrderStatusChanged(
                    order, OrderStatus.OUT_FOR_DELIVERY
            );
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(output.toString(StandardCharsets.UTF_8)
                .contains("out for delivery"));
    }

    @Test
    void notifiesCustomerWhenOrderIsDelivered() {
        NotificationService service = new NotificationService();
        Order order = createOrder();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));
            service.onOrderStatusChanged(
                    order, OrderStatus.DELIVERED
            );
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(output.toString(StandardCharsets.UTF_8)
                .contains("has been delivered"));
    }

    @Test
    void notifiesCustomerWhenOrderStatusChanges() {
        NotificationService service = new NotificationService();
        Order order = createOrder();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));
            service.onOrderStatusChanged(
                    order, OrderStatus.CONFIRMED
            );
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(output.toString(StandardCharsets.UTF_8)
                .contains("Order status changed to CONFIRMED"));
    }
}
