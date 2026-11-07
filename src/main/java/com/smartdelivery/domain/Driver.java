
package com.smartdelivery.domain;


// Represents a delivery driver in the food delivery system.

public class Driver {

  
    private String id;


    private String name;

   
    private DriverStatus status;

  
    private double latitude;

   
    private double longitude;

   
    private double estimatedDeliveryTime;

    /**
     * Creates a new driver.
     *
     * @param id the driver's ID
     * @param name the driver's name
     * @param latitude the driver's latitude
     * @param longitude the driver's longitude
     * @param estimatedDeliveryTime the estimated delivery time
     */
    public Driver(String id, String name, double latitude,
                  double longitude, double estimatedDeliveryTime) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Driver ID is required");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Driver name is required");
        }

        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Invalid latitude");
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid longitude");
        }

        if (estimatedDeliveryTime <= 0) {
            throw new IllegalArgumentException("Delivery time must be positive");
        }

        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.status = DriverStatus.AVAILABLE;
    }

    /** @return the driver's ID */
    public String getId() {
        return id;
    }

    /** @return the driver's name */
    public String getName() {
        return name;
    }

    /**
     * Checks whether this driver can handle a delivery.
     *
     * <p>A plain driver has no vehicle restrictions, so it can handle any
     * delivery. Vehicle-specific subclasses override this method to apply
     * their own rules.</p>
     *
     * @param order the order to deliver
     * @param distance the delivery distance in kilometres
     * @return {@code true} if this driver can handle the delivery
     */
    public boolean canHandleDelivery(Order order, double distance) {
        return true;
    }

    /**
     * Checks whether this driver can be given an order when the delivery
     * distance is not known.
     *
     * <p>A plain driver has no vehicle restrictions, so it is always
     * eligible. Vehicle types whose rule depends on the distance override this
     * method and return {@code false}, because their rule cannot be verified
     * without a distance. Vehicle types whose rule depends only on the order
     * apply that rule.</p>
     *
     * @param order the order to deliver
     * @return {@code true} if eligibility can be confirmed from the order alone
     */
    public boolean canHandleOrderWithoutDistance(Order order) {
        return true;
    }

    /** @return the driver's current status */
    public DriverStatus getStatus() {
        return status;
    }

    /** @return the driver's latitude */
    public double getLatitude() {
        return latitude;
    }

    /** @return the driver's longitude */
    public double getLongitude() {
        return longitude;
    }

    /** @return the estimated delivery time */
    public double getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    /**
     * Changes the driver's status.
     *
     * @param status the new driver status
     */
    public void setStatus(DriverStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Driver status cannot be null");
        }

        this.status = status;
    }
}
