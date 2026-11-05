package pricing;

public class NormalPricing implements PricingStrategy{

	@Override
    public double calculatePrice(double basePrice) {
        return basePrice;
    }
	
	
}
