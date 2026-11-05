package com.smartdelivery.service;

import org.junit.jupiter.api.Test; 
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Set;
import pricing.*;

import com.smartdelivery.domain.*;

public class OrderServiceTest {

	@Test
	void returnCorrectOrder() {
		Restaurant restaurant = new Restaurant("Pizza House" , true , 20 , Set.of("Nablus" , "Tubas" , "Tamoon"));
		MenuItem menu = new MenuItem("Pizza" , 15 , true);
		OrderItem item = new OrderItem(menu , 3);
		Address address = new Address("Nablus" , "Taluza" , "main street" , "in front of yellow house");
		OrderService service = new OrderService(new DeliveryCostCalculator(2));
		
		Order result = service.placeOrder(restaurant, List.of(item), address , 5);

		assertNotNull(result);
		assertEquals(OrderStatus.PENDING, result.getStatus());
		assertEquals(55.0, result.getTotal(), 0.001);	
    }
    
    
@Test 
void rejectsUncoveredDeliveryArea() {
	Restaurant restaurant = new Restaurant("Pizza House" , true , 20 , Set.of("Tubas" , "Tamoon"));
	MenuItem menu = new MenuItem("Pizza" , 15 , true);
	OrderItem item = new OrderItem(menu , 3);
	Address address = new Address("Nablus" , "Taluza" , "main street" , "in front of yellow house");
	OrderService service = new OrderService(new DeliveryCostCalculator(2));
	
	assertThrows( IllegalArgumentException.class, ()->service.placeOrder(restaurant, List.of(item), address , 5));
}

@Test
void rejectsOrderBelowMinimumValue() {
	Restaurant restaurant = new Restaurant("Pizza House" , true , 20 , Set.of("Nablus" , "Tubas" , "Tamoon"));
	MenuItem menu = new MenuItem("Pizza" , 15 , true);
	OrderItem item = new OrderItem(menu , 1);
	Address address = new Address("Nablus" , "Taluza" , "main street" , "in front of yellow house");
	OrderService service = new OrderService(new DeliveryCostCalculator(2));
	
	assertThrows( IllegalArgumentException.class, ()->service.placeOrder(restaurant, List.of(item), address , 5));
}


@Test
void cancelsPendingOrder() {
	MenuItem menu = new MenuItem("Pizza" , 15 , true);
	OrderItem item = new OrderItem(menu , 3);
	Order order=new Order(List.of(item) , 0);
	OrderService service = new OrderService(new DeliveryCostCalculator(2));
	
	service.cancelOrder(order);
	assertEquals(OrderStatus.CANCELLED ,order.getStatus() );	
}

@Test
void rejectsCancellingOrderInPreparingStatus() {

    MenuItem menu = new MenuItem("Pizza", 15, true);
    OrderItem item = new OrderItem(menu, 3);
    Order order = new Order(List.of(item) , 0);
    OrderService service = new OrderService(new DeliveryCostCalculator(2));

    order.confirm();
    order.startPreparing();

    assertThrows(IllegalStateException.class,() -> service.cancelOrder(order));

    assertEquals(OrderStatus.PREPARING, order.getStatus());
}

@Test
void rejectsCancellingNullOrder() {
    OrderService service = new OrderService(new DeliveryCostCalculator(2));

    assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(null));
}

@Test
void rejectsCancellingAlreadyCancelledOrder() {
    MenuItem menu = new MenuItem("Pizza", 15, true);
    OrderItem item = new OrderItem(menu, 3);
    Order order = new Order(List.of(item) , 0 );
    OrderService service = new OrderService(new DeliveryCostCalculator(2));

    service.cancelOrder(order);

    assertThrows(IllegalStateException.class, () -> service.cancelOrder(order));
}


