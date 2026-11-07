
package com.smartdelivery.domain;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduledOrderTest {

    private final Clock fixedClock = Clock.fixed(
            Instant.parse("2026-11-07T10:00:00Z"),
            ZoneOffset.UTC
    );

    @Test
    void createsScheduledOrderWithFutureDeliveryTime() {

        LocalDateTime deliveryTime =
                LocalDateTime.of(2026, 11, 7, 12, 0);

        OrderItem item = new OrderItem(
                new MenuItem("Pizza", 15, true), 2
        );

        ScheduledOrder order = new ScheduledOrder(
                List.of(item), 5, deliveryTime, fixedClock
        );

        assertNotNull(order);
        assertEquals(deliveryTime, order.getScheduledDeliveryTime());
        assertEquals(35.0, order.getTotal(), 0.001);
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void rejectsPastDeliveryTime() {

        LocalDateTime deliveryTime =
                LocalDateTime.of(2026, 11, 7, 9, 0);

        OrderItem item = new OrderItem(
                new MenuItem("Pizza", 15, true), 1
        );

        assertThrows(IllegalArgumentException.class,
                () -> new ScheduledOrder(
                        List.of(item), 5, deliveryTime, fixedClock
                ));
    }

    @Test
    void rejectsCurrentDeliveryTime() {

        LocalDateTime deliveryTime =
                LocalDateTime.of(2026, 11, 7, 10, 0);

        OrderItem item = new OrderItem(
                new MenuItem("Pizza", 15, true), 1
        );

        assertThrows(IllegalArgumentException.class,
                () -> new ScheduledOrder(
                        List.of(item), 5, deliveryTime, fixedClock
                ));
    }

    @Test
    void rejectsNullDeliveryTime() {

        OrderItem item = new OrderItem(
                new MenuItem("Pizza", 15, true), 1
        );

        assertThrows(IllegalArgumentException.class,
                () -> new ScheduledOrder(
                        List.of(item), 5, null, fixedClock
                ));
    }

    @Test
    void rejectsNullClock() {

        OrderItem item = new OrderItem(
                new MenuItem("Pizza", 15, true), 1
        );

        LocalDateTime deliveryTime =
                LocalDateTime.of(2026, 11, 7, 12, 0);

        assertThrows(IllegalArgumentException.class,
                () -> new ScheduledOrder(
                        List.of(item), 5, deliveryTime, null
                ));
    }

    @Test
    void acceptsDeliveryTimeJustAfterCurrentTime() {

        LocalDateTime deliveryTime =
                LocalDateTime.of(2026, 11, 7, 10, 0, 0, 1);

        OrderItem item = new OrderItem(
                new MenuItem("Pizza", 15, true), 1
        );

        ScheduledOrder order = new ScheduledOrder(
                List.of(item), 5, deliveryTime, fixedClock
        );

        assertEquals(deliveryTime, order.getScheduledDeliveryTime());
    }

    @Test
    void rejectsDeliveryTimeJustBeforeCurrentTime() {

        LocalDateTime deliveryTime =
                LocalDateTime.of(2026, 11, 7, 9, 59, 59, 999999999);

        OrderItem item = new OrderItem(
                new MenuItem("Pizza", 15, true), 1
        );

        assertThrows(IllegalArgumentException.class,
                () -> new ScheduledOrder(
                        List.of(item), 5, deliveryTime, fixedClock
                ));
    }

    @Test
    void rejectsPastDeliveryTimeOnPreviousDay() {

        LocalDateTime deliveryTime =
                LocalDateTime.of(2026, 11, 6, 23, 0);

        OrderItem item = new OrderItem(
                new MenuItem("Pizza", 15, true), 1
        );

        assertThrows(IllegalArgumentException.class,
                () -> new ScheduledOrder(
                        List.of(item), 5, deliveryTime, fixedClock
                ));
    }

    @Test
    void rejectsEmptyScheduledOrder() {

        LocalDateTime deliveryTime =
                LocalDateTime.of(2026, 11, 7, 12, 0);

        assertThrows(IllegalArgumentException.class,
                () -> new ScheduledOrder(
                        List.of(), 5, deliveryTime, fixedClock
                ));
    }

    @Test
    void scheduledOrderDoesNotChangeDeliveryCost() {

        LocalDateTime deliveryTime =
                LocalDateTime.of(2026, 11, 8, 12, 0);

        OrderItem item = new OrderItem(
                new MenuItem("Pizza", 15, true), 1
        );

        ScheduledOrder order = new ScheduledOrder(
                List.of(item), 5, deliveryTime, fixedClock
        );

        assertEquals(5.0, order.getDeliveryCost(), 0.001);
    }

    @Test
    void scheduledOrderIsAnOrderAndFollowsNormalLifecycle() {

        LocalDateTime deliveryTime =
                LocalDateTime.of(2026, 11, 7, 12, 0);

        OrderItem item = new OrderItem(
                new MenuItem("Pizza", 15, true), 1
        );

        Order order = new ScheduledOrder(
                List.of(item), 5, deliveryTime, fixedClock
        );

        order.confirm();
        order.startPreparing();
        order.markReady();
        order.markOutForDelivery();
        order.markDelivered();

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }
}
