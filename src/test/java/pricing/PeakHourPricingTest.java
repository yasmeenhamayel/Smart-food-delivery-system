package pricing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PeakHourPricingTest {

	@Test
	void calculatesPriceWithPeakHourIncrease() {
		PeakHourPricing pricing = new PeakHourPricing(0.20);
		double result = pricing.calculatePrice(100);
		
		assertEquals(120 , result , 0.001);
	}
}
