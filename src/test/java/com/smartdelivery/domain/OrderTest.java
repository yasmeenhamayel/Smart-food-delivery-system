
package com.smartdelivery.domain;

import com.smartdelivery.observer.OrderObserver;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderTest {

    private Order createOrder() {
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 2);
        return new Order(List.of(item), 0);
    }

    // =========================
    // Order Creation Tests
    // =========================

    @Test
    void createsOrderWithValidItems() {
        Order order = createOrder();

        assertNotNull(order);
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void rejectsOrderWithNullItems() {
        assertThrows(IllegalArgumentException.class,
                () -> new Order(null, 0));
    }

    @Test
    void rejectsOrderWithEmptyItems() {
        assertThrows(IllegalArgumentException.class,
                () -> new Order(new ArrayList<>(), 0));
    }

    @Test
    void rejectsOrderWithNullItem() {
        List<OrderItem> items = new ArrayList<>();
        items.add(null);

        assertThrows(IllegalArgumentException.class,
                () -> new Order(items, 0));
    }

    // =========================
    // US4.1 and US4.2: Order Total
    // =========================

    @Test
    void calculatesItemTotalCorrectly() {
        Order order = createOrder();

        assertEquals(30.0, order.getItemTotal());
    }

    @Test
    void calculatesOrderTotalIncludingDeliveryCost() {
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 2);

        Order order = new Order(List.of(item), 10);

        assertEquals(30.0, order.getItemTotal());
        assertEquals(10.0, order.getDeliveryCost());
        assertEquals(40.0, order.getTotal());
    }

    @Test
    void calculatesOrderTotalWithFreeDelivery() {
        Order order = createOrder();

        assertEquals(30.0, order.getTotal());
        assertEquals(0.0, order.getDeliveryCost());
    }

    @Test
    void rejectsNegativeDeliveryCost() {
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 2);

        assertThrows(IllegalArgumentException.class,
                () -> new Order(List.of(item), -5));
    }

    @Test
    void rejectsInfiniteDeliveryCost() {
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 2);

        assertThrows(IllegalArgumentException.class,
                () -> new Order(List.of(item), Double.POSITIVE_INFINITY));
    }

    @Test
    void rejectsNaNDeliveryCost() {
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 2);

        assertThrows(IllegalArgumentException.class,
                () -> new Order(List.of(item), Double.NaN));
    }

    // =========================
    // US4.1: Order Lifecycle
    // =========================

    @Test
    void cancelsPendingOrder() {
        Order order = createOrder();

        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void rejectsCancellingNonPendingOrder() {
        Order order = createOrder();
        order.confirm();

        assertThrows(IllegalStateException.class,
                order::cancel);
    }

    @Test
    void confirmsPendingOrder() {
        Order order = createOrder();

        order.confirm();

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void rejectsConfirmingNonPendingOrder() {
        Order order = createOrder();
        order.confirm();

        assertThrows(IllegalStateException.class,
                order::confirm);
    }

    @Test
    void startsPreparingConfirmedOrder() {
        Order order = createOrder();

        order.confirm();
        order.startPreparing();

        assertEquals(OrderStatus.PREPARING, order.getStatus());
    }

    @Test
    void rejectsPreparingOrderThatIsNotConfirmed() {
        Order order = createOrder();

        assertThrows(IllegalStateException.class,
                order::startPreparing);
    }

    @Test
    void marksPreparingOrderAsReady() {
        Order order = createOrder();

        order.confirm();
        order.startPreparing();
        order.markReady();

        assertEquals(OrderStatus.READY, order.getStatus());
    }

    @Test
    void rejectsMarkingOrderReadyBeforePreparing() {
        Order order = createOrder();

        assertThrows(IllegalStateException.class,
                order::markReady);
    }

    @Test
    void marksReadyOrderAsOutForDelivery() {
        Order order = createOrder();

        order.confirm();
        order.startPreparing();
        order.markReady();
        order.markOutForDelivery();

        assertEquals(OrderStatus.OUT_FOR_DELIVERY, order.getStatus());
    }

    @Test
    void rejectsMarkingOrderOutForDeliveryBeforeReady() {
        Order order = createOrder();

        assertThrows(IllegalStateException.class,
                order::markOutForDelivery);
    }

    @Test
    void marksOutForDeliveryOrderAsDelivered() {
        Order order = createOrder();

        order.confirm();
        order.startPreparing();
        order.markReady();
        order.markOutForDelivery();
        order.markDelivered();

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void rejectsMarkingOrderDeliveredBeforeOutForDelivery() {
        Order order = createOrder();

        assertThrows(IllegalStateException.class,
                order::markDelivered);
    }

    // =========================
    // Driver Tests
    // =========================

    @Test
    void rejectsNullDriver() {
        Order order = createOrder();

        assertThrows(IllegalArgumentException.class,
                () -> order.assignDriver(null));
    }

    @Test
    void assignsAvailableDriver() {
        Order order = createOrder();

        Driver driver = new Driver(
                "D1", "Ahmad", 32.2211, 35.2544, 15
        );

        order.assignDriver(driver);

        assertEquals(driver, order.getDriver());
        assertEquals(DriverStatus.BUSY, driver.getStatus());
    }

    @Test
    void rejectsAssigningBusyDriver() {
        Order order = createOrder();

        Driver driver = new Driver(
                "D1", "Ahmad", 32.2211, 35.2544, 15
        );

        driver.setStatus(DriverStatus.BUSY);

        assertThrows(IllegalStateException.class,
                () -> order.assignDriver(driver));
    }

    @Test
    void rejectsAssigningSecondDriver() {
        Order order = createOrder();

        Driver firstDriver = new Driver(
                "D1", "Ahmad", 32.2211, 35.2544, 15
        );

        Driver secondDriver = new Driver(
                "D2", "Ali", 32.2220, 35.2550, 20
        );

        order.assignDriver(firstDriver);

        assertThrows(IllegalStateException.class,
                () -> order.assignDriver(secondDriver));
    }

    @Test
    void rejectsAssigningDriverToDeliveredOrder() {
        Order order = createOrder();
        OrderObserver observer = mock(OrderObserver.class);

        Driver driver = new Driver(
                "D1", "Ahmad", 32.2211, 35.2544, 15
        );

        order.addObserver(observer);

        order.confirm();
        order.startPreparing();
        order.markReady();
        order.markOutForDelivery();
        order.markDelivered();

        assertThrows(IllegalStateException.class,
                () -> order.assignDriver(driver));
                assertEquals(OrderStatus.DELIVERED, order.getStatus());

        assertNull(order.getDriver());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
        verify(observer, never()).onDriverAssigned(any(), any());
    }

    // =========================
    // Observer Tests
    // =========================

    @Test
    void notifiesObserverWhenOrderIsConfirmed() {
        Order order = createOrder();
        OrderObserver observer = mock(OrderObserver.class);

        order.addObserver(observer);
        order.confirm();

        verify(observer).onOrderStatusChanged(
                order, OrderStatus.CONFIRMED
        );
    }

    @Test
    void notifiesObserverWhenOrderIsCancelled() {
        Order order = createOrder();
        OrderObserver observer = mock(OrderObserver.class);

        order.addObserver(observer);
        order.cancel();

        verify(observer).onOrderStatusChanged(
                order, OrderStatus.CANCELLED
        );
    }

    @Test
    void doesNotNotifyObserverWhenStatusChangeFails() {
        Order order = createOrder();
        OrderObserver observer = mock(OrderObserver.class);

        order.addObserver(observer);

        assertThrows(IllegalStateException.class,
                order::startPreparing);

        verify(observer, never()).onOrderStatusChanged(
                any(), any()
        );
    }

    @Test
    void notifiesObserverWhenDriverIsAssigned() {
        Order order = createOrder();
        OrderObserver observer = mock(OrderObserver.class);

        Driver driver = new Driver(
                "D1", "Ahmad", 32.2211, 35.2544, 15
        );

        order.addObserver(observer);
        order.assignDriver(driver);

        verify(observer).onDriverAssigned(order, driver);
    }

    @Test
    void notifiesObserverWhenOrderStartsPreparing() {
        Order order = createOrder();
        OrderObserver observer = mock(OrderObserver.class);

        order.addObserver(observer);

        order.confirm();
        order.startPreparing();

        verify(observer).onOrderStatusChanged(
                order, OrderStatus.PREPARING
        );
    }

    @Test
    void notifiesObserverWhenOrderBecomesReady() {
        Order order = createOrder();
        OrderObserver observer = mock(OrderObserver.class);

        order.addObserver(observer);

        order.confirm();
        order.startPreparing();
        order.markReady();

        verify(observer).onOrderStatusChanged(
                order, OrderStatus.READY
        );
    }

    @Test
    void notifiesObserverWhenOrderIsOutForDelivery() {
        Order order = createOrder();
        OrderObserver observer = mock(OrderObserver.class);

        order.addObserver(observer);

        order.confirm();
        order.startPreparing();
        order.markReady();
        order.markOutForDelivery();

        verify(observer).onOrderStatusChanged(
                order, OrderStatus.OUT_FOR_DELIVERY
        );
    }

    @Test
    void notifiesObserverWhenOrderIsDelivered() {
        Order order = createOrder();
        OrderObserver observer = mock(OrderObserver.class);

        order.addObserver(observer);

        order.confirm();
        order.startPreparing();
        order.markReady();
        order.markOutForDelivery();
        order.markDelivered();

        verify(observer).onOrderStatusChanged(
                order, OrderStatus.DELIVERED
        );
    }

    @Test
    void calculatesTotalWeightCorrectly() {
        MenuItem pizza = new MenuItem("Pizza", 12.5, true, 0.8);
        MenuItem burger = new MenuItem("Burger", 8.0, true, 0.5);

        OrderItem pizzaOrder = new OrderItem(pizza, 3);
        OrderItem burgerOrder = new OrderItem(burger, 2);

        Order order = new Order(
                List.of(pizzaOrder, burgerOrder),
                5.0
        );

        assertEquals(3.4, order.getTotalWeight(), 0.001);
    }
}
