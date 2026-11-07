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

    @Test
    void rejectsInfiniteExpressFee() {
        MenuItem item = new MenuItem("Pizza", 12.5, true);
        OrderItem orderItem = new OrderItem(item, 2);

        assertThrows(IllegalArgumentException.class, () ->
                new ExpressOrder(
                        List.of(orderItem), 5.0, Double.POSITIVE_INFINITY
                )
        );
    }

    @Test
    void rejectsEmptyExpressOrder() {
        assertThrows(IllegalArgumentException.class, () ->
                new ExpressOrder(List.of(), 5.0, 3.0)
        );
    }

    @Test
    void rejectsNegativeBaseDeliveryCost() {
        MenuItem item = new MenuItem("Pizza", 12.5, true);
        OrderItem orderItem = new OrderItem(item, 2);

        assertThrows(IllegalArgumentException.class, () ->
                new ExpressOrder(List.of(orderItem), -5.0, 3.0)
        );
    }

    @Test
    void expressFeeIsAddedExactlyOnce() {
        MenuItem item = new MenuItem("Pizza", 10, true);
        ExpressOrder order = new ExpressOrder(
                List.of(new OrderItem(item, 1)), 4.0, 2.5
        );

        assertEquals(6.5, order.getDeliveryCost(), 0.001);
        assertEquals(16.5, order.getTotal(), 0.001);
    }

    @Test
    void expressOrderIsAnOrderAndFollowsNormalLifecycle() {
        MenuItem item = new MenuItem("Pizza", 12.5, true);
        Order order = new ExpressOrder(
                List.of(new OrderItem(item, 2)), 5.0, 3.0
        );

        order.confirm();
        order.startPreparing();
        order.markReady();
        order.markOutForDelivery();
        order.markDelivered();

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
        assertEquals(8.0, order.getDeliveryCost(), 0.001);
    }
}
