
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

    @Test
void createsMenuItemWithWeight() {
    MenuItem item = new MenuItem("Pizza", 12.5, true, 0.8);

    assertEquals(0.8, item.getWeight(), 0.001);
}

@Test
void oldConstructorUsesZeroWeight() {
    MenuItem item = new MenuItem("Pizza", 12.5, true);

    assertEquals(0.0, item.getWeight(), 0.001);
}

@Test
void rejectsZeroWeightInNewConstructor() {
    assertThrows(IllegalArgumentException.class,
            () -> new MenuItem("Pizza", 12.5, true, 0));
}

@Test
void rejectsNegativeWeight() {
    assertThrows(IllegalArgumentException.class,
            () -> new MenuItem("Pizza", 12.5, true, -0.5));
}

@Test
void rejectsInfiniteWeight() {
    assertThrows(IllegalArgumentException.class,
            () -> new MenuItem("Pizza", 12.5, true, Double.POSITIVE_INFINITY));
}
}
