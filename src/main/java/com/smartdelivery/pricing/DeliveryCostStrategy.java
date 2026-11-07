package com.smartdelivery.pricing;

/**
 * Strategy for calculating the delivery cost of an order.
 *
 * <p>This interface is deliberately separate from {@link PricingStrategy},
 * which prices the food subtotal. Keeping the two apart means a delivery-cost
 * strategy, such as the Express surcharge, cannot be passed where a
 * food-subtotal strategy is expected, so the surcharge cannot be applied
 * twice by mistake.</p>
 */
public interface DeliveryCostStrategy {

	/**
	 * Calculates the delivery cost.
	 *
	 * @param deliveryCost the base delivery cost
	 * @return the delivery cost after this strategy has been applied
	 */
	double calculateDeliveryCost(double deliveryCost);
}