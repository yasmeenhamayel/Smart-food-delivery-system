package com.smartdelivery.service;

public class DeliveryCostCalculator {
	private double costPerKm;
	
	public DeliveryCostCalculator(double costPerKm) {
		if(costPerKm < 0 || !Double.isFinite(costPerKm)) {
			throw new IllegalArgumentException("Invalid cost per Km ");
		}
		this.costPerKm=costPerKm;
	}
	
	public double calculateDeliveryCost(double distance) {
		if(distance <=0 || !Double.isFinite(distance)) {
			throw new IllegalArgumentException("Invalid distance");
		}
		return (distance*costPerKm);
	}
}
