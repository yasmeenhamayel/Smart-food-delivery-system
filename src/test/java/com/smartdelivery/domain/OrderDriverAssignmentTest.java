package com.smartdelivery.domain;

import com.smartdelivery.observer.OrderObserver;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Sprint 5 (US5.4): checks that the order itself enforces driver-type rules,
 * so no assignment overload can bypass them.
 */
class OrderDriverAssignmentTest {

    private Order lightOrder() {
        return new Order(List.of(
                new OrderItem(new MenuItem("Pizza", 15, true), 2)
        ), 5.0);
    }

    private Order weightedOrder(double itemWeight, int quantity) {
        MenuItem menu = new MenuItem("Pizza", 15, true, itemWeight);

        return new Order(List.of(new OrderItem(menu, quantity)), 5.0);
    }

    private void assertNotAssigned(Order order, Driver driver) {
        assertNull(order.getDriver());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }

    // =========================
    // assignDriver(driver): no distance available
    // =========================

    @Test
    void assignsPlainDriverWithoutDistance() {
        Order order = lightOrder();
        Driver driver = new Driver("D1", "Ahmad", 32.2, 35.2, 10);

        order.assignDriver(driver);

        assertSame(driver, order.getDriver());
        assertEquals(DriverStatus.BUSY, driver.getStatus());
    }

    @Test
    void rejectsBicycleDriverWithoutDistance() {
        Order order = lightOrder();
        BicycleDriver driver =
                new BicycleDriver("B1", "A", 32.2, 35.2, 20, 5);

        assertThrows(IllegalStateException.class,
                () -> order.assignDriver(driver));

        assertNotAssigned(order, driver);
    }

    @Test
    void rejectsMotorcycleDriverWithoutDistance() {
        Order order = lightOrder();
        MotorcycleDriver driver =
                new MotorcycleDriver("M1", "B", 32.2, 35.2, 15, 20);

        assertThrows(IllegalStateException.class,
                () -> order.assignDriver(driver));

        assertNotAssigned(order, driver);
    }

    @Test
    void rejectsElectricDriverWithoutDistance() {
        Order order = lightOrder();
        ElectricVehicleDriver driver =
                new ElectricVehicleDriver("E1", "D", 32.2, 35.2, 10, 100);

        assertThrows(IllegalStateException.class,
                () -> order.assignDriver(driver));

        assertNotAssigned(order, driver);
    }

    @Test
    void assignsCarDriverWithoutDistanceWhenOrderIsWithinCapacity() {
        Order order = weightedOrder(0.5, 2);
        CarDriver driver = new CarDriver("C1", "C", 32.2, 35.2, 10, 5);

        order.assignDriver(driver);

        assertSame(driver, order.getDriver());
        assertEquals(DriverStatus.BUSY, driver.getStatus());
    }

    @Test
    void rejectsCarDriverWithoutDistanceWhenOrderIsTooHeavy() {
        Order order = weightedOrder(2, 3);
        CarDriver driver = new CarDriver("C1", "C", 32.2, 35.2, 10, 5);

        assertThrows(IllegalStateException.class,
                () -> order.assignDriver(driver));

        assertNotAssigned(order, driver);
    }

    @Test
    void rejectedAssignmentDoesNotNotifyAndOrderCanStillBeAssigned() {
        OrderObserver observer = mock(OrderObserver.class);
        Order order = lightOrder();
        order.addObserver(observer);

        BicycleDriver bicycle =
                new BicycleDriver("B1", "A", 32.2, 35.2, 20, 5);
        Driver plain = new Driver("D1", "Ahmad", 32.2, 35.2, 10);

        assertThrows(IllegalStateException.class,
                () -> order.assignDriver(bicycle));
        verify(observer, never()).onDriverAssigned(any(), any());

        order.assignDriver(plain);

        assertSame(plain, order.getDriver());
        verify(observer).onDriverAssigned(order, plain);
    }

    // =========================
    // assignDriver(driver, distance)
    // =========================

    @Test
    void assignsBicycleDriverWithinDistance() {
        Order order = lightOrder();
        BicycleDriver driver =
                new BicycleDriver("B1", "A", 32.2, 35.2, 20, 5);

        order.assignDriver(driver, 5);

        assertSame(driver, order.getDriver());
        assertEquals(DriverStatus.BUSY, driver.getStatus());
    }

    @Test
    void rejectsBicycleDriverBeyondDistance() {
        Order order = lightOrder();
        BicycleDriver driver =
                new BicycleDriver("B1", "A", 32.2, 35.2, 20, 5);

        assertThrows(IllegalStateException.class,
                () -> order.assignDriver(driver, 5.01));

        assertNotAssigned(order, driver);
    }

