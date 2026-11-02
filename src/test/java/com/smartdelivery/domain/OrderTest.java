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

	
	@Test
	void changesPendingOrderToConfirmed() {
	    MenuItem menu = new MenuItem("Pizza", 15, true);
	    OrderItem item = new OrderItem(menu, 3);
	    Order order = new Order(List.of(item));

	    order.confirm();

	    assertEquals(OrderStatus.CONFIRMED, order.getStatus());
	}
	
	
	@Test
	void rejectsConfirmingNonPendingOrder() {
	    MenuItem menu = new MenuItem("Pizza", 15, true);
	    OrderItem item = new OrderItem(menu, 3);
	    Order order = new Order(List.of(item));

	    order.confirm();

	    assertThrows(IllegalStateException.class, () -> order.confirm());
	}
	
	
	@Test
	void changesConfirmedOrderToPreparing() {
	    MenuItem menu = new MenuItem("Pizza", 15, true);
	    OrderItem item = new OrderItem(menu, 3);
	    Order order = new Order(List.of(item));

	    order.confirm();
	    order.startPreparing();

	    assertEquals(OrderStatus.PREPARING, order.getStatus());
	}
	
	
	@Test
	void rejectsStartingPreparationBeforeConfirmation() {
	    MenuItem menu = new MenuItem("Pizza", 15, true);
	    OrderItem item = new OrderItem(menu, 3);
	    Order order = new Order(List.of(item));

	    assertThrows(IllegalStateException.class, () -> order.startPreparing());
	}
	
	
	@Test
	void cancelsPendingOrder() {
	    MenuItem menu = new MenuItem("Pizza", 15, true);
	    OrderItem item = new OrderItem(menu, 3);
	    Order order = new Order(List.of(item));

	    order.cancel();

	    assertEquals(OrderStatus.CANCELLED, order.getStatus());
	}
	
	
	@Test
	void rejectsCancellingNonPendingOrder() {
	    MenuItem menu = new MenuItem("Pizza", 15, true);
	    OrderItem item = new OrderItem(menu, 3);
	    Order order = new Order(List.of(item));

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
            () -> new Order(items)
      );
   }

}
