
package com.smartdelivery.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {

    @Test
    void gettersReturnCorrectValues() {
        Restaurant restaurant = new Restaurant("Pizza House", true);

        assertEquals("Pizza House", restaurant.getName());
        assertTrue(restaurant.isOpen());
    }

    @Test
    void closedRestaurantReturnsFalse() {
        Restaurant restaurant = new Restaurant("Burger House", false);

        assertFalse(restaurant.isOpen());
    }
}
