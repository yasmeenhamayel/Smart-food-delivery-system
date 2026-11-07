package com.smartdelivery.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sprint 5 (US5.3 and US5.4): checks that every driver type applies its own
 * rule when used through the common {@link Driver} type.
 */
class DriverPolymorphismTest {

    private Order orderWithWeight(double weight) {
        MenuItem menu = new MenuItem("Pizza", 12.5, true, weight);

        return new Order(List.of(new OrderItem(menu, 1)), 5.0);
    }

    private Order lightOrder() {
        return new Order(List.of(
                new OrderItem(new MenuItem("Pizza", 12.5, true), 1)
        ), 5.0);
    }

    @Test
    void allDriverTypesAreDrivers() {
        List<Driver> drivers = List.of(
                new BicycleDriver("B1", "A", 32.2, 35.2, 20, 5),
                new MotorcycleDriver("M1", "B", 32.2, 35.2, 15, 20),
                new CarDriver("C1", "C", 32.2, 35.2, 10, 15),
                new ElectricVehicleDriver("E1", "D", 32.2, 35.2, 10, 100)
        );

        for (Driver driver : drivers) {
            assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
        }
    }

    @Test
    void plainDriverHandlesAnyDelivery() {
        Driver driver = new Driver("D1", "Ahmad", 32.2, 35.2, 10);

        assertTrue(driver.canHandleDelivery(lightOrder(), 500));
    }

    @Test
    void bicycleAppliesDistanceLimitThroughDriverType() {
        Driver driver = new BicycleDriver("B1", "A", 32.2, 35.2, 20, 5);

        assertTrue(driver.canHandleDelivery(lightOrder(), 5));
        assertFalse(driver.canHandleDelivery(lightOrder(), 5.01));
    }

    @Test
    void motorcycleAppliesDistanceLimitThroughDriverType() {
        Driver driver = new MotorcycleDriver("M1", "B", 32.2, 35.2, 15, 20);

        assertTrue(driver.canHandleDelivery(lightOrder(), 20));
        assertFalse(driver.canHandleDelivery(lightOrder(), 20.01));
    }

    @Test
    void carAppliesWeightLimitAndIgnoresDistance() {
        Driver driver = new CarDriver("C1", "C", 32.2, 35.2, 10, 10);

        assertTrue(driver.canHandleDelivery(orderWithWeight(10), 500));
        assertFalse(driver.canHandleDelivery(orderWithWeight(10.01), 1));
    }

    @Test
    void electricVehicleAppliesRangeLimitThroughDriverType() {
        Driver driver = new ElectricVehicleDriver(
                "E1", "D", 32.2, 35.2, 10, 100
        );

        assertTrue(driver.canHandleDelivery(lightOrder(), 100));
        assertFalse(driver.canHandleDelivery(lightOrder(), 100.01));
    }

    @Test
    void vehicleDriversRejectNullOrder() {
        assertFalse(new BicycleDriver("B1", "A", 32.2, 35.2, 20, 5)
                .canHandleDelivery(null, 3));
        assertFalse(new MotorcycleDriver("M1", "B", 32.2, 35.2, 15, 20)
                .canHandleDelivery(null, 3));
        assertFalse(new CarDriver("C1", "C", 32.2, 35.2, 10, 15)
                .canHandleDelivery(null, 3));
        assertFalse(new ElectricVehicleDriver("E1", "D", 32.2, 35.2, 10, 100)
                .canHandleDelivery(null, 3));
    }

    @Test
    void distanceLimitedDriversRejectInvalidDistanceThroughDriverType() {
        List<Driver> drivers = List.of(
                new BicycleDriver("B1", "A", 32.2, 35.2, 20, 5),
                new MotorcycleDriver("M1", "B", 32.2, 35.2, 15, 20),
                new ElectricVehicleDriver("E1", "D", 32.2, 35.2, 10, 100)
        );

        for (Driver driver : drivers) {
            assertFalse(driver.canHandleDelivery(lightOrder(), 0));
            assertFalse(driver.canHandleDelivery(lightOrder(), -1));
            assertFalse(driver.canHandleDelivery(lightOrder(), Double.NaN));
        }
    }

    @Test
    void unweightedItemsDoNotLimitCarDriver() {
        Driver driver = new CarDriver("C1", "C", 32.2, 35.2, 10, 0.5);

        assertTrue(driver.canHandleDelivery(lightOrder(), 5));
    }

    @Test
    void rejectsMotorcycleDriverWithInvalidMaximumDistance() {
        assertThrows(IllegalArgumentException.class, () ->
                new MotorcycleDriver("M1", "B", 32.2, 35.2, 15,
                        Double.POSITIVE_INFINITY)
        );
    }

    @Test
    void motorcycleRejectsInvalidDistances() {
        MotorcycleDriver driver =
                new MotorcycleDriver("M1", "B", 32.2, 35.2, 15, 20);

        assertFalse(driver.canDeliver(0));
        assertFalse(driver.canDeliver(-2));
        assertFalse(driver.canDeliver(Double.NaN));
        assertFalse(driver.canDeliver(Double.POSITIVE_INFINITY));
    }
}
