package com.smartdelivery.pricing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class PromotionalPricingTest {
	 @Test
	    void calculatesPriceWithDiscount() {
	        PromotionalPricing pricing = new PromotionalPricing(0.20);

	        double result = pricing.calculatePrice(100);

	        assertEquals(80, result, 0.001);
	    }


}
