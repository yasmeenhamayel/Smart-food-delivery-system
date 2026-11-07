package com.smartdelivery.pricing;

public class ExpressDeliveryPricing implements DeliveryCostStrategy {
	private double expressFee;
	
	 public ExpressDeliveryPricing(double expressFee) {
	        if (expressFee < 0 || !Double.isFinite(expressFee)) {
	            throw new IllegalArgumentException("Invalid express fee");
	        }
	        this.expressFee = expressFee;
	    }
	 
	 @Override
	    public double calculateDeliveryCost(double deliveryCost) {
	        return deliveryCost + expressFee;
	    }
	 
	 

}
