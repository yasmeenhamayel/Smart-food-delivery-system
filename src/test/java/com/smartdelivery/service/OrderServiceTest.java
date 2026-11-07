
package com.smartdelivery.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Set;

import com.smartdelivery.domain.*;
import com.smartdelivery.observer.OrderObserver;
import com.smartdelivery.strategy.*;

public class OrderServiceTest {

    private OrderService createService() {
        OrderObserver observer = mock(OrderObserver.class);
        DeliveryCostCalculator calculator = new DeliveryCostCalculator(2);

        return new OrderService(observer, calculator);
    }

    // =========================
    // US4.1 and US4.2: Order Creation
    // =========================

    @Test
    void returnCorrectOrder() {

        Restaurant restaurant = new Restaurant(
                "Pizza House", true, 20,
                Set.of("Nablus", "Tubas", "Tamoon")
        );

        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);

        Address address = new Address(
                "Nablus", "Taluza", "main street",
                "in front of yellow house"
        );

        OrderService service = createService();

        Order result = service.placeOrder(
                restaurant, List.of(item), address, 5
        );

        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(45.0, result.getItemTotal(), 0.001);
        assertEquals(10.0, result.getDeliveryCost(), 0.001);
        assertEquals(55.0, result.getTotal(), 0.001);
    }

    @Test
    void calculatesFreeDeliveryCorrectly() {

        Restaurant restaurant = new Restaurant(
                "Pizza House", true, 20,
                Set.of("Nablus", "Tubas", "Tamoon")
        );

        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 2);

        Address address = new Address(
                "Nablus", "Taluza", "main street",
                "in front of yellow house"
        );

        OrderService service = new OrderService(
                mock(OrderObserver.class),
                new DeliveryCostCalculator(0)
        );

        Order result = service.placeOrder(
                restaurant, List.of(item), address, 5
        );

        assertEquals(0.0, result.getDeliveryCost(), 0.001);
        assertEquals(30.0, result.getTotal(), 0.001);
    }

    @Test
    void rejectsUncoveredDeliveryArea() {

        Restaurant restaurant = new Restaurant(
                "Pizza House", true, 20,
                Set.of("Tubas", "Tamoon")
        );

        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);

        Address address = new Address(
                "Nablus", "Taluza", "main street",
                "in front of yellow house"
        );

        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(
                        restaurant, List.of(item), address, 5
                ));
    }

    @Test
    void rejectsOrderBelowMinimumValue() {

        Restaurant restaurant = new Restaurant(
                "Pizza House", true, 20,
                Set.of("Nablus", "Tubas", "Tamoon")
        );

        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 1);

        Address address = new Address(
                "Nablus", "Taluza", "main street",
                "in front of yellow house"
        );

        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(
                        restaurant, List.of(item), address, 5
                ));
    }

    @Test
    void deliveryCostIsNotIncludedInMinimumOrderValue() {

        Restaurant restaurant = new Restaurant(
                "Pizza House", true, 40,
                Set.of("Nablus", "Tubas", "Tamoon")
        );

        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 2);

        Address address = new Address(
                "Nablus", "Taluza", "main street",
                "in front of yellow house"
        );

        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(
                        restaurant, List.of(item), address, 10
                ));
    }

    @Test
    void rejectsNullRestaurant() {

        Address address = new Address(
                "Nablus", "Taluza", "main street",
                "in front of yellow house"
        );

        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(
                        null, List.of(), address, 5
                ));
    }

    @Test
    void rejectsNullAddress() {

        Restaurant restaurant = new Restaurant(
                "Pizza House", true, 20,
                Set.of("Nablus", "Tubas", "Tamoon")
        );

        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);

        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(
                        restaurant, List.of(item), null, 5
                ));
    }

    @Test
    void rejectsInvalidDeliveryDistance() {

        Restaurant restaurant = new Restaurant(
                "Pizza House", true, 20,
                Set.of("Nablus", "Tubas", "Tamoon")
        );

        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);

        Address address = new Address(
                "Nablus", "Taluza", "main street",
                "in front of yellow house"
        );

        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(
                        restaurant, List.of(item), address, 0
                ));
    }

    @Test
    void rejectsNegativeDeliveryDistance() {

        Restaurant restaurant = new Restaurant(
                "Pizza House", true, 20,
                Set.of("Nablus", "Tubas", "Tamoon")
        );

        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);

        Address address = new Address(
                "Nablus", "Taluza", "main street",
                "in front of yellow house"
        );

        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(
                        restaurant, List.of(item), address, -5
                ));
    }

    @Test
    void rejectsNullOrderObserver() {

        assertThrows(IllegalArgumentException.class,
                () -> new OrderService(
                        null, new DeliveryCostCalculator(2)
                ));
    }

    @Test
    void rejectsNullDeliveryCostCalculator() {

        assertThrows(IllegalArgumentException.class,
                () -> new OrderService(
                        mock(OrderObserver.class), null
                ));
    }

    // =========================
    // Observer Tests
    // =========================

    @Test
    void notifiesObserverWhenCreatedOrderIsCancelled() {

        Restaurant restaurant = new Restaurant(
                "Pizza House", true, 20,
                Set.of("Nablus", "Tubas", "Tamoon")
        );

        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);

        Address address = new Address(
                "Nablus", "Taluza", "main street",
                "in front of yellow house"
        );

        OrderObserver observer = mock(OrderObserver.class);

        OrderService service = new OrderService(
                observer, new DeliveryCostCalculator(2)
        );

        Order order = service.placeOrder(
                restaurant, List.of(item), address, 5
        );

        service.cancelOrder(order);

        verify(observer).onOrderStatusChanged(
                order, OrderStatus.CANCELLED
        );
    }

    // =========================
    // Order Cancellation Tests
    // =========================

    @Test
    void cancelsPendingOrder() {

        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);

        Order order = new Order(List.of(item), 0);

        OrderService service = createService();

        service.cancelOrder(order);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void rejectsCancellingOrderInPreparingStatus() {

        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);

        Order order = new Order(List.of(item), 0);

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

        Order order = new Order(List.of(item), 0);

        OrderService service = createService();

        service.cancelOrder(order);

        assertThrows(IllegalStateException.class,
                () -> service.cancelOrder(order));
    }

    // =========================
    // US3.1 - Driver Assignment
    // =========================

    @Test
    void assignsAvailableDriverToOrder() {

        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);

        Order order = new Order(List.of(item), 0);

        Driver driver = new Driver(
                "D1", "Ahmad", 32.22, 35.25, 20
        );

        OrderService service = createService();

        service.assignDriver(order, driver);

        assertSame(driver, order.getDriver());
    }

    @Test
    void changesDriverStatusToBusy() {

        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);

        Order order = new Order(List.of(item), 0);

        Driver driver = new Driver(
                "D1", "Ahmad", 32.22, 35.25, 20
        );

        OrderService service = createService();

        service.assignDriver(order, driver);

        assertEquals(DriverStatus.BUSY, driver.getStatus());
    }

    @Test
    void rejectsAssigningNullOrder() {

        Driver driver = new Driver(
                "D1", "Ahmad", 32.22, 35.25, 20
        );

        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.assignDriver(null, driver));

        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }

    @Test
    void rejectsAssigningNullDriver() {

        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);

        Order order = new Order(List.of(item), 0);

        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.assignDriver(order, null));

        assertNull(order.getDriver());
    }

    @Test
    void rejectsAssigningBusyDriver() {

        MenuItem menu = new MenuItem("Pizza", 15, true);
        OrderItem item = new OrderItem(menu, 3);

        Order order = new Order(List.of(item), 0);

        Driver driver = new Driver(
                "D1", "Ahmad", 32.22, 35.25, 20
        );

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

        Order order = new Order(List.of(item), 0);

        Driver firstDriver = new Driver(
                "D1", "Ahmad", 32.22, 35.25, 20
        );

        Driver secondDriver = new Driver(
                "D2", "Omar", 32.23, 35.26, 25
        );

        OrderService service = createService();

        service.assignDriver(order, firstDriver);

        assertThrows(IllegalStateException.class,
                () -> service.assignDriver(order, secondDriver));

        assertSame(firstDriver, order.getDriver());
        assertEquals(DriverStatus.AVAILABLE, secondDriver.getStatus());
    }

    // =========================
    // US3.2 - Strategy-Based Driver Selection
    // =========================

    @Test
    void assignsNearestAvailableDriver() {

        Order order = new Order(List.of(
                new OrderItem(new MenuItem("Pizza", 15, true), 2)
        ), 0);

        Driver nearestDriver = new Driver(
                "D1", "Ahmad", 32.22, 35.25, 20
        );

        Driver farthestDriver = new Driver(
                "D2", "Omar", 32.30, 35.35, 15
        );

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
        ), 0);

        Driver slowerDriver = new Driver(
                "D1", "Ahmad", 32.22, 35.25, 30
        );

        Driver fasterDriver = new Driver(
                "D2", "Omar", 32.30, 35.35, 10
        );

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
        ), 0);

        Driver busyDriver = new Driver(
                "D1", "Ahmad", 32.22, 35.25, 5
        );

        Driver availableDriver = new Driver(
                "D2", "Omar", 32.30, 35.35, 20
        );

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
        ), 0);

        Driver driver = new Driver(
                "D1", "Ahmad", 32.22, 35.25, 20
        );

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
        ), 0);

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
        ), 0);

        Driver driver = new Driver(
                "D1", "Ahmad", 32.22, 35.25, 20
        );

        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.assignDriverUsingStrategy(
                        order, List.of(driver), 32.22, 35.25, null
                ));

        assertNull(order.getDriver());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }


        // =========================
    // Sprint 5 - Polymorphic Driver Selection
    // =========================

    @Test
    void skipsBicycleDriverWhenDeliveryDistanceExceedsItsLimit() {

        Order order = new Order(List.of(
                new OrderItem(new MenuItem("Pizza", 15, true), 2)
        ), 0);

        BicycleDriver bicycleDriver = new BicycleDriver(
                "D1", "Ahmad", 32.22, 35.25, 20, 3
        );

        MotorcycleDriver motorcycleDriver = new MotorcycleDriver(
                "D2", "Omar", 32.30, 35.35, 15, 10
        );

        OrderService service = createService();

        Driver result = service.assignDriverUsingStrategy(
                order,
                List.of(bicycleDriver, motorcycleDriver),
                32.22,
                35.25,
                5,
                new NearestDriverStrategy()
        );

        assertSame(motorcycleDriver, result);
        assertSame(motorcycleDriver, order.getDriver());

        assertEquals(DriverStatus.AVAILABLE, bicycleDriver.getStatus());
        assertEquals(DriverStatus.BUSY, motorcycleDriver.getStatus());
    }

    @Test
    void rejectsAssignmentWhenNoDriverCanHandleDelivery() {

        Order order = new Order(List.of(
                new OrderItem(new MenuItem("Pizza", 15, true), 2)
        ), 0);

        BicycleDriver bicycleDriver = new BicycleDriver(
                "D1", "Ahmad", 32.22, 35.25, 20, 3
        );

        OrderService service = createService();

        assertThrows(IllegalStateException.class,
                () -> service.assignDriverUsingStrategy(
                        order,
                        List.of(bicycleDriver),
                        32.22,
                        35.25,
                        5,
                        new NearestDriverStrategy()
                ));

        assertNull(order.getDriver());
        assertEquals(DriverStatus.AVAILABLE, bicycleDriver.getStatus());
    }

    @Test
    void rejectsCarDriverWhenOrderWeightExceedsItsLimit() {

        MenuItem menu = new MenuItem("Pizza", 15, true, 0.8);
        OrderItem item = new OrderItem(menu, 3);

        Order order = new Order(List.of(item), 0);

        CarDriver carDriver = new CarDriver(
                "D1", "Ahmad", 32.22, 35.25, 20, 2
        );

        OrderService service = createService();

        assertThrows(IllegalStateException.class,
                () -> service.assignDriverUsingStrategy(
                        order,
                        List.of(carDriver),
                        32.22,
                        35.25,
                        5,
                        new NearestDriverStrategy()
                ));

        assertEquals(2.4, order.getTotalWeight(), 0.001);
        assertNull(order.getDriver());
        assertEquals(DriverStatus.AVAILABLE, carDriver.getStatus());
    }

    @Test
    void assignsCarDriverWhenOrderWeightIsWithinItsLimit() {

        MenuItem menu = new MenuItem("Pizza", 15, true, 0.8);
        OrderItem item = new OrderItem(menu, 3);

        Order order = new Order(List.of(item), 0);

        CarDriver carDriver = new CarDriver(
                "D1", "Ahmad", 32.22, 35.25, 20, 3
        );

        OrderService service = createService();

        Driver result = service.assignDriverUsingStrategy(
                order,
                List.of(carDriver),
                32.22,
                35.25,
                5,
                new NearestDriverStrategy()
        );

        assertSame(carDriver, result);
        assertSame(carDriver, order.getDriver());
        assertEquals(DriverStatus.BUSY, carDriver.getStatus());
    }

    @Test
    void rejectsInvalidDistanceInPolymorphicDriverSelection() {

        Order order = new Order(List.of(
                new OrderItem(new MenuItem("Pizza", 15, true), 2)
        ), 0);

        Driver driver = new Driver(
                "D1", "Ahmad", 32.22, 35.25, 20
        );

        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.assignDriverUsingStrategy(
                        order,
                        List.of(driver),
                        32.22,
                        35.25,
                        0,
                        new NearestDriverStrategy()
                ));

        assertNull(order.getDriver());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }
}
