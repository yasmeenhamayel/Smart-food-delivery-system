package com.smartdelivery.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElectricVehicleDriverTest {

    @Test
    void createsElectricVehicleDriverSuccessfully() {
        ElectricVehicleDriver driver = new ElectricVehicleDriver(
                "E1", "Ahmad", 32.2, 35.2, 10, 100
        );

        assertEquals("E1", driver.getId());
        assertEquals(100, driver.getRemainingRange());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }

    @Test
    void acceptsDeliveryWithinRemainingRange() {
        ElectricVehicleDriver driver = new ElectricVehicleDriver(
                "E1", "Ahmad", 32.2, 35.2, 10, 100
        );

        assertTrue(driver.canDeliver(70));
    }

    @Test
    void acceptsDeliveryAtRemainingRangeLimit() {
        ElectricVehicleDriver driver = new ElectricVehicleDriver(
                "E1", "Ahmad", 32.2, 35.2, 10, 100
        );

        assertTrue(driver.canDeliver(100));
    }

    @Test
    void rejectsDeliveryBeyondRemainingRange() {
        ElectricVehicleDriver driver = new ElectricVehicleDriver(
                "E1", "Ahmad", 32.2, 35.2, 10, 100
        );

        assertFalse(driver.canDeliver(120));
    }

    @Test
    void rejectsInvalidDistance() {
        ElectricVehicleDriver driver = new ElectricVehicleDriver(
                "E1", "Ahmad", 32.2, 35.2, 10, 100
        );

        assertFalse(driver.canDeliver(0));
        assertFalse(driver.canDeliver(-5));
        assertFalse(driver.canDeliver(Double.NaN));
        assertFalse(driver.canDeliver(Double.POSITIVE_INFINITY));
    }

    @Test
    void rejectsInvalidRemainingRange() {
        assertThrows(IllegalArgumentException.class, () ->
                new ElectricVehicleDriver(
                        "E1", "Ahmad", 32.2, 35.2, 10, 0
                ));

        assertThrows(IllegalArgumentException.class, () ->
                new ElectricVehicleDriver(
                        "E1", "Ahmad", 32.2, 35.2, 10, -10
                ));

        assertThrows(IllegalArgumentException.class, () ->
                new ElectricVehicleDriver(
                        "E1", "Ahmad", 32.2, 35.2, 10, Double.NaN
                ));
    }
}