
package com.smartdelivery.strategy;

import com.smartdelivery.domain.Driver;
import com.smartdelivery.domain.DriverStatus;

import java.util.List;

// Selects the nearest available driver to the restaurant.
public class NearestDriverStrategy implements DriverSelectionStrategy {

    @Override
    public Driver selectDriver(List<Driver> drivers,
                               double restaurantLatitude,
                               double restaurantLongitude) {

        Driver nearestDriver = null;
        double minDistance = Double.MAX_VALUE;

        for (Driver driver : drivers) {

            if (driver.getStatus() == DriverStatus.AVAILABLE) {

                double distance = calculateDistance(
                        restaurantLatitude,
                        restaurantLongitude,
                        driver.getLatitude(),
                        driver.getLongitude()
                );

                if (distance < minDistance) {
                    minDistance = distance;
                    nearestDriver = driver;
                }
            }
        }

        return nearestDriver;
    }

    private double calculateDistance(double lat1, double lon1,
                                     double lat2, double lon2) {

        final double EARTH_RADIUS = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2)
                * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}
