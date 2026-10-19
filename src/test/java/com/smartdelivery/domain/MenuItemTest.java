
package com.smartdelivery.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemTest {

    @Test
    void gettersReturnCorrectValues() {
        MenuItem item = new MenuItem("Pizza", 12.5, true);

        assertEquals("Pizza", item.getName());
        assertEquals(12.5, item.getPrice(), 0.001);
        assertTrue(item.isAvailable());
    }

    @Test
    void unavailableItemReturnsFalse() {
        MenuItem item = new MenuItem("Burger", 8.0, false);

        assertFalse(item.isAvailable());
    }
}
