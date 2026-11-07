package com.smartdelivery.domain;

public class CarDriver extends Driver {

    private double maxOrderWeight;

    public CarDriver(String id, String name,
                     double latitude, double longitude,
                     double estimatedDeliveryTime,
                     double maxOrderWeight) {

        super(id, name, latitude, longitude, estimatedDeliveryTime);

        if (maxOrderWeight <= 0 || !Double.isFinite(maxOrderWeight)) {
            throw new IllegalArgumentException(
                    "Maximum order weight must be positive and finite");
        }

        this.maxOrderWeight = maxOrderWeight;
    }

    public double getMaxOrderWeight() {
        return maxOrderWeight;
    }

    public boolean canHandle(Order order) {
        return order != null
                && order.getTotalWeight() <= maxOrderWeight;
    }

    @Override
    public boolean canHandleDelivery(Order order, double distance) {
        return canHandle(order);
    }
}