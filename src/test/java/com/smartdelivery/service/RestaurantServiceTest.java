
package com.smartdelivery.service;

import com.smartdelivery.domain.Restaurant;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantServiceTest {

    @Test
    void returnsOnlyOpenRestaurants() {
        Restaurant openRestaurant1 =
                new Restaurant("Pizza House", true);

        Restaurant closedRestaurant =
                new Restaurant("Burger House", false);

        Restaurant openRestaurant2 =
                new Restaurant("Sushi House", true);

        RestaurantService service = new RestaurantService();

        List<Restaurant> result = service.getOpenRestaurants(
                List.of(
                        openRestaurant1,
                        closedRestaurant,
                        openRestaurant2
                )
        );

        assertEquals(2, result.size());
        assertTrue(result.contains(openRestaurant1));
        assertTrue(result.contains(openRestaurant2));
        assertFalse(result.contains(closedRestaurant));
    }

    @Test
    void returnsEmptyListWhenAllRestaurantsAreClosed() {
        Restaurant restaurant1 = new Restaurant("Pizza House", false);
        Restaurant restaurant2 = new Restaurant("Burger House", false);

        RestaurantService service = new RestaurantService();

        List<Restaurant> result = service.getOpenRestaurants(
                List.of(restaurant1, restaurant2)
        );

        assertTrue(result.isEmpty());
    }
}
