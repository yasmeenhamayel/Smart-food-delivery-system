package com.smartdelivery.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MotorcycleDriverTest {

    @Test
    void createsMotorcycleDriverSuccessfully() {
        MotorcycleDriver driver = new MotorcycleDriver(
                "M1", "Ahmad", 32.2, 35.2, 15, 20
        );

        assertEquals("M1", driver.getId());
        assertEquals(20, driver.getMaxDeliveryDistance());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }

    @Test
    void allowsDeliveryWithinMaximumDistance() {
        MotorcycleDriver driver = new MotorcycleDriver(
                "M1", "Ahmad", 32.2, 35.2, 15, 20
        );

        assertTrue(driver.canDeliver(10));
    }

    @Test
    void allowsDeliveryAtMaximumDistance() {
        MotorcycleDriver driver = new MotorcycleDriver(
                "M1", "Ahmad", 32.2, 35.2, 15, 20
        );

        assertTrue(driver.canDeliver(20));
    }

    @Test
    void rejectsDeliveryBeyondMaximumDistance() {
        MotorcycleDriver driver = new MotorcycleDriver(
                "M1", "Ahmad", 32.2, 35.2, 15, 20
        );

        assertFalse(driver.canDeliver(21));
    }

    @Test
    void rejectsInvalidDistances() {
        MotorcycleDriver driver = new MotorcycleDriver(
                "M1", "Ahmad", 32.2, 35.2, 15, 20
        );

        assertFalse(driver.canDeliver(0));
        assertFalse(driver.canDeliver(-1));
        assertFalse(driver.canDeliver(Double.NaN));
        assertFalse(driver.canDeliver(Double.POSITIVE_INFINITY));
    }

    @Test
    void rejectsInvalidMaximumDeliveryDistance() {
        assertThrows(IllegalArgumentException.class, () ->
                new MotorcycleDriver("M1", "Ahmad", 32.2, 35.2, 15, 0)
        );

        assertThrows(IllegalArgumentException.class, () ->
                new MotorcycleDriver("M1", "Ahmad", 32.2, 35.2, 15, -5)
        );

        assertThrows(IllegalArgumentException.class, () ->
                new MotorcycleDriver("M1", "Ahmad", 32.2, 35.2, 15,
                        Double.NaN)
        );
    }
}