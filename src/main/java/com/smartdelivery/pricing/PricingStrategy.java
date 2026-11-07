package com.smartdelivery.pricing;

/**
 * Strategy for pricing the food subtotal of an order.
 *
 * <p>Implementations (normal, peak-hour and promotional pricing) are applied
 * to the food subtotal only. The delivery cost is added separately and is not
 * changed by these strategies. Delivery-cost strategies, such as the Express
 * surcharge, use {@link DeliveryCostStrategy} instead.</p>
 */



/**
 * Strategy for calculating the food subtotal before delivery cost is added.
 */
public interface PricingStrategy {

	double calculatePrice(double basePrice);
}
