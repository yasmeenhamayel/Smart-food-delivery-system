package com.smartdelivery.domain;

import com.smartdelivery.pricing.ExpressDeliveryPricing;

import java.util.List;

public class ExpressOrder extends Order {

    public ExpressOrder(List<OrderItem> items,
                        double deliveryCost,
                        double expressFee) {

        super(items,
                new ExpressDeliveryPricing(expressFee)
                        .calculateDeliveryCost(deliveryCost));
    }
}