    @Test
    void assignsAndRejectsMotorcycleDriverAroundMaximumDistance() {
        MotorcycleDriver ok =
                new MotorcycleDriver("M1", "B", 32.2, 35.2, 15, 20);
        MotorcycleDriver tooFar =
                new MotorcycleDriver("M2", "B", 32.2, 35.2, 15, 20);
        Order first = lightOrder();
        Order second = lightOrder();

        first.assignDriver(ok, 20);

        assertSame(ok, first.getDriver());
        assertThrows(IllegalStateException.class,
                () -> second.assignDriver(tooFar, 20.01));
        assertNotAssigned(second, tooFar);
    }

    @Test
    void assignsAndRejectsElectricDriverAroundRemainingRange() {
        ElectricVehicleDriver ok =
                new ElectricVehicleDriver("E1", "D", 32.2, 35.2, 10, 100);
        ElectricVehicleDriver tooFar =
                new ElectricVehicleDriver("E2", "D", 32.2, 35.2, 10, 100);
        Order first = lightOrder();
        Order second = lightOrder();

        first.assignDriver(ok, 100);

        assertSame(ok, first.getDriver());
        assertThrows(IllegalStateException.class,
                () -> second.assignDriver(tooFar, 100.01));
        assertNotAssigned(second, tooFar);
    }

    @Test
    void carDriverWithDistanceIsCheckedByWeightOnly() {
        Order heavy = weightedOrder(2, 3);
        Order light = weightedOrder(0.5, 2);
        CarDriver tooSmall = new CarDriver("C1", "C", 32.2, 35.2, 10, 5);
        CarDriver fits = new CarDriver("C2", "C", 32.2, 35.2, 10, 5);

        assertThrows(IllegalStateException.class,
                () -> heavy.assignDriver(tooSmall, 3));
        assertNotAssigned(heavy, tooSmall);

        light.assignDriver(fits, 500);

        assertSame(fits, light.getDriver());
    }

    @Test
    void rejectsInvalidArgumentsWhenAssigningWithDistance() {
        Order order = lightOrder();
        Driver driver = new Driver("D1", "Ahmad", 32.2, 35.2, 10);

        assertThrows(IllegalArgumentException.class,
                () -> order.assignDriver(null, 5));
        assertThrows(IllegalArgumentException.class,
                () -> order.assignDriver(driver, 0));
        assertThrows(IllegalArgumentException.class,
                () -> order.assignDriver(driver, -1));
        assertThrows(IllegalArgumentException.class,
                () -> order.assignDriver(driver, Double.NaN));
        assertThrows(IllegalArgumentException.class,
                () -> order.assignDriver(driver, Double.POSITIVE_INFINITY));

        assertNotAssigned(order, driver);
    }

    @Test
    void assignmentWithDistanceKeepsExistingStateRules() {
        Order order = lightOrder();
        Driver first = new Driver("D1", "Ahmad", 32.2, 35.2, 10);
        Driver second = new Driver("D2", "Omar", 32.2, 35.2, 10);
        Driver busy = new Driver("D3", "Sami", 32.2, 35.2, 10);
        busy.setStatus(DriverStatus.BUSY);

        assertThrows(IllegalStateException.class,
                () -> order.assignDriver(busy, 5));

        order.assignDriver(first, 5);

        assertThrows(IllegalStateException.class,
                () -> order.assignDriver(second, 5));
        assertSame(first, order.getDriver());
        assertEquals(DriverStatus.AVAILABLE, second.getStatus());
    }

    @Test
    void deliveredOrderRejectsAssignmentWithDistance() {
        Order order = lightOrder();
        order.confirm();
        order.startPreparing();
        order.markReady();
        order.markOutForDelivery();
        order.markDelivered();

        Driver driver = new Driver("D1", "Ahmad", 32.2, 35.2, 10);

        assertThrows(IllegalStateException.class,
                () -> order.assignDriver(driver, 5));
        assertNotAssigned(order, driver);
    }

    @Test
    void notifiesObserverWhenAssignedWithDistance() {
        OrderObserver observer = mock(OrderObserver.class);
        Order order = lightOrder();
        order.addObserver(observer);
        BicycleDriver driver =
                new BicycleDriver("B1", "A", 32.2, 35.2, 20, 5);

        order.assignDriver(driver, 3);

        verify(observer).onDriverAssigned(order, driver);
    }
}
