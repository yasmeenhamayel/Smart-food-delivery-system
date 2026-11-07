
package com.smartdelivery.domain;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * A scheduled order. It must be delivered at a time that is strictly after the
 * moment the order is created.
 */
public class ScheduledOrder extends Order {

    private final LocalDateTime scheduledDeliveryTime;

    public ScheduledOrder(List<OrderItem> items,
                          double deliveryCost,
                          LocalDateTime scheduledDeliveryTime) {

        this(items, deliveryCost, scheduledDeliveryTime, Clock.systemDefaultZone());
    }

    // Constructor with Clock to make time validation testable
    public ScheduledOrder(List<OrderItem> items,
                          double deliveryCost,
                          LocalDateTime scheduledDeliveryTime,
                          Clock clock) {

        super(items, deliveryCost);

        if (scheduledDeliveryTime == null) {
            throw new IllegalArgumentException(
                    "Scheduled delivery time cannot be null");
        }

        if (clock == null) {
            throw new IllegalArgumentException("Clock cannot be null");
        }

        LocalDateTime currentTime = LocalDateTime.now(clock);

        if (!scheduledDeliveryTime.isAfter(currentTime)) {
            throw new IllegalArgumentException(
                    "Scheduled delivery time must be in the future");
        }

        this.scheduledDeliveryTime = scheduledDeliveryTime;
    }

    public LocalDateTime getScheduledDeliveryTime() {
        return scheduledDeliveryTime;
    }
}
