
package com.smartdelivery.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class RestaurantTest {

    @Test
    void gettersReturnCorrectValues() {
    	Set<String> areas = new HashSet<>(Set.of("Nablus" , "Tubas" , "Tamoon"));
    	double min = 10;
        Restaurant restaurant = new Restaurant("Pizza House", true , min , areas);

        assertEquals("Pizza House", restaurant.getName());
        assertEquals(10 , restaurant.getMinOrderValue());
        assertEquals(Set.of("Nablus" , "Tubas" , "Tamoon" ) , restaurant.getDeliveryAreas());
        assertTrue(restaurant.isOpen());
    }
    
    @Test
    void acceptZeroasMinOrderValue() {
        Restaurant restaurant = new Restaurant("Pizza House", true , 0 , Set.of("Nablus"));
        
        assertEquals(0, restaurant.getMinOrderValue());

    }
    
    @Test
    void rejectsNullName() {
      	 assertThrows(IllegalArgumentException.class, () -> new Restaurant(null, true, 20 , Set.of("Rafidia")));
    }
    
    @Test
    void rejectsBlankName() {
     	 assertThrows(IllegalArgumentException.class, () -> new Restaurant("   ", true, 20 , Set.of("Rafidia")));
   }
    
    
   @Test
   void rejectsNulldeliveryAreas() {
   	 assertThrows(IllegalArgumentException.class, () -> new Restaurant("Pizza House", true, 20 , null ));
   }
   
   
   @Test
   void rejectsEmptydeliveryAreas() {
	   Set<String> deliveryAreas =new HashSet<>();
   	 assertThrows(IllegalArgumentException.class, () -> new Restaurant("Pizza House", true, 20 , deliveryAreas ));
   }
   
   @Test
   void rejectsNullAreaName() {
	   Set<String> areas = new HashSet<>();
	   areas.add("Rafedia");
	   areas.add(null);
   	 assertThrows(IllegalArgumentException.class, () -> new Restaurant("Pizza House", true, 20 , areas));
   }
   
   @Test
   void rejectsBlankAreaName() {
	 assertThrows(IllegalArgumentException.class, () -> new Restaurant("Pizza House", true, 20 , Set.of("Rafedia" , " ")));
	   }
    
    
    @Test
    void rejectsNegativeMinOrderValue() {
    	Set<String> areas = new HashSet<>(Set.of("Nablus" , "Tubas" , "Tamoon"));
    	double min = -10;
    	
    	assertThrows(IllegalArgumentException.class, ()->new Restaurant("Pizza House", true , min , areas));
    }
    
    
    @Test
    void rejectsNaNminOrderValue() {
    	Set<String> areas = new HashSet<>(Set.of("Nablus" , "Tubas" , "Tamoon"));
    	
    	assertThrows(IllegalArgumentException.class, ()->new Restaurant("Pizza House", true , Double.NaN , areas));
    }
    
    
    @Test
    void rejectsPositiveInfintyMin() {
    	 assertThrows(IllegalArgumentException.class, () -> new Restaurant("Cafe", true, Double.POSITIVE_INFINITY, Set.of("Rafidia")));
    }
    
    @Test
    void rejectsNegativeInfintyMin() {
   	 assertThrows(IllegalArgumentException.class, () -> new Restaurant("Cafe", true, Double.NEGATIVE_INFINITY, Set.of("Rafidia")));
    }
    
    @Test
    void stipsNameandAreaName() {
    	Set<String> areas = new HashSet<>(Set.of("  Nablus "));
    	double min = 10;
        Restaurant restaurant = new Restaurant(" Pizza House ", true , min , areas);

        assertEquals("Pizza House", restaurant.getName());
        assertEquals(Set.of("Nablus") , restaurant.getDeliveryAreas());
    }

    @Test
    void closedRestaurantReturnsFalse() {
    	Set<String> areas = new HashSet<>(Set.of("Nablus" , "Tubas" , "Tamoon"));
    	double min = 10;
     
        Restaurant restaurant = new Restaurant("Burger House", false , min , areas);

        assertFalse(restaurant.isOpen());
    }
    
    
    @Test
    void rejectsModificationOfDeliveryAreasThroughGetter() {
        Restaurant restaurant = new Restaurant( "Pizza House", true,10, Set.of("Nablus", "Tubas"));

        assertThrows(UnsupportedOperationException.class, () -> restaurant.getDeliveryAreas().add("Rafidia")
        );
    }
}
