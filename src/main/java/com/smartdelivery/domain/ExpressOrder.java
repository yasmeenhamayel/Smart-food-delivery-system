package com.smartdelivery.domain;

import com.smartdelivery.pricing.ExpressDeliveryPricing;

import java.util.List;

/**
 * An express order. Its delivery cost is the base delivery cost plus an
 * additional express fee, applied once when the order is created.
 */
public class ExpressOrder extends Order {

    public ExpressOrder(List<OrderItem> items,
                        double deliveryCost,
                        double expressFee) {

        super(items,
                new ExpressDeliveryPricing(expressFee)
                        .calculateDeliveryCost(deliveryCost));
    }
}