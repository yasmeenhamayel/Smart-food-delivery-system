package com.smartdelivery.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BicycleDriverTest {

    @Test
    void createsBicycleDriverSuccessfully() {
        BicycleDriver driver = new BicycleDriver(
                "B1", "Ahmad", 32.2, 35.2, 20, 5
        );

        assertEquals("B1", driver.getId());
        assertEquals(5, driver.getMaxDeliveryDistance());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }

    @Test
    void allowsDeliveryWithinMaximumDistance() {
        BicycleDriver driver = new BicycleDriver(
                "B1", "Ahmad", 32.2, 35.2, 20, 5
        );

        assertTrue(driver.canDeliver(3));
    }

    @Test
    void allowsDeliveryAtMaximumDistance() {
        BicycleDriver driver = new BicycleDriver(
                "B1", "Ahmad", 32.2, 35.2, 20, 5
        );

        assertTrue(driver.canDeliver(5));
    }

    @Test
    void rejectsDeliveryBeyondMaximumDistance() {
        BicycleDriver driver = new BicycleDriver(
                "B1", "Ahmad", 32.2, 35.2, 20, 5
        );

        assertFalse(driver.canDeliver(6));
    }

    @Test
    void rejectsInvalidDistances() {
        BicycleDriver driver = new BicycleDriver(
                "B1", "Ahmad", 32.2, 35.2, 20, 5
        );

        assertFalse(driver.canDeliver(0));
        assertFalse(driver.canDeliver(-2));
        assertFalse(driver.canDeliver(Double.NaN));
        assertFalse(driver.canDeliver(Double.POSITIVE_INFINITY));
    }

    @Test
    void rejectsInvalidMaximumDeliveryDistance() {
        assertThrows(IllegalArgumentException.class, () ->
                new BicycleDriver("B1", "Ahmad", 32.2, 35.2, 20, 0)
        );

        assertThrows(IllegalArgumentException.class, () ->
                new BicycleDriver("B1", "Ahmad", 32.2, 35.2, 20, -5)
        );

        assertThrows(IllegalArgumentException.class, () ->
                new BicycleDriver("B1", "Ahmad", 32.2, 35.2, 20,
                        Double.NaN)
        );
    }
}