@Test
void calculatesTotalWithFreeDelivery() {
    Restaurant restaurant = new Restaurant(
        "Pizza House", true, 20, Set.of("Nablus")
    );

    MenuItem menu = new MenuItem("Pizza", 15, true);
    OrderItem item = new OrderItem(menu, 2);

    Address address = new Address(
        "Nablus", "Taluza", "main street", "Yellow house"
    );

    OrderService service = new OrderService(new DeliveryCostCalculator(0));

    Order result = service.placeOrder(restaurant, List.of(item), address, 5);

    assertEquals(30.0, result.getTotal(), 0.001);
}


@Test
void rejectsNullCalculator() {
    assertThrows(IllegalArgumentException.class,
            () -> new OrderService(null));
}

@Test
void returnsCorrectDeliveryCost() {
    Restaurant restaurant = new Restaurant(
            "Pizza House", true, 20, Set.of("Nablus")
    );

    MenuItem menu = new MenuItem("Pizza", 15, true);
    OrderItem item = new OrderItem(menu, 3);

    Address address = new Address(
            "Nablus", "Taluza", "main street", "Yellow house"
    );

    OrderService service =
            new OrderService(new DeliveryCostCalculator(2));

    Order result = service.placeOrder(
            restaurant, List.of(item), address, 5
    );

    assertEquals(10.0, result.getDeliveryCost(), 0.001);
    assertEquals(55.0, result.getTotal(), 0.001);
}


@Test
void appliesPeakHourPricingCorrectly() {

    Restaurant restaurant = new Restaurant("Pizza House", true, 20, Set.of("Nablus"));

    MenuItem pizza = new MenuItem("Pizza", 15, true);

    OrderItem item = new OrderItem(pizza, 3);

    OrderService service = new OrderService(new DeliveryCostCalculator(2));

    Order order = service.placeOrder(restaurant,List.of(item),new Address("Nablus", "Taluza", "main street", "near school"),5);

    PricingStrategy strategy = new PeakHourPricing(0.20);

    double result = service.calculateFinalPrice(order, strategy);

    assertEquals(66, result, 0.001);
}


@Test
void appliesExpressPricingCorrectly() {

    Restaurant restaurant = new Restaurant("Pizza House", true, 20, Set.of("Nablus"));

    MenuItem pizza = new MenuItem("Pizza", 15, true);
    OrderItem item = new OrderItem(pizza, 3);

    OrderService service = new OrderService(new DeliveryCostCalculator(2));

    Order order = service.placeOrder(restaurant,List.of(item),new Address("Nablus", "Taluza", "main street", "near school"),5);

    PricingStrategy strategy = new ExpressDeliveryPricing(15);

    double result = service.calculateFinalPrice(order, strategy);

    assertEquals(70, result, 0.001);
}

@Test
void appliesPromotionalPricingCorrectly() {

    Restaurant restaurant = new Restaurant("Pizza House", true, 20, Set.of("Nablus"));

    MenuItem pizza = new MenuItem("Pizza", 15, true);
    OrderItem item = new OrderItem(pizza, 3);

    OrderService service = new OrderService(new DeliveryCostCalculator(2));

    Order order = service.placeOrder(restaurant,List.of(item),new Address("Nablus", "Taluza", "main street", "near school"),5);

    PricingStrategy strategy = new PromotionalPricing(0.20);

    double result = service.calculateFinalPrice(order, strategy);

    assertEquals(44, result, 0.001);
}


@Test
void appliesNormalPricingCorrectly() {

    Restaurant restaurant = new Restaurant("Pizza House", true, 20, Set.of("Nablus"));

    MenuItem pizza = new MenuItem("Pizza", 15, true);
    OrderItem item = new OrderItem(pizza, 3);

    OrderService service = new OrderService(new DeliveryCostCalculator(2) );

    Order order = service.placeOrder(restaurant,List.of(item),new Address("Nablus", "Taluza", "main street", "near school"),5);

    PricingStrategy strategy = new NormalPricing();

    double result = service.calculateFinalPrice(order, strategy);

    assertEquals(55, result, 0.001);
}



}



