package com.smartdelivery.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExpressOrderTest {

    @Test
    void createsExpressOrderWithAdditionalFee() {
        MenuItem item = new MenuItem("Pizza", 12.5, true);
        OrderItem orderItem = new OrderItem(item, 2);

        ExpressOrder order = new ExpressOrder(
                List.of(orderItem), 5.0, 3.0
        );

        assertEquals(8.0, order.getDeliveryCost(), 0.001);
        assertEquals(33.0, order.getTotal(), 0.001);
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void acceptsZeroExpressFee() {
        MenuItem item = new MenuItem("Pizza", 12.5, true);
        OrderItem orderItem = new OrderItem(item, 2);

        ExpressOrder order = new ExpressOrder(
                List.of(orderItem), 5.0, 0.0
        );

        assertEquals(5.0, order.getDeliveryCost(), 0.001);
    }

    @Test
    void rejectsNegativeExpressFee() {
        MenuItem item = new MenuItem("Pizza", 12.5, true);
        OrderItem orderItem = new OrderItem(item, 2);

        assertThrows(IllegalArgumentException.class, () ->
                new ExpressOrder(List.of(orderItem), 5.0, -3.0)
        );
    }

    @Test
    void rejectsInvalidExpressFee() {
        MenuItem item = new MenuItem("Pizza", 12.5, true);
        OrderItem orderItem = new OrderItem(item, 2);

        assertThrows(IllegalArgumentException.class, () ->
                new ExpressOrder(
                        List.of(orderItem), 5.0, Double.NaN
                )
        );
    }
}