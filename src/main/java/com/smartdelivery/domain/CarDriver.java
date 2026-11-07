package com.smartdelivery.domain;

/**
 * A car driver, who can carry orders up to a maximum total weight.
 */
public class CarDriver extends Driver {

    /** Absolute tolerance used when comparing weights, to absorb rounding error. */
    private static final double WEIGHT_TOLERANCE = 1e-9;

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
                && order.getTotalWeight() <= maxOrderWeight + WEIGHT_TOLERANCE;
    }

    @Override
    public boolean canHandleDelivery(Order order, double distance) {
        return canHandle(order);
    }

    /**
     * The car rule depends only on the order weight, so it can be verified
     * without a delivery distance.
     *
     * @param order the order to deliver
     * @return {@code true} if the order weight is within the car's capacity
     */
    @Override
    public boolean canHandleOrderWithoutDistance(Order order) {
        return canHandle(order);
    }
}