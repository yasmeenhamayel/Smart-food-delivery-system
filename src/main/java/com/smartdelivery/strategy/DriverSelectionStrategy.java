package com.smartdelivery.strategy;

import com.smartdelivery.domain.Driver;
import java.util.List;

// Defines the strategy used to select a delivery driver.
public interface DriverSelectionStrategy {

    Driver selectDriver(List<Driver> drivers,
                        double restaurantLatitude,
                        double restaurantLongitude);
}