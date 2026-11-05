package com.smartdelivery.service;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class DeliveryCostCalculatorTest {

		@Test
		void calculatesDeliveryCostCorrectly() {
			 DeliveryCostCalculator calculator = new DeliveryCostCalculator(2);

			    assertEquals(10, calculator.calculateDeliveryCost(5), 0.001);
		}
		
		@Test
		void returnsZeroForZeroCost() {
			 DeliveryCostCalculator calculator = new DeliveryCostCalculator(0);

			   assertEquals(0 ,calculator.calculateDeliveryCost(2), 0.001);
		}
		
		@Test
		void rejectsNegativeDistance() {
			 DeliveryCostCalculator calculator = new DeliveryCostCalculator(5);

			 assertThrows(IllegalArgumentException.class ,()-> calculator.calculateDeliveryCost(-6));
		}
		
		@Test
		void rejectsNegativeCostPerKm() {
			 assertThrows(IllegalArgumentException.class , ()-> new DeliveryCostCalculator(-6));
		}
		
		
		@Test
		void rejectsNonFiniteDistance() {
			 DeliveryCostCalculator calculator = new DeliveryCostCalculator(5);

			 assertThrows(IllegalArgumentException.class ,()-> calculator.calculateDeliveryCost(Double.NaN));
			 assertThrows(IllegalArgumentException.class ,()-> calculator.calculateDeliveryCost(Double.POSITIVE_INFINITY));
			 assertThrows(IllegalArgumentException.class ,()-> calculator.calculateDeliveryCost(Double.NEGATIVE_INFINITY));
		}
		
		@Test 
		void  rejectsNonFiniteCostPerKm() {
		    assertThrows(IllegalArgumentException.class,
		            () -> new DeliveryCostCalculator(Double.NaN));

		    assertThrows(IllegalArgumentException.class,
		            () -> new DeliveryCostCalculator(Double.POSITIVE_INFINITY));

		    assertThrows(IllegalArgumentException.class,
		            () -> new DeliveryCostCalculator(Double.NEGATIVE_INFINITY));
		}
}



