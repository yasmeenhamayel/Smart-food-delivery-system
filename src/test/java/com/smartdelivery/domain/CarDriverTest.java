package com.smartdelivery.domain;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CarDriverTest {

    @Test
    void createsCarDriverSuccessfully() {
        CarDriver driver = new CarDriver(
                "C1", "Ahmad", 32.2, 35.2, 10, 15
        );

        assertEquals("C1", driver.getId());
        assertEquals(15, driver.getMaxOrderWeight());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }

    @Test
    void acceptsOrderWithinMaximumWeight() {
        CarDriver driver = new CarDriver(
                "C1", "Ahmad", 32.2, 35.2, 10, 10
        );

        Order order = createOrder(6.0);

        assertTrue(driver.canHandle(order));
    }

    @Test
    void acceptsOrderAtMaximumWeight() {
        CarDriver driver = new CarDriver(
                "C1", "Ahmad", 32.2, 35.2, 10, 10
        );

        Order order = createOrder(10.0);

        assertTrue(driver.canHandle(order));
    }

    @Test
    void rejectsOrderBeyondMaximumWeight() {
        CarDriver driver = new CarDriver(
                "C1", "Ahmad", 32.2, 35.2, 10, 10
        );

        Order order = createOrder(12.0);

        assertFalse(driver.canHandle(order));
    }

    @Test
    void rejectsNullOrder() {
        CarDriver driver = new CarDriver(
                "C1", "Ahmad", 32.2, 35.2, 10, 10
        );

        assertFalse(driver.canHandle(null));
    }

    @Test
    void rejectsInvalidMaximumOrderWeight() {
        assertThrows(IllegalArgumentException.class, () ->
                new CarDriver("C1", "Ahmad", 32.2, 35.2, 10, 0)
        );

        assertThrows(IllegalArgumentException.class, () ->
                new CarDriver("C1", "Ahmad", 32.2, 35.2, 10, -5)
        );

        assertThrows(IllegalArgumentException.class, () ->
                new CarDriver("C1", "Ahmad", 32.2, 35.2, 10,
                        Double.NaN)
        );
    }

    private Order createOrder(double weight) {
        MenuItem item = new MenuItem("Pizza", 12.5, true, weight);
        OrderItem orderItem = new OrderItem(item, 1);

        return new Order(List.of(orderItem), 5.0);
    }

    @Test
    void acceptsOrderExactlyAtCapacityDespiteFloatingPointError() {
        CarDriver driver = new CarDriver(
                "C1", "Ahmad", 32.2, 35.2, 10, 2.4
        );

        MenuItem item = new MenuItem("Pizza", 12.5, true, 0.8);
        Order order = new Order(List.of(new OrderItem(item, 3)), 5.0);

        // 0.8 * 3 is 2.4000000000000004 in floating point
        assertTrue(order.getTotalWeight() > 2.4);
        assertTrue(driver.canHandle(order));
        assertTrue(driver.canHandleDelivery(order, 5));
    }

    @Test
    void rejectsOrderGenuinelyAboveCapacity() {
        CarDriver driver = new CarDriver(
                "C1", "Ahmad", 32.2, 35.2, 10, 2.4
        );

        MenuItem slightlyHeavier = new MenuItem("Pizza", 12.5, true, 0.8001);
        Order order = new Order(
                List.of(new OrderItem(slightlyHeavier, 3)), 5.0
        );

        assertFalse(driver.canHandle(order));
        assertFalse(driver.canHandleDelivery(order, 5));
    }

    @Test
    void rejectsOrderJustAboveCapacityByAMeaningfulMargin() {
        CarDriver driver = new CarDriver(
                "C1", "Ahmad", 32.2, 35.2, 10, 10
        );

        assertFalse(driver.canHandle(createOrder(10.000001)));
        assertTrue(driver.canHandle(createOrder(10.0)));
    }
}
