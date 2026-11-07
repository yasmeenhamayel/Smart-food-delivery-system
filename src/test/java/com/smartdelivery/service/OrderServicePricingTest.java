package com.smartdelivery.service;

import com.smartdelivery.domain.Address;
import com.smartdelivery.domain.ExpressOrder;
import com.smartdelivery.domain.MenuItem;
import com.smartdelivery.domain.Order;
import com.smartdelivery.domain.OrderItem;
import com.smartdelivery.domain.OrderStatus;
import com.smartdelivery.domain.Restaurant;
import com.smartdelivery.observer.OrderObserver;
import com.smartdelivery.pricing.NormalPricing;
import com.smartdelivery.pricing.PeakHourPricing;
import com.smartdelivery.pricing.PricingStrategy;
import com.smartdelivery.pricing.PromotionalPricing;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Verifies US4.3 through {@link OrderService#calculateTotalWithPricing}.
 *
 * <p>The fixture order has a food subtotal of 38 (2 x 15 + 1 x 8) and a
 * delivery cost of 10 (5 km at 2 per km). The express order adds an express
 * fee of 5 to that delivery cost, so its delivery cost is 15.</p>
 */
class OrderServicePricingTest {

    private final Restaurant restaurant = new Restaurant(
            "Pizza House", true, 20, Set.of("Nablus"));

    private final Address address = new Address(
            "Nablus", "Taluza", "main street", "yellow house");

    private OrderService createService() {
        return new OrderService(
                mock(OrderObserver.class), new DeliveryCostCalculator(2));
    }

    private List<OrderItem> items() {
        return List.of(
                new OrderItem(new MenuItem("Pizza", 15, true), 2),
                new OrderItem(new MenuItem("Burger", 8, true), 1));
    }

    private Order placeStandardOrder(OrderService service) {
        return service.placeOrder(restaurant, items(), address, 5);
    }

    private ExpressOrder placeExpressOrder(OrderService service) {
        return service.placeExpressOrder(restaurant, items(), address, 5, 5);
    }

    // ---------- Pricing strategies on a standard order ----------

    @Test
    void normalPricingKeepsTheOrderTotalForStandardOrder() {
        OrderService service = createService();
        Order order = placeStandardOrder(service);

        double result = service.calculateTotalWithPricing(
                order, new NormalPricing());

        assertEquals(48.0, result, 0.001);
        assertEquals(order.getTotal(), result, 0.001);
    }

    @Test
    void peakHourPricingIncreasesOnlyTheFoodSubtotal() {
        OrderService service = createService();
        Order order = placeStandardOrder(service);

        double result = service.calculateTotalWithPricing(
                order, new PeakHourPricing(0.20));

        assertEquals(55.6, result, 0.001);
    }

    @Test
    void promotionalPricingDiscountsOnlyTheFoodSubtotal() {
        OrderService service = createService();
        Order order = placeStandardOrder(service);

        double result = service.calculateTotalWithPricing(
                order, new PromotionalPricing(0.10));

        assertEquals(44.2, result, 0.001);
    }

    // ---------- Strategy delegation ----------

    @Test
    void passesOnlyTheFoodSubtotalToTheStrategy() {
        OrderService service = createService();
        Order order = placeStandardOrder(service);
        PricingStrategy strategy = mock(PricingStrategy.class);
        when(strategy.calculatePrice(38.0)).thenReturn(100.0);

        double result = service.calculateTotalWithPricing(order, strategy);

        assertEquals(110.0, result, 0.001);
        verify(strategy).calculatePrice(38.0);
        verifyNoMoreInteractions(strategy);
    }

    // ---------- Express orders ----------

    @Test
    void normalPricingKeepsTheOrderTotalForExpressOrder() {
        OrderService service = createService();
        ExpressOrder order = placeExpressOrder(service);

        double result = service.calculateTotalWithPricing(
                order, new NormalPricing());

        assertEquals(53.0, result, 0.001);
        assertEquals(order.getTotal(), result, 0.001);
    }

    @Test
    void promotionalPricingDoesNotDiscountTheExpressFee() {
        OrderService service = createService();
        ExpressOrder order = placeExpressOrder(service);

        double result = service.calculateTotalWithPricing(
                order, new PromotionalPricing(0.10));

        assertEquals(49.2, result, 0.001);
    }

    // ---------- Read-only behavior ----------

    @Test
    void pricingDoesNotModifyTheOrderOrNotifyTheObserver() {
        OrderObserver observer = mock(OrderObserver.class);
        OrderService service = new OrderService(
                observer, new DeliveryCostCalculator(2));
        Order order = placeStandardOrder(service);

        service.calculateTotalWithPricing(order, new PromotionalPricing(0.10));

        assertEquals(48.0, order.getTotal(), 0.001);
        assertEquals(10.0, order.getDeliveryCost(), 0.001);
        assertEquals(38.0, order.getItemTotal(), 0.001);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertNull(order.getDriver());
        verifyNoInteractions(observer);
    }

    // ---------- Invalid input ----------

    @Test
    void rejectsNullOrderWhenPricing() {
        OrderService service = createService();

        assertThrows(IllegalArgumentException.class,
                () -> service.calculateTotalWithPricing(
                        null, new NormalPricing()));
    }

    @Test
    void rejectsNullPricingStrategy() {
        OrderService service = createService();
        Order order = placeStandardOrder(service);

        assertThrows(IllegalArgumentException.class,
                () -> service.calculateTotalWithPricing(order, null));
    }
}