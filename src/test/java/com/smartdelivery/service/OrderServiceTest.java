package com.smartdelivery.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Set;

import com.smartdelivery.domain.*;

public class OrderServiceTest {

	@Test
	void returnCorrectOrder() {
		Restaurant restaurant = new Restaurant("Pizza House" , true , 20 , Set.of("Nablus" , "Tubas" , "Tamoon"));
		MenuItem menu = new MenuItem("Pizza" , 15 , true);
		OrderItem item = new OrderItem(menu , 3);
		Address address = new Address("Nablus" , "Taluza" , "main street" , "in front of yellow house");
		OrderService service = new OrderService();
		
		Order result = service.placeOrder(restaurant, List.of(item), address);

		assertNotNull(result);
		assertEquals(OrderStatus.PENDING, result.getStatus());
		assertEquals(45.0, result.getTotal(), 0.001);	}
}
