package com.smartdelivery.pricing;

public class ExpressDeliveryPricing implements PricingStrategy {
	private double expressFee;
	
	 public ExpressDeliveryPricing(double expressFee) {
	        if (expressFee < 0 || !Double.isFinite(expressFee)) {
	            throw new IllegalArgumentException("Invalid express fee");
	        }
	        this.expressFee = expressFee;
	    }
	 
	 @Override
	    public double calculatePrice(double basePrice) {
	        return basePrice + expressFee;
	    }
	 
	 

}
