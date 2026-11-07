package com.smartdelivery.domain;

/**
 * An electric vehicle driver, who can only deliver distances that fit within
 * the vehicle's remaining range.
 */
public class ElectricVehicleDriver extends Driver {

    private double remainingRange;

    public ElectricVehicleDriver(String id, String name,
                                 double latitude, double longitude,
                                 double estimatedDeliveryTime,
                                 double remainingRange) {

        super(id, name, latitude, longitude, estimatedDeliveryTime);

        if (remainingRange <= 0 || !Double.isFinite(remainingRange)) {
            throw new IllegalArgumentException(
                    "Remaining range must be positive and finite");
        }

        this.remainingRange = remainingRange;
    }

    public double getRemainingRange() {
        return remainingRange;
    }

    public boolean canDeliver(double distance) {
        return distance > 0
                && Double.isFinite(distance)
                && distance <= remainingRange;
    }

    @Override
    public boolean canHandleDelivery(Order order, double distance) {
        return order != null && canDeliver(distance);
    }

    /**
     * A distance-limited driver cannot be verified without a distance.
     *
     * @param order the order to deliver
     * @return always {@code false}
     */
    @Override
    public boolean canHandleOrderWithoutDistance(Order order) {
        return false;
    }
}
