package com.smartdelivery.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StandardOrderTest {

    @Test
    void createsStandardOrderSuccessfully() {
        MenuItem item = new MenuItem("Pizza", 12.5, true);
        OrderItem orderItem = new OrderItem(item, 2);

        StandardOrder order = new StandardOrder(
                List.of(orderItem), 5.0
        );

        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(5.0, order.getDeliveryCost());
        assertEquals(30.0, order.getTotal());
    }

    @Test
    void rejectsEmptyOrder() {
        assertThrows(IllegalArgumentException.class, () ->
                new StandardOrder(List.of(), 5.0)
        );
    }

    @Test
    void rejectsInvalidDeliveryCost() {
        MenuItem item = new MenuItem("Pizza", 12.5, true);
        OrderItem orderItem = new OrderItem(item, 2);

        assertThrows(IllegalArgumentException.class, () ->
                new StandardOrder(List.of(orderItem), -5.0)
        );
    }
}