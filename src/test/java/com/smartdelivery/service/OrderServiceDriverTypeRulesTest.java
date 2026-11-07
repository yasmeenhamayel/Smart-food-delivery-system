package com.smartdelivery.service;

import com.smartdelivery.domain.*;
import com.smartdelivery.observer.OrderObserver;
import com.smartdelivery.strategy.FastestDriverStrategy;
import com.smartdelivery.strategy.NearestDriverStrategy;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Sprint 5 (US5.3 and US5.4): checks that driver-type rules are enforced
 * when drivers are assigned through the service.
 */
public class OrderServiceDriverTypeRulesTest {

    private OrderService createService() {
        return new OrderService(
                mock(OrderObserver.class),
                new DeliveryCostCalculator(2)
        );
    }

    private Order orderWithWeight(double itemWeight, int quantity) {
        MenuItem menu = new MenuItem("Pizza", 15, true, itemWeight);

        return new Order(List.of(new OrderItem(menu, quantity)), 0);
    }

    private Order lightOrder() {
        return new Order(List.of(
                new OrderItem(new MenuItem("Pizza", 15, true), 2)
        ), 0);
    }

    // =========================
    // Direct assignment with distance
    // =========================

    @Test
    void directAssignmentAcceptsBicycleDriverWithinDistance() {
        Order order = lightOrder();
        BicycleDriver driver = new BicycleDriver(
                "B1", "Ahmad", 32.22, 35.25, 20, 5
        );

        createService().assignDriver(order, driver, 5);

        assertSame(driver, order.getDriver());
        assertEquals(DriverStatus.BUSY, driver.getStatus());
    }

    @Test
    void directAssignmentRejectsBicycleDriverBeyondDistance() {
        Order order = lightOrder();
        BicycleDriver driver = new BicycleDriver(
                "B1", "Ahmad", 32.22, 35.25, 20, 5
        );

        assertThrows(IllegalStateException.class, () ->
                createService().assignDriver(order, driver, 5.1)
        );

        assertNull(order.getDriver());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }

    @Test
    void directAssignmentRejectsMotorcycleDriverBeyondDistance() {
        Order order = lightOrder();
        MotorcycleDriver driver = new MotorcycleDriver(
                "M1", "Omar", 32.22, 35.25, 15, 20
        );

        assertThrows(IllegalStateException.class, () ->
                createService().assignDriver(order, driver, 25)
        );

        assertNull(order.getDriver());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }

    @Test
    void directAssignmentAcceptsMotorcycleDriverAtMaximumDistance() {
        Order order = lightOrder();
        MotorcycleDriver driver = new MotorcycleDriver(
                "M1", "Omar", 32.22, 35.25, 15, 20
        );

        createService().assignDriver(order, driver, 20);

        assertSame(driver, order.getDriver());
        assertEquals(DriverStatus.BUSY, driver.getStatus());
    }

    @Test
    void directAssignmentRejectsCarDriverWhenOrderIsTooHeavy() {
        Order order = orderWithWeight(0.8, 3);
        CarDriver driver = new CarDriver(
                "C1", "Sami", 32.22, 35.25, 10, 2
        );

        assertThrows(IllegalStateException.class, () ->
                createService().assignDriver(order, driver, 5)
        );

        assertNull(order.getDriver());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }

    @Test
    void directAssignmentAcceptsCarDriverForHeavyOrderWithinLimit() {
        Order order = orderWithWeight(0.8, 3);
        CarDriver driver = new CarDriver(
                "C1", "Sami", 32.22, 35.25, 10, 2.5
        );

        createService().assignDriver(order, driver, 50);

        assertSame(driver, order.getDriver());
        assertEquals(DriverStatus.BUSY, driver.getStatus());
    }

    @Test
    void directAssignmentRejectsElectricDriverWithInsufficientRange() {
        Order order = lightOrder();
        ElectricVehicleDriver driver = new ElectricVehicleDriver(
                "E1", "Lina", 32.22, 35.25, 10, 30
        );

        assertThrows(IllegalStateException.class, () ->
                createService().assignDriver(order, driver, 31)
        );

        assertNull(order.getDriver());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }

    @Test
    void directAssignmentAcceptsElectricDriverAtRangeLimit() {
        Order order = lightOrder();
        ElectricVehicleDriver driver = new ElectricVehicleDriver(
                "E1", "Lina", 32.22, 35.25, 10, 30
        );

        createService().assignDriver(order, driver, 30);

        assertSame(driver, order.getDriver());
        assertEquals(DriverStatus.BUSY, driver.getStatus());
    }

    @Test
    void directAssignmentAcceptsPlainDriverForAnyDistance() {
        Order order = lightOrder();
        Driver driver = new Driver("D1", "Ahmad", 32.22, 35.25, 20);

        createService().assignDriver(order, driver, 500);

        assertSame(driver, order.getDriver());
    }

