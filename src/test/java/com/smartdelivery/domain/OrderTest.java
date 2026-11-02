
package com.smartdelivery.domain;

import com.smartdelivery.observer.OrderObserver;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderTest {

    private Order createOrder() {
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 2);
        return new Order(List.of(item));
    }

    @Test
    void createsOrderWithValidItems() {
        Order order = createOrder();

        assertNotNull(order);
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void rejectsOrderWithNullItems() {
        assertThrows(IllegalArgumentException.class,
                () -> new Order(null));
    }

    @Test
    void rejectsOrderWithEmptyItems() {
        assertThrows(IllegalArgumentException.class,
                () -> new Order(new ArrayList<>()));
    }

    @Test
    void rejectsOrderWithNullItem() {
        List<OrderItem> items = new ArrayList<>();
        items.add(null);

        assertThrows(IllegalArgumentException.class,
                () -> new Order(items));
    }

    @Test
    void calculatesOrderTotal() {
        Order order = createOrder();

        assertEquals(30.0, order.getTotal());
    }

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

    // US3.3: Observer notification tests

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
}
