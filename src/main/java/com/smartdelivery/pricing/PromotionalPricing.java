package com.smartdelivery.pricing;

public class PromotionalPricing implements PricingStrategy {

	  private double discountRate;

	    public PromotionalPricing(double discountRate) {
	        if (discountRate < 0 || discountRate > 1 || !Double.isFinite(discountRate)) {
	            throw new IllegalArgumentException("Invalid discount rate");
	        }
	        this.discountRate = discountRate;
	    }

	    @Override
	    public double calculatePrice(double basePrice) {
	        return basePrice * (1 - discountRate);
	    }
}