    @Test
    void directAssignmentRejectsInvalidArguments() {
        OrderService service = createService();
        Order order = lightOrder();
        Driver driver = new Driver("D1", "Ahmad", 32.22, 35.25, 20);

        assertThrows(IllegalArgumentException.class,
                () -> service.assignDriver(null, driver, 5));
        assertThrows(IllegalArgumentException.class,
                () -> service.assignDriver(order, null, 5));
        assertThrows(IllegalArgumentException.class,
                () -> service.assignDriver(order, driver, 0));
        assertThrows(IllegalArgumentException.class,
                () -> service.assignDriver(order, driver, -1));
        assertThrows(IllegalArgumentException.class,
                () -> service.assignDriver(order, driver, Double.NaN));
        assertThrows(IllegalArgumentException.class,
                () -> service.assignDriver(order, driver,
                        Double.POSITIVE_INFINITY));

        assertNull(order.getDriver());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }

    @Test
    void directAssignmentStillRejectsBusyDriver() {
        Order order = lightOrder();
        BicycleDriver driver = new BicycleDriver(
                "B1", "Ahmad", 32.22, 35.25, 20, 5
        );
        driver.setStatus(DriverStatus.BUSY);

        assertThrows(IllegalStateException.class, () ->
                createService().assignDriver(order, driver, 3)
        );

        assertNull(order.getDriver());
    }

    // =========================
    // Strategy-based assignment with each driver type
    // =========================

    @Test
    void skipsMotorcycleDriverBeyondDistanceAndPicksCapableCar() {
        Order order = lightOrder();

        MotorcycleDriver motorcycle = new MotorcycleDriver(
                "M1", "Omar", 32.22, 35.25, 15, 10
        );
        CarDriver car = new CarDriver(
                "C1", "Sami", 32.40, 35.40, 20, 10
        );

        Driver result = createService().assignDriverUsingStrategy(
                order,
                List.of(motorcycle, car),
                32.22, 35.25, 15,
                new NearestDriverStrategy()
        );

        assertSame(car, result);
        assertEquals(DriverStatus.AVAILABLE, motorcycle.getStatus());
        assertEquals(DriverStatus.BUSY, car.getStatus());
    }

    @Test
    void skipsElectricDriverWithInsufficientRangeUsingFastestStrategy() {
        Order order = lightOrder();

        ElectricVehicleDriver electric = new ElectricVehicleDriver(
                "E1", "Lina", 32.22, 35.25, 5, 10
        );
        MotorcycleDriver motorcycle = new MotorcycleDriver(
                "M1", "Omar", 32.22, 35.25, 15, 30
        );

        Driver result = createService().assignDriverUsingStrategy(
                order,
                List.of(electric, motorcycle),
                32.22, 35.25, 20,
                new FastestDriverStrategy()
        );

        assertSame(motorcycle, result);
        assertEquals(DriverStatus.AVAILABLE, electric.getStatus());
    }

    @Test
    void assignsElectricDriverWhenRangeIsSufficient() {
        Order order = lightOrder();

        ElectricVehicleDriver electric = new ElectricVehicleDriver(
                "E1", "Lina", 32.22, 35.25, 5, 10
        );

        Driver result = createService().assignDriverUsingStrategy(
                order,
                List.of(electric),
                32.22, 35.25, 10,
                new NearestDriverStrategy()
        );

        assertSame(electric, result);
        assertSame(electric, order.getDriver());
        assertEquals(DriverStatus.BUSY, electric.getStatus());
    }

    @Test
    void rejectsAssignmentWhenOnlyCapableDriverIsBusy() {
        Order order = lightOrder();

        BicycleDriver capable = new BicycleDriver(
                "B1", "Ahmad", 32.22, 35.25, 20, 10
        );
        capable.setStatus(DriverStatus.BUSY);
        BicycleDriver tooShort = new BicycleDriver(
                "B2", "Khaled", 32.22, 35.25, 20, 2
        );

        assertThrows(IllegalStateException.class, () ->
                createService().assignDriverUsingStrategy(
                        order,
                        List.of(capable, tooShort),
                        32.22, 35.25, 5,
                        new NearestDriverStrategy()
                ));

        assertNull(order.getDriver());
        assertEquals(DriverStatus.AVAILABLE, tooShort.getStatus());
    }

    @Test
    void ignoresNullEntriesInDriverList() {
        Order order = lightOrder();
        CarDriver car = new CarDriver(
                "C1", "Sami", 32.22, 35.25, 10, 10
        );

        List<Driver> drivers = new java.util.ArrayList<>();
        drivers.add(null);
        drivers.add(car);

        Driver result = createService().assignDriverUsingStrategy(
                order, drivers, 32.22, 35.25, 5,
                new NearestDriverStrategy()
        );

        assertSame(car, result);
    }

