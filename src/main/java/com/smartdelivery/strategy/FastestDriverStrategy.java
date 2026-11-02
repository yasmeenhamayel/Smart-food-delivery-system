
package com.smartdelivery.strategy;

import com.smartdelivery.domain.Driver;
import com.smartdelivery.domain.DriverStatus;

import java.util.List;

// Selects the available driver with the shortest estimated delivery time.
public class FastestDriverStrategy implements DriverSelectionStrategy {

    @Override
    public Driver selectDriver(List<Driver> drivers,
                               double restaurantLatitude,
                               double restaurantLongitude) {

        Driver fastestDriver = null;
        double minTime = Double.MAX_VALUE;

        for (Driver driver : drivers) {

            if (driver.getStatus() == DriverStatus.AVAILABLE) {

                if (driver.getEstimatedDeliveryTime() < minTime) {
                    minTime = driver.getEstimatedDeliveryTime();
                    fastestDriver = driver;
                }
            }
        }

        return fastestDriver;
    }
}
