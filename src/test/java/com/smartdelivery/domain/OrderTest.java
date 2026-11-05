package com.smartdelivery.domain;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.ArrayList;


public class OrderTest {
	
	@Test
	void initializesOrderWithPendingStatus() {
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
        
        Order order = new Order(name , 0);
        
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
        
        Order order = new Order(name , 3);
        
        assertEquals(75.5 , order.getTotal(), 0.001);
	}
	
	
	@Test
	void RejectsNullOrder(){
        List<OrderItem>name = null;

		assertThrows( IllegalArgumentException.class, ()->new Order(name , 0));
	}
	
	
	@Test
	void RejectsEmptyOrder() {
		List<OrderItem>name = new ArrayList<>();
		
		assertThrows( IllegalArgumentException.class, ()->new Order(name , 0));

	}

	
	@Test
	void changesPendingOrderToConfirmed() {
	    MenuItem menu = new MenuItem("Pizza", 15, true);
	    OrderItem item = new OrderItem(menu, 3);
	    Order order = new Order(List.of(item) , 0);

	    order.confirm();

	    assertEquals(OrderStatus.CONFIRMED, order.getStatus());
	}
	
	
	@Test
	void rejectsConfirmingNonPendingOrder() {
	    MenuItem menu = new MenuItem("Pizza", 15, true);
	    OrderItem item = new OrderItem(menu, 3);
	    Order order = new Order(List.of(item) , 0);

	    order.confirm();

	    assertThrows(IllegalStateException.class, () -> order.confirm());
	}
	
	
	@Test
	void changesConfirmedOrderToPreparing() {
	    MenuItem menu = new MenuItem("Pizza", 15, true);
	    OrderItem item = new OrderItem(menu, 3);
	    Order order = new Order(List.of(item) , 0);

	    order.confirm();
	    order.startPreparing();

	    assertEquals(OrderStatus.PREPARING, order.getStatus());
	}
	
	
	@Test
	void rejectsStartingPreparationBeforeConfirmation() {
	    MenuItem menu = new MenuItem("Pizza", 15, true);
	    OrderItem item = new OrderItem(menu, 3);
	    Order order = new Order(List.of(item) , 0);

	    assertThrows(IllegalStateException.class, () -> order.startPreparing());
	}
	
	
	@Test
	void cancelsPendingOrder() {
	    MenuItem menu = new MenuItem("Pizza", 15, true);
	    OrderItem item = new OrderItem(menu, 3);
	    Order order = new Order(List.of(item) , 0);

	    order.cancel();

	    assertEquals(OrderStatus.CANCELLED, order.getStatus());
	}
	
	
	@Test
	void rejectsCancellingNonPendingOrder() {
	    MenuItem menu = new MenuItem("Pizza", 15, true);
	    OrderItem item = new OrderItem(menu, 3);
	    Order order = new Order(List.of(item) , 0);

	    order.confirm();

	    assertThrows(IllegalStateException.class, () -> order.cancel());

	    assertEquals(OrderStatus.CONFIRMED, order.getStatus());
	}
	


 	//YASMEEN
     @Test
     void rejectsOrderWithNullItem() {
     List<OrderItem> items = new ArrayList<>();
     items.add(null);

    assertThrows(
            IllegalArgumentException.class,
            () -> new Order(items ,0 )
      );
   }

	
	
	@Test
	void changesPreparingOrderToReady() {
		MenuItem menu = new MenuItem("Pizza", 15, true);
	    OrderItem item = new OrderItem(menu, 3);
	    Order order = new Order(List.of(item) ,0 );

	    order.confirm();
	    order.startPreparing();
	    order.markReady();

	    assertEquals(OrderStatus.READY, order.getStatus());
	}
	
	
	@Test
	void changesReadyOrderToOutForDelivery() {
		MenuItem menu = new MenuItem("Pizza", 15, true);
	    OrderItem item = new OrderItem(menu, 3);
	    Order order = new Order(List.of(item) , 0);

	    order.confirm();
	    order.startPreparing();
	    order.markReady();
	    order.markOutForDelivery();

	    assertEquals(OrderStatus.OUT_FOR_DELIVERY, order.getStatus());
	}
	
	@Test
	void changesOutForDeliveryOrderToDelivered() {
		MenuItem menu = new MenuItem("Pizza", 15, true);
	    OrderItem item = new OrderItem(menu, 3);
	    Order order = new Order(List.of(item) , 0);

	    order.confirm();
	    order.startPreparing();
	    order.markReady();
	    order.markOutForDelivery();
	    order.markDelivered();

	    assertEquals(OrderStatus.DELIVERED, order.getStatus());
	}
	
	@Test
	void rejectsMarkingOrderReadyBeforePreparing() {
		MenuItem menu = new MenuItem("Pizza", 15, true);
	    OrderItem item = new OrderItem(menu, 3);
	    Order order = new Order(List.of(item) , 0);

	    assertThrows(IllegalStateException.class, () -> order.markReady());
	}
	
	@Test
	void rejectsMarkingOrderDeliveredBeforeOutForDelivery() {
		MenuItem menu = new MenuItem("Pizza", 15, true);
	    OrderItem item = new OrderItem(menu, 3);
	    Order order = new Order(List.of(item) , 0);

	    order.confirm();
	    order.startPreparing();
	    order.markReady();
	    
	    assertThrows(IllegalStateException.class, () -> order.markDelivered());
	}
	
	
	@Test
	void rejectsChangingStatusAfterDelivered() {
		MenuItem menu = new MenuItem("Pizza", 15, true);
	    OrderItem item = new OrderItem(menu, 3);
	    Order order = new Order(List.of(item) , 0);

	    order.confirm();
	    order.startPreparing();
	    order.markReady();
	    order.markOutForDelivery();
	    order.markDelivered();
	    
	    assertThrows(IllegalStateException.class, () -> order.confirm());
	    assertThrows(IllegalStateException.class, () -> order.cancel());
	    assertThrows(IllegalStateException.class, () -> order.markOutForDelivery());
	    assertThrows(IllegalStateException.class, () -> order.markReady());

	    assertEquals(OrderStatus.DELIVERED, order.getStatus());
	}
	
	
	@Test
	void allowsZeroDeliveryCost() {
	    MenuItem menu = new MenuItem("Pizza", 15, true);
	    OrderItem item = new OrderItem(menu, 2);

	    Order order = new Order(List.of(item), 0);

	    assertEquals(30, order.getTotal(), 0.001);
	}
	
	
	@Test
	void rejectsNegativeDeliveryCost() {
	    MenuItem menu = new MenuItem("Pizza", 15, true);
	    OrderItem item = new OrderItem(menu, 2);

	    assertThrows(IllegalArgumentException.class,
	            () -> new Order(List.of(item), -5));
	}
	
	
	@Test
	void returnsCorrectDeliveryCost() {
	    MenuItem menu = new MenuItem("Pizza", 15, true);
	    OrderItem item = new OrderItem(menu, 3);

	    Order order = new Order(List.of(item), 10);

	    assertEquals(10.0, order.getDeliveryCost(), 0.001);
	}
	

}
