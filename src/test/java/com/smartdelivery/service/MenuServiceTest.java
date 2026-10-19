
package com.smartdelivery.service;

import com.smartdelivery.domain.MenuItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MenuServiceTest {

    @Test
    void returnsOnlyAvailableItems() {
        MenuItem pizza = new MenuItem("Pizza", 10.0, true);
        MenuItem burger = new MenuItem("Burger", 8.5, false);
        MenuItem shawarma = new MenuItem("Shawarma", 6.0, true);

        MenuService service = new MenuService();

        List<MenuItem> result = service.getAvailableItems(
                List.of(pizza, burger, shawarma)
        );

        assertEquals(2, result.size());
        assertTrue(result.contains(pizza));
        assertTrue(result.contains(shawarma));
        assertFalse(result.contains(burger));
    }

    @Test
    void returnsCorrectPriceForAvailableItem() {
        MenuItem pizza = new MenuItem("Pizza", 10.0, true);

        MenuService service = new MenuService();

        List<MenuItem> result = service.getAvailableItems(List.of(pizza));

        assertEquals(10.0, result.get(0).getPrice(), 0.001);
    }
}
