
package com.smartdelivery.observer;

import com.smartdelivery.domain.Order;
import com.smartdelivery.domain.OrderStatus;
import com.smartdelivery.domain.Driver;

public interface OrderObserver {

    void onOrderStatusChanged(Order order, OrderStatus newStatus);

    default void onDriverAssigned(Order order, Driver driver) {
    }
}
