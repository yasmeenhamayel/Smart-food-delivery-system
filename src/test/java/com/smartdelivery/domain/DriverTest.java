
package com.smartdelivery.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DriverTest {

    @Test
    void createsDriverWithCorrectValues() {
        Driver driver = new Driver("D1", "Ahmad", 32.22, 35.25, 15);

        assertEquals("D1", driver.getId());
        assertEquals("Ahmad", driver.getName());
        assertEquals(32.22, driver.getLatitude());
        assertEquals(35.25, driver.getLongitude());
        assertEquals(15, driver.getEstimatedDeliveryTime());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }

    @Test
    void setsDriverStatusCorrectly() {
        Driver driver = new Driver("D1", "Ahmad", 32.22, 35.25, 15);

        driver.setStatus(DriverStatus.BUSY);

        assertEquals(DriverStatus.BUSY, driver.getStatus());
    }

    @Test
    void throwsExceptionWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Driver(null, "Ahmad", 32.22, 35.25, 15)
        );
    }

    @Test
    void throwsExceptionWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Driver("D1", null, 32.22, 35.25, 15)
        );
    }

    @Test
    void throwsExceptionWhenLatitudeIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                new Driver("D1", "Ahmad", 91, 35.25, 15)
        );
    }

    @Test
    void throwsExceptionWhenLongitudeIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                new Driver("D1", "Ahmad", 32.22, 181, 15)
        );
    }

    @Test
    void throwsExceptionWhenEstimatedDeliveryTimeIsNotPositive() {
        assertThrows(IllegalArgumentException.class, () ->
                new Driver("D1", "Ahmad", 32.22, 35.25, 0)
        );
    }

    @Test
    void throwsExceptionWhenSettingNullStatus() {
        Driver driver = new Driver("D1", "Ahmad", 32.22, 35.25, 15);

        assertThrows(IllegalArgumentException.class, () ->
                driver.setStatus(null)
        );
    }
}
