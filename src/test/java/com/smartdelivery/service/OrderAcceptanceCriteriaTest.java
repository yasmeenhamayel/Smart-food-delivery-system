package com.smartdelivery.service;

import com.smartdelivery.domain.Address;
import com.smartdelivery.domain.MenuItem;
import com.smartdelivery.domain.Order;
import com.smartdelivery.domain.OrderItem;
import com.smartdelivery.domain.OrderStatus;
import com.smartdelivery.domain.Restaurant;
import com.smartdelivery.observer.OrderObserver;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Verifies the Sprint 2 acceptance criteria (US2.1 to US2.4) through
 * {@link OrderService#placeOrder} and {@link OrderService#cancelOrder}.
 *
 * <p>Every test in this class calls the service. Rules that are enforced
 * earlier, by the {@link OrderItem} and {@link Address} constructors, are
 * covered by {@code OrderItemTest} and {@code AddressTest}.</p>
 */
class OrderAcceptanceCriteriaTest {

    private final Restaurant restaurant = new Restaurant(
            "Pizza House", true, 20, Set.of("Nablus", "Tubas"));

    private final Address address = new Address(
            "Nablus", "Taluza", "main street", "yellow house");

    private OrderService createService() {
        return new OrderService(
                mock(OrderObserver.class), new DeliveryCostCalculator(2));
    }

    private OrderItem item(double price, int quantity) {
        return new OrderItem(new MenuItem("Item", price, true), quantity);
    }

    // ---------- US2.1: Place a Food Order ----------

    @Test
    void placeOrderCreatesPendingOrderWithCorrectTotalForMultipleItems() {
        Order order = createService().placeOrder(
                restaurant, List.of(item(15, 2), item(8, 1)), address, 5);

        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(38.0, order.getItemTotal(), 0.001);
        assertEquals(10.0, order.getDeliveryCost(), 0.001);
        assertEquals(48.0, order.getTotal(), 0.001);
    }

    // ---------- US2.2: Prevent Invalid Orders ----------

    @Test
    void placeOrderRejectsEmptyItemList() {
        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(restaurant, List.of(), address, 5));
    }

    @Test
    void placeOrderRejectsNullItemList() {
        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(restaurant, null, address, 5));
    }

    @Test
    void placeOrderRejectsItemListContainingNull() {
        OrderService service = createService();
        List<OrderItem> items = Arrays.asList(item(30, 1), null);

        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(restaurant, items, address, 5));
    }

    // ---------- US2.3: Enforce Delivery Policies ----------

    @Test
    void placeOrderAcceptsOrderExactlyAtMinimumOrderValue() {
        Order order = createService().placeOrder(
                restaurant, List.of(item(20, 1)), address, 5);

        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void placeOrderRejectsOrderJustBelowMinimumOrderValue() {
        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(
                        restaurant, List.of(item(19.99, 1)), address, 5));
    }

    @Test
    void placeOrderAcceptsAddressCityWithSurroundingSpaces() {
        Address spacedAddress = new Address(
                "  Nablus ", "Taluza", "main street", null);

        Order order = createService().placeOrder(
                restaurant, List.of(item(30, 1)), spacedAddress, 5);

        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void placeOrderRejectsAddressOutsideDeliveryZone() {
        OrderService service = createService();
        Address farAddress = new Address("Jenin", "Center", "main street", null);

        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(
                        restaurant, List.of(item(30, 1)), farAddress, 5));
    }

    @Test
    void placeOrderRejectsNullAddress() {
        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(
                        restaurant, List.of(item(30, 1)), null, 5));
    }

    // ---------- US2.4: Cancel an Order ----------

    @Test
    void cancelOrderCancelsPlacedPendingOrder() {
        OrderService service = createService();
        Order order = service.placeOrder(
                restaurant, List.of(item(30, 1)), address, 5);

        service.cancelOrder(order);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void cancelOrderRejectsPlacedOrderOnceItIsPreparing() {
        OrderService service = createService();
        Order order = service.placeOrder(
                restaurant, List.of(item(30, 1)), address, 5);
        order.confirm();
        order.startPreparing();

        assertThrows(IllegalStateException.class,
                () -> service.cancelOrder(order));

        assertEquals(OrderStatus.PREPARING, order.getStatus());
    }
}