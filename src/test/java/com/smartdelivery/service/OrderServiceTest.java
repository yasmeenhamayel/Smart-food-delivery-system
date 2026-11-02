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
		assertEquals(45.0, result.getTotal(), 0.001);	
    }
    
    
@Test 
void rejectsUncoveredDilveryArea() {
	Restaurant restaurant = new Restaurant("Pizza House" , true , 20 , Set.of("Tubas" , "Tamoon"));
	MenuItem menu = new MenuItem("Pizza" , 15 , true);
	OrderItem item = new OrderItem(menu , 3);
	Address address = new Address("Nablus" , "Taluza" , "main street" , "in front of yellow house");
	OrderService service = new OrderService();
	
	assertThrows( IllegalArgumentException.class, ()->service.placeOrder(restaurant, List.of(item), address));
}

@Test
void rejectsOrderBelowMinimumValue() {
	Restaurant restaurant = new Restaurant("Pizza House" , true , 20 , Set.of("Nablus" , "Tubas" , "Tamoon"));
	MenuItem menu = new MenuItem("Pizza" , 15 , true);
	OrderItem item = new OrderItem(menu , 1);
	Address address = new Address("Nablus" , "Taluza" , "main street" , "in front of yellow house");
	OrderService service = new OrderService();
	
	assertThrows( IllegalArgumentException.class, ()->service.placeOrder(restaurant, List.of(item), address));
}


@Test
void cancelsPendingOrder() {
	MenuItem menu = new MenuItem("Pizza" , 15 , true);
	OrderItem item = new OrderItem(menu , 3);
	Order order=new Order(List.of(item));
	OrderService service = new OrderService();
	
	service.cancelOrder(order);
	assertEquals(OrderStatus.CANCELLED ,order.getStatus() );	
}

@Test
void rejectsCancellingOrderInPreparingStatus() {

    MenuItem menu = new MenuItem("Pizza", 15, true);
    OrderItem item = new OrderItem(menu, 3);
    Order order = new Order(List.of(item));
    OrderService service = new OrderService();

    order.confirm();
    order.startPreparing();

    assertThrows(IllegalStateException.class,() -> service.cancelOrder(order));

    assertEquals(OrderStatus.PREPARING, order.getStatus());
}

@Test
void rejectsCancellingNullOrder() {
    OrderService service = new OrderService();

    assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(null));
}

@Test
void rejectsCancellingAlreadyCancelledOrder() {
    MenuItem menu = new MenuItem("Pizza", 15, true);
    OrderItem item = new OrderItem(menu, 3);
    Order order = new Order(List.of(item));
    OrderService service = new OrderService();

    service.cancelOrder(order);

    assertThrows(IllegalStateException.class, () -> service.cancelOrder(order));
}



}



