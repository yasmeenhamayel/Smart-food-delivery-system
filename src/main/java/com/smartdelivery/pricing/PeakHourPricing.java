package com.smartdelivery.pricing;

public class PeakHourPricing implements PricingStrategy{
	
	private double increaseRate;
	
	
	public PeakHourPricing(double increaseRate) {
		if (increaseRate < 0 || !Double.isFinite(increaseRate)) {
		    throw new IllegalArgumentException("Invalid increase rate");
		}
		this.increaseRate = increaseRate;
	}
	
	
	@Override
	public double calculatePrice(double basePrice) {
		return basePrice*(1+increaseRate);
	}

	
}
