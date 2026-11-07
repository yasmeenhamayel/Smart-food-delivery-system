package com.smartdelivery.domain;

import java.util.List;

public class StandardOrder extends Order {

    public StandardOrder(List<OrderItem> items, double deliveryCost) {
        super(items, deliveryCost);
    }
}