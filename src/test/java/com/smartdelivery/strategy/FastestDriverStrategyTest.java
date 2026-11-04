
package com.smartdelivery.strategy;

import com.smartdelivery.domain.Driver;
import com.smartdelivery.domain.DriverStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FastestDriverStrategyTest {

    private final FastestDriverStrategy strategy = new FastestDriverStrategy();

    @Test
    void testSelectFastestAvailableDriver() {

        Driver driver1 = new Driver("D1", "Ahmad",
                32.01, 35.0, 20);

        Driver driver2 = new Driver("D2", "Omar",
                32.02, 35.0, 10);

        Driver result = strategy.selectDriver(
                List.of(driver1, driver2), 32.0, 35.0
        );

        assertSame(driver2, result);
    }

    @Test
    void testIgnoreBusyDriver() {

        Driver busyDriver = new Driver("D1", "Ahmad",
                32.01, 35.0, 5);

        Driver availableDriver = new Driver("D2", "Omar",
                32.02, 35.0, 15);

        busyDriver.setStatus(DriverStatus.BUSY);

        Driver result = strategy.selectDriver(
                List.of(busyDriver, availableDriver), 32.0, 35.0
        );

        assertSame(availableDriver, result);
    }

    @Test
    void testReturnNullWhenNoDriverIsAvailable() {

        Driver driver1 = new Driver("D1", "Ahmad",
                32.01, 35.0, 10);

        Driver driver2 = new Driver("D2", "Omar",
                32.02, 35.0, 20);

        driver1.setStatus(DriverStatus.BUSY);
        driver2.setStatus(DriverStatus.BUSY);

        Driver result = strategy.selectDriver(
                List.of(driver1, driver2), 32.0, 35.0
        );

        assertNull(result);
    }
}
