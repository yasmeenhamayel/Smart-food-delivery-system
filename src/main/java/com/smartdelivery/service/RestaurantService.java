
package com.smartdelivery.service;

import com.smartdelivery.domain.Restaurant;
import java.util.ArrayList;
import java.util.List;

public class RestaurantService {

    public List<Restaurant> getOpenRestaurants(List<Restaurant> restaurants) {
        List<Restaurant> openRestaurants = new ArrayList<>();

        for (Restaurant restaurant : restaurants) {
            if (restaurant.isOpen()) {
                openRestaurants.add(restaurant);
            }
        }

        return openRestaurants;
    }
}
