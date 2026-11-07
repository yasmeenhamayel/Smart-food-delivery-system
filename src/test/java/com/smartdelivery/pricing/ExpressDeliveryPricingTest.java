package com.smartdelivery.pricing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class ExpressDeliveryPricingTest {

	@Test
    void calculatesPriceWithExpressFee() {
        ExpressDeliveryPricing pricing = new ExpressDeliveryPricing(15);

        double result = pricing.calculateDeliveryCost(110);

        assertEquals(125, result, 0.001);
    }


}
