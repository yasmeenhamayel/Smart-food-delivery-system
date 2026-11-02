
package com.smartdelivery.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import java.util.List;
import java.util.Set;

import com.smartdelivery.domain.*;
import com.smartdelivery.observer.OrderObserver;
import com.smartdelivery.strategy.*;

public class OrderServiceTest {

    private OrderService createService() {
        OrderObserver observer = mock(OrderObserver.class);
        return new OrderService(observer);
    }

    @Test
    void returnCorrectOrder() {
        Restaurant restaurant = new Restaurant("Pizza House", true, 20,
                Set.of("Nablus", "Tubas", "Tamoon"));
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);
        Address address = new Address("Nablus", "Taluza", "main street",
                "in front of yellow house");
        OrderService service = createService();

        Order result = service.placeOrder(restaurant, List.of(item), address);

        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(45.0, result.getTotal(), 0.001);
    }

	@Test
void notifiesObserverWhenCreatedOrderIsCancelled() {
    Restaurant restaurant = new Restaurant("Pizza House", true, 20,
            Set.of("Nablus", "Tubas", "Tamoon"));

    MenuItem menu = new MenuItem("Pizza", 15, true);
    OrderItem item = new OrderItem(menu, 3);

    Address address = new Address("Nablus", "Taluza", "main street",
            "in front of yellow house");

    OrderObserver observer = mock(OrderObserver.class);
    OrderService service = new OrderService(observer);

    Order order = service.placeOrder(restaurant, List.of(item), address);

    service.cancelOrder(order);

    verify(observer).onOrderStatusChanged(order, OrderStatus.CANCELLED);
}



    @Test
    void rejectsUncoveredDilveryArea() {
        Restaurant restaurant = new Restaurant("Pizza House", true, 20,
                Set.of("Tubas", "Tamoon"));
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);
        Address address = new Address("Nablus", "Taluza", "main street",
                "in front of yellow house");
        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(restaurant, List.of(item), address));
    }

    @Test
    void rejectsOrderBelowMinimumValue() {
        Restaurant restaurant = new Restaurant("Pizza House", true, 20,
                Set.of("Nablus", "Tubas", "Tamoon"));
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 1);
        Address address = new Address("Nablus", "Taluza", "main street",
                "in front of yellow house");
        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(restaurant, List.of(item), address));
    }

    @Test
    void cancelsPendingOrder() {
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);
        Order order = new Order(List.of(item));
        OrderService service = createService();

        service.cancelOrder(order);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void rejectsCancellingOrderInPreparingStatus() {
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);
        Order order = new Order(List.of(item));
        OrderService service = createService();

        order.confirm();
        order.startPreparing();

        assertThrows(IllegalStateException.class,
                () -> service.cancelOrder(order));

        assertEquals(OrderStatus.PREPARING, order.getStatus());
    }

    @Test
    void rejectsCancellingNullOrder() {
        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.cancelOrder(null));
    }

    @Test
    void rejectsCancellingAlreadyCancelledOrder() {
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);
        Order order = new Order(List.of(item));
        OrderService service = createService();

        service.cancelOrder(order);

        assertThrows(IllegalStateException.class,
                () -> service.cancelOrder(order));
    }

    // US3.1 - Driver Assignment

    @Test
    void assignsAvailableDriverToOrder() {
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);
        Order order = new Order(List.of(item));
        Driver driver = new Driver("D1", "Ahmad", 32.22, 35.25, 20);
        OrderService service = createService();

        service.assignDriver(order, driver);

        assertSame(driver, order.getDriver());
    }

    @Test
    void changesDriverStatusToBusy() {
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);
        Order order = new Order(List.of(item));
        Driver driver = new Driver("D1", "Ahmad", 32.22, 35.25, 20);
        OrderService service = createService();

        service.assignDriver(order, driver);

        assertEquals(DriverStatus.BUSY, driver.getStatus());
    }

    @Test
    void rejectsAssigningNullOrder() {
        Driver driver = new Driver("D1", "Ahmad", 32.22, 35.25, 20);
        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.assignDriver(null, driver));

        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }

    @Test
    void rejectsAssigningNullDriver() {
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);
        Order order = new Order(List.of(item));
        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.assignDriver(order, null));

        assertNull(order.getDriver());
    }

    @Test
    void rejectsAssigningBusyDriver() {
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);
        Order order = new Order(List.of(item));
        Driver driver = new Driver("D1", "Ahmad", 32.22, 35.25, 20);
        OrderService service = createService();

        driver.setStatus(DriverStatus.BUSY);

        assertThrows(IllegalStateException.class,
                () -> service.assignDriver(order, driver));

        assertNull(order.getDriver());
        assertEquals(DriverStatus.BUSY, driver.getStatus());
    }

    @Test
    void rejectsAssigningSecondDriverToOrder() {
        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);
        Order order = new Order(List.of(item));
        Driver firstDriver = new Driver("D1", "Ahmad", 32.22, 35.25, 20);
        Driver secondDriver = new Driver("D2", "Omar", 32.23, 35.26, 25);
        OrderService service = createService();

        service.assignDriver(order, firstDriver);

        assertThrows(IllegalStateException.class,
                () -> service.assignDriver(order, secondDriver));

        assertSame(firstDriver, order.getDriver());
        assertEquals(DriverStatus.AVAILABLE, secondDriver.getStatus());
    }

    // US3.2 - Strategy-Based Driver Selection

    @Test
    void assignsNearestAvailableDriver() {
        Order order = new Order(List.of(
                new OrderItem(new MenuItem("Pizza", 15, true), 2)
        ));

        Driver nearestDriver = new Driver("D1", "Ahmad",
                32.22, 35.25, 20);

        Driver farthestDriver = new Driver("D2", "Omar",
                32.30, 35.35, 15);

        List<Driver> drivers = List.of(farthestDriver, nearestDriver);
        OrderService service = createService();

        Driver result = service.assignDriverUsingStrategy(
                order, drivers, 32.22, 35.25,
                new NearestDriverStrategy()
        );

        assertSame(nearestDriver, result);
        assertSame(nearestDriver, order.getDriver());
        assertEquals(DriverStatus.BUSY, nearestDriver.getStatus());
        assertEquals(DriverStatus.AVAILABLE, farthestDriver.getStatus());
    }

    @Test
    void assignsFastestAvailableDriver() {
        Order order = new Order(List.of(
                new OrderItem(new MenuItem("Pizza", 15, true), 2)
        ));

        Driver slowerDriver = new Driver("D1", "Ahmad",
                32.22, 35.25, 30);

        Driver fasterDriver = new Driver("D2", "Omar",
                32.30, 35.35, 10);

        List<Driver> drivers = List.of(slowerDriver, fasterDriver);
        OrderService service = createService();

        Driver result = service.assignDriverUsingStrategy(
                order, drivers, 32.22, 35.25,
                new FastestDriverStrategy()
        );

        assertSame(fasterDriver, result);
        assertSame(fasterDriver, order.getDriver());
        assertEquals(DriverStatus.BUSY, fasterDriver.getStatus());
        assertEquals(DriverStatus.AVAILABLE, slowerDriver.getStatus());
    }

    @Test
    void ignoresBusyDriversWhenSelectingDriver() {
        Order order = new Order(List.of(
                new OrderItem(new MenuItem("Pizza", 15, true), 2)
        ));

        Driver busyDriver = new Driver("D1", "Ahmad",
                32.22, 35.25, 5);

        Driver availableDriver = new Driver("D2", "Omar",
                32.30, 35.35, 20);

        busyDriver.setStatus(DriverStatus.BUSY);

        List<Driver> drivers = List.of(busyDriver, availableDriver);
        OrderService service = createService();

        Driver result = service.assignDriverUsingStrategy(
                order, drivers, 32.22, 35.25,
                new FastestDriverStrategy()
        );

        assertSame(availableDriver, result);
        assertSame(availableDriver, order.getDriver());
        assertEquals(DriverStatus.BUSY, busyDriver.getStatus());
        assertEquals(DriverStatus.BUSY, availableDriver.getStatus());
    }

    @Test
    void rejectsAssignmentWhenNoDriverIsAvailable() {
        Order order = new Order(List.of(
                new OrderItem(new MenuItem("Pizza", 15, true), 2)
        ));

        Driver driver = new Driver("D1", "Ahmad",
                32.22, 35.25, 20);

        driver.setStatus(DriverStatus.BUSY);

        OrderService service = createService();

        assertThrows(IllegalStateException.class,
                () -> service.assignDriverUsingStrategy(
                        order,
                        List.of(driver),
                        32.22,
                        35.25,
                        new NearestDriverStrategy()
                ));

        assertNull(order.getDriver());
        assertEquals(DriverStatus.BUSY, driver.getStatus());
    }

    @Test
    void rejectsNullDriversList() {
        Order order = new Order(List.of(
                new OrderItem(new MenuItem("Pizza", 15, true), 2)
        ));

        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.assignDriverUsingStrategy(
                        order, null, 32.22, 35.25,
                        new NearestDriverStrategy()
                ));

        assertNull(order.getDriver());
    }

    @Test
    void rejectsNullDriverSelectionStrategy() {
        Order order = new Order(List.of(
                new OrderItem(new MenuItem("Pizza", 15, true), 2)
        ));

        Driver driver = new Driver("D1", "Ahmad",
                32.22, 35.25, 20);

        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.assignDriverUsingStrategy(
                        order, List.of(driver), 32.22, 35.25, null
                ));

        assertNull(order.getDriver());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }
}

