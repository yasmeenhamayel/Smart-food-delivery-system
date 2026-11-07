package com.smartdelivery.domain;

public class BicycleDriver extends Driver {

    private double maxDeliveryDistance;

    public BicycleDriver(String id, String name,
                         double latitude, double longitude,
                         double estimatedDeliveryTime,
                         double maxDeliveryDistance) {

        super(id, name, latitude, longitude, estimatedDeliveryTime);

        if (maxDeliveryDistance <= 0 ||
                !Double.isFinite(maxDeliveryDistance)) {
            throw new IllegalArgumentException(
                    "Maximum delivery distance must be positive and finite");
        }

        this.maxDeliveryDistance = maxDeliveryDistance;
    }

    public double getMaxDeliveryDistance() {
        return maxDeliveryDistance;
    }

    public boolean canDeliver(double distance) {
        return distance > 0
                && Double.isFinite(distance)
                && distance <= maxDeliveryDistance;
    }

    @Override
    public boolean canHandleDelivery(Order order, double distance) {
        return order != null && canDeliver(distance);
    }
}