    // =========================
    // Order types combined with driver types
    // =========================

    @Test
    void expressOrderCanBeAssignedToCapableDriver() {
        MenuItem menu = new MenuItem("Pizza", 15, true, 0.5);
        ExpressOrder order = new ExpressOrder(
                List.of(new OrderItem(menu, 2)), 10, 5
        );
        CarDriver car = new CarDriver(
                "C1", "Sami", 32.22, 35.25, 10, 5
        );

        createService().assignDriver(order, car, 8);

        assertSame(car, order.getDriver());
        assertEquals(15.0, order.getDeliveryCost(), 0.001);
    }

    @Test
    void scheduledOrderIsSubjectToDriverTypeRules() {
        java.time.Clock clock = java.time.Clock.fixed(
                java.time.Instant.parse("2026-11-07T10:00:00Z"),
                java.time.ZoneOffset.UTC
        );
        ScheduledOrder order = new ScheduledOrder(
                List.of(new OrderItem(new MenuItem("Pizza", 15, true), 1)),
                10,
                java.time.LocalDateTime.of(2026, 11, 7, 12, 0),
                clock
        );
        BicycleDriver bicycle = new BicycleDriver(
                "B1", "Ahmad", 32.22, 35.25, 20, 3
        );

        assertThrows(IllegalStateException.class, () ->
                createService().assignDriver(order, bicycle, 8)
        );

        assertNull(order.getDriver());
    }

    // =========================
    // Older assignment overloads cannot bypass vehicle rules
    // =========================

    @Test
    void olderAssignDriverRejectsBicycleDriver() {
        Order order = lightOrder();
        BicycleDriver driver = new BicycleDriver(
                "B1", "Ahmad", 32.22, 35.25, 20, 5
        );

        assertThrows(IllegalStateException.class, () ->
                createService().assignDriver(order, driver)
        );

        assertNull(order.getDriver());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }

    @Test
    void olderAssignDriverRejectsMotorcycleAndElectricDrivers() {
        OrderService service = createService();
        Order first = lightOrder();
        Order second = lightOrder();
        MotorcycleDriver motorcycle = new MotorcycleDriver(
                "M1", "Omar", 32.22, 35.25, 15, 20
        );
        ElectricVehicleDriver electric = new ElectricVehicleDriver(
                "E1", "Lina", 32.22, 35.25, 10, 100
        );

        assertThrows(IllegalStateException.class,
                () -> service.assignDriver(first, motorcycle));
        assertThrows(IllegalStateException.class,
                () -> service.assignDriver(second, electric));

        assertNull(first.getDriver());
        assertNull(second.getDriver());
        assertEquals(DriverStatus.AVAILABLE, motorcycle.getStatus());
        assertEquals(DriverStatus.AVAILABLE, electric.getStatus());
    }

    @Test
    void olderAssignDriverAppliesCarWeightRule() {
        OrderService service = createService();
        Order heavy = orderWithWeight(2, 3);
        Order light = orderWithWeight(0.5, 2);
        CarDriver tooSmall = new CarDriver(
                "C1", "Sami", 32.22, 35.25, 10, 5
        );
        CarDriver fits = new CarDriver(
                "C2", "Sami", 32.22, 35.25, 10, 5
        );

        assertThrows(IllegalStateException.class,
                () -> service.assignDriver(heavy, tooSmall));
        assertNull(heavy.getDriver());
        assertEquals(DriverStatus.AVAILABLE, tooSmall.getStatus());

        service.assignDriver(light, fits);

        assertSame(fits, light.getDriver());
        assertEquals(DriverStatus.BUSY, fits.getStatus());
    }

    @Test
    void olderAssignDriverStillAcceptsPlainDriver() {
        Order order = lightOrder();
        Driver driver = new Driver("D1", "Ahmad", 32.22, 35.25, 20);

        createService().assignDriver(order, driver);

        assertSame(driver, order.getDriver());
        assertEquals(DriverStatus.BUSY, driver.getStatus());
    }

    @Test
    void olderStrategySkipsNearestBicycleAndPicksPlainDriver() {
        Order order = lightOrder();
        BicycleDriver bicycle = new BicycleDriver(
                "B1", "Ahmad", 32.22, 35.25, 20, 5
        );
        Driver plain = new Driver("D1", "Omar", 32.40, 35.40, 20);

        Driver result = createService().assignDriverUsingStrategy(
                order,
                List.of(bicycle, plain),
                32.22, 35.25,
                new NearestDriverStrategy()
        );

        assertSame(plain, result);
        assertSame(plain, order.getDriver());
        assertEquals(DriverStatus.AVAILABLE, bicycle.getStatus());
    }

