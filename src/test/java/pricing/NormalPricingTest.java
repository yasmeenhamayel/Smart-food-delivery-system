package pricing;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NormalPricingTest {
	
	@Test
	void returnsSameBasePrice() {
		NormalPricing pricing = new NormalPricing();
		
		double result = pricing.calculatePrice(100);
		assertEquals(100 , result);
	}

}
