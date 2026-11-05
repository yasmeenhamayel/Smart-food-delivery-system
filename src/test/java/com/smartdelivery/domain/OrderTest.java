
package com.smartdelivery.domain;

import com.smartdelivery.observer.OrderObserver;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderTest {

    private Order createOrder() {
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 2);
        return new Order(List.of(item), 0);
    }

    // Order creation and validation

    @Test
    void initializesOrderWithPendingStatus() {
        MenuItem item1 = new MenuItem("Faheta", 8, true);
        OrderItem orderitem1 = new OrderItem(item1, 5);

        MenuItem item2 = new MenuItem("Burger", 10, true);
        OrderItem orderitem2 = new OrderItem(item2, 2);

        MenuItem item3 = new MenuItem("Pizza", 12.5, true);
        OrderItem orderitem3 = new OrderItem(item3, 1);

        List<OrderItem> items = new ArrayList<>();
        items.add(orderitem1);
        items.add(orderitem2);
        items.add(orderitem3);

        Order order = new Order(items, 0);

        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void calculatesTotalCorrectly() {
        MenuItem item1 = new MenuItem("Faheta", 8, true);
        OrderItem orderitem1 = new OrderItem(item1, 5);

        MenuItem item2 = new MenuItem("Burger", 10, true);
        OrderItem orderitem2 = new OrderItem(item2, 2);

        MenuItem item3 = new MenuItem("Pizza", 12.5, true);
        OrderItem orderitem3 = new OrderItem(item3, 1);

        List<OrderItem> items = new ArrayList<>();
        items.add(orderitem1);
        items.add(orderitem2);
        items.add(orderitem3);

        Order order = new Order(items, 3);

        assertEquals(75.5, order.getTotal(), 0.001);
    }

    @Test
    void rejectsOrderWithNullItems() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Order(null, 0)
        );
    }

    @Test
    void rejectsOrderWithEmptyItems() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Order(new ArrayList<>(), 0)
        );
    }

    @Test
    void rejectsOrderWithNullItem() {
        List<OrderItem> items = new ArrayList<>();
        items.add(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Order(items, 0)
        );
    }

    // Order confirmation and preparation

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

        assertThrows(
                IllegalStateException.class,
                order::confirm
        );
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

        assertThrows(
                IllegalStateException.class,
                order::startPreparing
        );
    }

    // Order cancellation

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

        assertThrows(
                IllegalStateException.class,
                order::cancel
        );

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    // Driver assignment

    @Test
    void rejectsNullDriver() {
        Order order = createOrder();

        assertThrows(
                IllegalArgumentException.class,
                () -> order.assignDriver(null)
        );
    }

    @Test
    void assignsAvailableDriver() {
        Order order = createOrder();

        Driver driver = new Driver(
                "D1", "Ahmad", 32.2211, 35.2544, 15
        );

        order.assignDriver(driver);

        assertEquals(driver, order.getDriver());
    }

    @Test
    void rejectsAssigningBusyDriver() {
        Order order = createOrder();

        Driver driver = new Driver(
                "D1", "Ahmad", 32.2211, 35.2544, 15
        );

        driver.setStatus(DriverStatus.BUSY);

        assertThrows(
                IllegalStateException.class,
                () -> order.assignDriver(driver)
        );
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

        assertThrows(
                IllegalStateException.class,
                () -> order.assignDriver(secondDriver)
        );
    }

    // Observer notification tests

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

        assertThrows(
                IllegalStateException.class,
                order::startPreparing
        );

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

    // Order lifecycle (US4.1)

    @Test
    void changesPreparingOrderToReady() {
        Order order = createOrder();

        order.confirm();
        order.startPreparing();
        order.markReady();

        assertEquals(OrderStatus.READY, order.getStatus());
    }

    @Test
    void changesReadyOrderToOutForDelivery() {
        Order order = createOrder();

        order.confirm();
        order.startPreparing();
        order.markReady();
        order.markOutForDelivery();

        assertEquals(
                OrderStatus.OUT_FOR_DELIVERY,
                order.getStatus()
        );
    }

    @Test
    void changesOutForDeliveryOrderToDelivered() {
        Order order = createOrder();

        order.confirm();
        order.startPreparing();
        order.markReady();
        order.markOutForDelivery();
        order.markDelivered();

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void rejectsMarkingOrderReadyBeforePreparing() {
        Order order = createOrder();

        assertThrows(
                IllegalStateException.class,
                order::markReady
        );
    }

    @Test
    void rejectsMarkingOrderDeliveredBeforeOutForDelivery() {
        Order order = createOrder();

        order.confirm();
        order.startPreparing();
        order.markReady();

        assertThrows(
                IllegalStateException.class,
                order::markDelivered
        );
    }

    @Test
    void rejectsChangingStatusAfterDelivered() {
        Order order = createOrder();

        order.confirm();
        order.startPreparing();
        order.markReady();
        order.markOutForDelivery();
        order.markDelivered();

        assertThrows(IllegalStateException.class, order::confirm);
        assertThrows(IllegalStateException.class, order::cancel);
        assertThrows(IllegalStateException.class, order::markOutForDelivery);
        assertThrows(IllegalStateException.class, order::markReady);

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    // Delivery cost tests (US4.2)

    @Test
    void allowsZeroDeliveryCost() {
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 2);

        Order order = new Order(List.of(item), 0);

        assertEquals(30, order.getTotal(), 0.001);
    }

    @Test
    void rejectsNegativeDeliveryCost() {
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 2);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Order(List.of(item), -5)
        );
    }

    @Test
    void returnsCorrectDeliveryCost() {
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);

        Order order = new Order(List.of(item), 10);

        assertEquals(10.0, order.getDeliveryCost(), 0.001);
    }
}
