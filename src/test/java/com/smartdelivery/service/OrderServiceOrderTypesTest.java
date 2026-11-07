package com.smartdelivery.service;

import com.smartdelivery.domain.*;
import com.smartdelivery.observer.OrderObserver;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceOrderTypesTest {

    private final Restaurant restaurant = new Restaurant(
            "Pizza House",
            true,
            20,
            Set.of("Nablus", "Tubas", "Tamoon")
    );

    private final OrderItem item = new OrderItem(
            new MenuItem("Pizza", 15, true),
            2
    );

    private final Address address = new Address(
            "Nablus",
            "Taluza",
            "main street",
            "in front of yellow house"
    );

    private OrderService createService(OrderObserver observer) {
        return new OrderService(
                observer,
                new DeliveryCostCalculator(2)
        );
    }

    // Standard Order Tests

    @Test
    void createsStandardOrderCorrectly() {

        OrderService service = createService(
                mock(OrderObserver.class)
        );

        StandardOrder order = service.placeStandardOrder(
                restaurant,
                List.of(item),
                address,
                5
        );

        assertNotNull(order);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(30.0, order.getItemTotal(), 0.001);
        assertEquals(10.0, order.getDeliveryCost(), 0.001);
        assertEquals(40.0, order.getTotal(), 0.001);
    }

    // Express Order Tests

    @Test
    void createsExpressOrderWithAdditionalFee() {

        OrderService service = createService(
                mock(OrderObserver.class)
        );

        ExpressOrder order = service.placeExpressOrder(
                restaurant,
                List.of(item),
                address,
                5,
                5
        );

        assertNotNull(order);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(30.0, order.getItemTotal(), 0.001);
        assertEquals(15.0, order.getDeliveryCost(), 0.001);
        assertEquals(45.0, order.getTotal(), 0.001);
    }

    @Test
    void rejectsExpressOrderWithNegativeExpressFee() {

        OrderService service = createService(
                mock(OrderObserver.class)
        );

        assertThrows(IllegalArgumentException.class,
                () -> service.placeExpressOrder(
                        restaurant,
                        List.of(item),
                        address,
                        5,
                        -5
                ));
    }

    // Scheduled Order Tests

    @Test
    void createsScheduledOrderWithFutureDeliveryTime() {

        OrderObserver observer = mock(OrderObserver.class);
        OrderService service = createService(observer);

        Clock clock = Clock.fixed(
                Instant.parse("2026-11-07T10:00:00Z"),
                ZoneOffset.UTC
        );

        LocalDateTime deliveryTime =
                LocalDateTime.of(2026, 11, 7, 11, 0);

        ScheduledOrder order = service.placeScheduledOrder(
                restaurant,
                List.of(item),
                address,
                5,
                deliveryTime,
                clock
        );

        assertNotNull(order);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(deliveryTime, order.getScheduledDeliveryTime());
        assertEquals(10.0, order.getDeliveryCost(), 0.001);
    }

    @Test
    void rejectsScheduledOrderWithPastDeliveryTime() {

        OrderService service = createService(
                mock(OrderObserver.class)
        );

        Clock clock = Clock.fixed(
                Instant.parse("2026-11-07T10:00:00Z"),
                ZoneOffset.UTC
        );

        LocalDateTime deliveryTime =
                LocalDateTime.of(2026, 11, 7, 9, 0);

        assertThrows(IllegalArgumentException.class,
                () -> service.placeScheduledOrder(
                        restaurant,
                        List.of(item),
                        address,
                        5,
                        deliveryTime,
                        clock
                ));
    }

    // Observer Test

    @Test
    void registersObserverForExpressOrder() {

        OrderObserver observer = mock(OrderObserver.class);
        OrderService service = createService(observer);

        ExpressOrder order = service.placeExpressOrder(
                restaurant,
                List.of(item),
                address,
                5,
                5
        );

        service.cancelOrder(order);

        verify(observer).onOrderStatusChanged(
                order,
                OrderStatus.CANCELLED
        );
    }

    // Common Validation Tests

    @Test
    void rejectsExpressOrderForUncoveredArea() {

        Restaurant restaurantWithoutNablus = new Restaurant(
                "Pizza House",
                true,
                20,
                Set.of("Tubas", "Tamoon")
        );

        OrderService service = createService(
                mock(OrderObserver.class)
        );

        assertThrows(IllegalArgumentException.class,
                () -> service.placeExpressOrder(
                        restaurantWithoutNablus,
                        List.of(item),
                        address,
                        5,
                        5
                ));
    }

    @Test
    void rejectsStandardOrderBelowMinimumValue() {

        Restaurant restaurantWithHighMinimum = new Restaurant(
                "Pizza House",
                true,
                50,
                Set.of("Nablus", "Tubas", "Tamoon")
        );

        OrderService service = createService(
                mock(OrderObserver.class)
        );

        assertThrows(IllegalArgumentException.class,
                () -> service.placeStandardOrder(
                        restaurantWithHighMinimum,
                        List.of(item),
                        address,
                        5
                ));
    }

    // Additional Scheduled Order Tests

    @Test
    void acceptsScheduledOrderFarInTheFutureWithSystemClock() {

        OrderService service = createService(
                mock(OrderObserver.class)
        );

        LocalDateTime deliveryTime = LocalDateTime.now().plusYears(1);

        ScheduledOrder order = service.placeScheduledOrder(
                restaurant,
                List.of(item),
                address,
                5,
                deliveryTime
        );

        assertEquals(deliveryTime, order.getScheduledDeliveryTime());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void rejectsScheduledOrderInThePastWithSystemClock() {

        OrderService service = createService(
                mock(OrderObserver.class)
        );

        LocalDateTime deliveryTime = LocalDateTime.now().minusYears(1);

        assertThrows(IllegalArgumentException.class,
                () -> service.placeScheduledOrder(
                        restaurant,
                        List.of(item),
                        address,
                        5,
                        deliveryTime
                ));
    }

    @Test
    void rejectsScheduledOrderWithCurrentDeliveryTime() {

        OrderService service = createService(
                mock(OrderObserver.class)
        );

        Clock clock = Clock.fixed(
                Instant.parse("2026-11-07T10:00:00Z"),
                ZoneOffset.UTC
        );

        assertThrows(IllegalArgumentException.class,
                () -> service.placeScheduledOrder(
                        restaurant,
                        List.of(item),
                        address,
                        5,
                        LocalDateTime.of(2026, 11, 7, 10, 0),
                        clock
                ));
    }

    @Test
    void rejectsScheduledOrderWithNullDeliveryTime() {

        OrderService service = createService(
                mock(OrderObserver.class)
        );

        Clock clock = Clock.fixed(
                Instant.parse("2026-11-07T10:00:00Z"),
                ZoneOffset.UTC
        );

        assertThrows(IllegalArgumentException.class,
                () -> service.placeScheduledOrder(
                        restaurant,
                        List.of(item),
                        address,
                        5,
                        null,
                        clock
                ));
    }

    @Test
    void registersObserverForScheduledOrder() {

        OrderObserver observer = mock(OrderObserver.class);
        OrderService service = createService(observer);

        Clock clock = Clock.fixed(
                Instant.parse("2026-11-07T10:00:00Z"),
                ZoneOffset.UTC
        );

        ScheduledOrder order = service.placeScheduledOrder(
                restaurant,
                List.of(item),
                address,
                5,
                LocalDateTime.of(2026, 11, 7, 11, 0),
                clock
        );

        service.cancelOrder(order);

        verify(observer).onOrderStatusChanged(
                order,
                OrderStatus.CANCELLED
        );
    }

    @Test
    void registersObserverForStandardOrder() {

        OrderObserver observer = mock(OrderObserver.class);
        OrderService service = createService(observer);

        StandardOrder order = service.placeStandardOrder(
                restaurant,
                List.of(item),
                address,
                5
        );

        service.cancelOrder(order);

        verify(observer).onOrderStatusChanged(
                order,
                OrderStatus.CANCELLED
        );
    }

    @Test
    void rejectsScheduledOrderForUncoveredArea() {

        Restaurant restaurantWithoutNablus = new Restaurant(
                "Pizza House",
                true,
                20,
                Set.of("Tubas", "Tamoon")
        );

        OrderService service = createService(
                mock(OrderObserver.class)
        );

        Clock clock = Clock.fixed(
                Instant.parse("2026-11-07T10:00:00Z"),
                ZoneOffset.UTC
        );

        assertThrows(IllegalArgumentException.class,
                () -> service.placeScheduledOrder(
                        restaurantWithoutNablus,
                        List.of(item),
                        address,
                        5,
                        LocalDateTime.of(2026, 11, 7, 11, 0),
                        clock
                ));
    }

    @Test
    void rejectsScheduledOrderBelowMinimumValue() {

        Restaurant restaurantWithHighMinimum = new Restaurant(
                "Pizza House",
                true,
                50,
                Set.of("Nablus", "Tubas", "Tamoon")
        );

        OrderService service = createService(
                mock(OrderObserver.class)
        );

        Clock clock = Clock.fixed(
                Instant.parse("2026-11-07T10:00:00Z"),
                ZoneOffset.UTC
        );

        assertThrows(IllegalArgumentException.class,
                () -> service.placeScheduledOrder(
                        restaurantWithHighMinimum,
                        List.of(item),
                        address,
                        5,
                        LocalDateTime.of(2026, 11, 7, 11, 0),
                        clock
                ));
    }

    @Test
    void rejectsExpressOrderBelowMinimumValue() {

        Restaurant restaurantWithHighMinimum = new Restaurant(
                "Pizza House",
                true,
                50,
                Set.of("Nablus", "Tubas", "Tamoon")
        );

        OrderService service = createService(
                mock(OrderObserver.class)
        );

        assertThrows(IllegalArgumentException.class,
                () -> service.placeExpressOrder(
                        restaurantWithHighMinimum,
                        List.of(item),
                        address,
                        5,
                        5
                ));
    }

    @Test
    void rejectsStandardOrderForUncoveredArea() {

        Restaurant restaurantWithoutNablus = new Restaurant(
                "Pizza House",
                true,
                20,
                Set.of("Tubas", "Tamoon")
        );

        OrderService service = createService(
                mock(OrderObserver.class)
        );

        assertThrows(IllegalArgumentException.class,
                () -> service.placeStandardOrder(
                        restaurantWithoutNablus,
                        List.of(item),
                        address,
                        5
                ));
    }

    @Test
    void rejectsNullRestaurantAndAddressForAllOrderTypes() {

        OrderService service = createService(
                mock(OrderObserver.class)
        );

        Clock clock = Clock.fixed(
                Instant.parse("2026-11-07T10:00:00Z"),
                ZoneOffset.UTC
        );

        LocalDateTime deliveryTime =
                LocalDateTime.of(2026, 11, 7, 11, 0);

        assertThrows(IllegalArgumentException.class,
                () -> service.placeStandardOrder(
                        null, List.of(item), address, 5));
        assertThrows(IllegalArgumentException.class,
                () -> service.placeStandardOrder(
                        restaurant, List.of(item), null, 5));
        assertThrows(IllegalArgumentException.class,
                () -> service.placeExpressOrder(
                        null, List.of(item), address, 5, 5));
        assertThrows(IllegalArgumentException.class,
                () -> service.placeExpressOrder(
                        restaurant, List.of(item), null, 5, 5));
        assertThrows(IllegalArgumentException.class,
                () -> service.placeScheduledOrder(
                        null, List.of(item), address, 5,
                        deliveryTime, clock));
        assertThrows(IllegalArgumentException.class,
                () -> service.placeScheduledOrder(
                        restaurant, List.of(item), null, 5,
                        deliveryTime, clock));
    }

    @Test
    void rejectsInvalidDistanceForAllOrderTypes() {

        OrderService service = createService(
                mock(OrderObserver.class)
        );

        Clock clock = Clock.fixed(
                Instant.parse("2026-11-07T10:00:00Z"),
                ZoneOffset.UTC
        );

        LocalDateTime deliveryTime =
                LocalDateTime.of(2026, 11, 7, 11, 0);

        assertThrows(IllegalArgumentException.class,
                () -> service.placeStandardOrder(
                        restaurant, List.of(item), address, 0));
        assertThrows(IllegalArgumentException.class,
                () -> service.placeExpressOrder(
                        restaurant, List.of(item), address, -1, 5));
        assertThrows(IllegalArgumentException.class,
                () -> service.placeScheduledOrder(
                        restaurant, List.of(item), address, 0,
                        deliveryTime, clock));
    }

    @Test
    void allOrderTypesFollowTheSameCostRulesForTheSameDistance() {

        OrderService service = createService(
                mock(OrderObserver.class)
        );

        Clock clock = Clock.fixed(
                Instant.parse("2026-11-07T10:00:00Z"),
                ZoneOffset.UTC
        );

        Order standard = service.placeStandardOrder(
                restaurant, List.of(item), address, 5);
        Order express = service.placeExpressOrder(
                restaurant, List.of(item), address, 5, 4);
        Order scheduled = service.placeScheduledOrder(
                restaurant, List.of(item), address, 5,
                LocalDateTime.of(2026, 11, 7, 11, 0), clock);

        assertEquals(10.0, standard.getDeliveryCost(), 0.001);
        assertEquals(14.0, express.getDeliveryCost(), 0.001);
        assertEquals(10.0, scheduled.getDeliveryCost(), 0.001);
    }
}