    @Test
    void olderStrategyRejectsWhenOnlyDistanceLimitedDriversExist() {
        Order order = lightOrder();
        BicycleDriver bicycle = new BicycleDriver(
                "B1", "Ahmad", 32.22, 35.25, 20, 5
        );
        MotorcycleDriver motorcycle = new MotorcycleDriver(
                "M1", "Omar", 32.22, 35.25, 15, 20
        );
        ElectricVehicleDriver electric = new ElectricVehicleDriver(
                "E1", "Lina", 32.22, 35.25, 10, 100
        );

        assertThrows(IllegalStateException.class, () ->
                createService().assignDriverUsingStrategy(
                        order,
                        List.of(bicycle, motorcycle, electric),
                        32.22, 35.25,
                        new FastestDriverStrategy()
                ));

        assertNull(order.getDriver());
        assertEquals(DriverStatus.AVAILABLE, bicycle.getStatus());
        assertEquals(DriverStatus.AVAILABLE, motorcycle.getStatus());
        assertEquals(DriverStatus.AVAILABLE, electric.getStatus());
    }

    @Test
    void olderStrategyAppliesCarWeightRule() {
        Order heavy = orderWithWeight(2, 3);
        Order light = orderWithWeight(0.5, 2);
        CarDriver small = new CarDriver(
                "C1", "Sami", 32.22, 35.25, 10, 5
        );
        CarDriver small2 = new CarDriver(
                "C2", "Sami", 32.22, 35.25, 10, 5
        );

        assertThrows(IllegalStateException.class, () ->
                createService().assignDriverUsingStrategy(
                        heavy, List.of(small), 32.22, 35.25,
                        new NearestDriverStrategy()
                ));
        assertNull(heavy.getDriver());

        Driver result = createService().assignDriverUsingStrategy(
                light, List.of(small2), 32.22, 35.25,
                new NearestDriverStrategy()
        );

        assertSame(small2, result);
    }

    @Test
    void newerStrategyStillAssignsDistanceLimitedDriversWithinLimits() {
        Order order = lightOrder();
        BicycleDriver bicycle = new BicycleDriver(
                "B1", "Ahmad", 32.22, 35.25, 20, 5
        );

        Driver result = createService().assignDriverUsingStrategy(
                order, List.of(bicycle), 32.22, 35.25, 5,
                new NearestDriverStrategy()
        );

        assertSame(bicycle, result);
        assertEquals(DriverStatus.BUSY, bicycle.getStatus());
    }

    // =========================
    // Car capacity boundary (floating point)
    // =========================

    @Test
    void acceptsCarDriverForOrderExactlyAtCapacityOnEveryPath() {
        OrderService service = createService();
        CarDriver direct = new CarDriver(
                "C1", "Sami", 32.22, 35.25, 10, 2.4
        );
        CarDriver withDistance = new CarDriver(
                "C2", "Sami", 32.22, 35.25, 10, 2.4
        );
        CarDriver viaStrategy = new CarDriver(
                "C3", "Sami", 32.22, 35.25, 10, 2.4
        );

        // 0.8 * 3 is 2.4000000000000004 in floating point
        Order first = orderWithWeight(0.8, 3);
        Order second = orderWithWeight(0.8, 3);
        Order third = orderWithWeight(0.8, 3);

        service.assignDriver(first, direct);
        service.assignDriver(second, withDistance, 5);
        Driver result = service.assignDriverUsingStrategy(
                third, List.of(viaStrategy), 32.22, 35.25, 5,
                new NearestDriverStrategy()
        );

        assertSame(direct, first.getDriver());
        assertSame(withDistance, second.getDriver());
        assertSame(viaStrategy, result);
    }

    @Test
    void rejectsCarDriverForOrderGenuinelyAboveCapacityOnEveryPath() {
        OrderService service = createService();
        CarDriver driver = new CarDriver(
                "C1", "Sami", 32.22, 35.25, 10, 2.4
        );

        Order order = orderWithWeight(0.8001, 3);

        assertThrows(IllegalStateException.class,
                () -> service.assignDriver(order, driver));
        assertThrows(IllegalStateException.class,
                () -> service.assignDriver(order, driver, 5));
        assertThrows(IllegalStateException.class,
                () -> service.assignDriverUsingStrategy(
                        order, List.of(driver), 32.22, 35.25, 5,
                        new NearestDriverStrategy()));
        assertThrows(IllegalStateException.class,
                () -> service.assignDriverUsingStrategy(
                        order, List.of(driver), 32.22, 35.25,
                        new NearestDriverStrategy()));

        assertNull(order.getDriver());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }
}
