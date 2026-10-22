package com.smartdelivery.domain;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.ArrayList;


public class OrderTest {
	
	@Test
	void gettersReturnCorrectValues() {
		MenuItem item1 = new MenuItem("Faheta", 8 , true);
		OrderItem orderitem1 = new OrderItem(item1 , 5);

		MenuItem item2 = new MenuItem("Burger", 10 , true);
		OrderItem orderitem2 = new OrderItem(item2 ,2 );
		
        MenuItem item3 = new MenuItem("Pizza", 12.5, true );
        OrderItem orderitem3 = new OrderItem(item3 , 1);
        
        List<OrderItem>name = new ArrayList<>();
        name.add(orderitem1);
        name.add(orderitem2);
        name.add(orderitem3);
        
        Order order = new Order(name);
        
        assertEquals(OrderStatus.PENDING , order.getStatus());
	}
	
	
	@Test
	void TotalIsCalculatedCorrectly() {
		MenuItem item1 = new MenuItem("Faheta", 8 , true);
		OrderItem orderitem1 = new OrderItem(item1 , 5);

		MenuItem item2 = new MenuItem("Burger", 10 , true);
		OrderItem orderitem2 = new OrderItem(item2 ,2 );
		
        MenuItem item3 = new MenuItem("Pizza", 12.5, true);
        OrderItem orderitem3 = new OrderItem(item3 , 1);
        
        List<OrderItem>name = new ArrayList<>();
        name.add(orderitem1);
        name.add(orderitem2);
        name.add(orderitem3);
        
        Order order = new Order(name);
        
        assertEquals(72.5 , order.getTotal(), 0.001);
	}
	
	
	@Test
	void RejectsNullOrder(){
        List<OrderItem>name = null;

		assertThrows( IllegalArgumentException.class, ()->new Order(name));
	}
	
	
	@Test
	void RejectsEmptyOrder() {
		List<OrderItem>name = new ArrayList<>();
		
		assertThrows( IllegalArgumentException.class, ()->new Order(name));

	}
}
