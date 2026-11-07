package com.smartdelivery.domain;

import java.util.List;

/**
 * A standard order, which follows the normal delivery policies and adds no
 * type-specific fee or time restriction.
 */
public class StandardOrder extends Order {

    public StandardOrder(List<OrderItem> items, double deliveryCost) {
        super(items, deliveryCost);
    }
}