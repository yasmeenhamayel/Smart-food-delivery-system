package com.smartdelivery.domain;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

	@Test
	void gettersReturnCorrectValues(){
		MenuItem item1 = new MenuItem("Faheta", 8 , true);
		OrderItem order = new OrderItem(item1 , 5);
		
		assertEquals(item1 , order.getMenuItem());
		assertEquals(5 , order.getQuantity());
	} 
	
	
	@Test
	void subtotalIsCalculatedCorrectly() {
		MenuItem item = new MenuItem("Burger", 10 , true);
		OrderItem order = new OrderItem(item , 3);
		
		assertEquals(30 , order.getSubtotal());
	}
	
	
	
	@Test
	void rejectsZeroQuantity() {
		MenuItem item = new MenuItem("Faheta", 8 , true);
		
		assertThrows( IllegalArgumentException.class, ()->new OrderItem(item , 0));
	}
	
	
	@Test
	void rejectsNegativeQuantity() {
		MenuItem item = new MenuItem("Faheta", 8 , true);
		
		assertThrows( IllegalArgumentException.class, ()->new OrderItem(item , -3));
	}
	
	
	@Test
	void rejectsNullMenuItem() {
		
		assertThrows( IllegalArgumentException.class, ()->new OrderItem(null , 1));
	}
	
	
	@Test
	void rejectsUnavailableMenuItem() {
	    MenuItem item = new MenuItem("Burger", 8.0, false);

	    assertThrows(IllegalArgumentException.class,
	            () -> new OrderItem(item, 1));
	}
	
	@Test
void calculatesTotalWeightCorrectly() {
    MenuItem item = new MenuItem("Pizza", 12.5, true, 0.8);
    OrderItem orderItem = new OrderItem(item, 3);

    assertEquals(2.4, orderItem.getTotalWeight(), 0.001);
}

@Test
void returnsZeroWeightForLegacyMenuItem() {
    MenuItem item = new MenuItem("Pizza", 12.5, true);
    OrderItem orderItem = new OrderItem(item, 2);

    assertEquals(0.0, orderItem.getTotalWeight(), 0.001);
}
}
