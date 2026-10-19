
package com.smartdelivery.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void gettersReturnCorrectValues() {
        User user = new User(
                "Ahmad",
                "ahmad123",
                "pass123",
                Role.CUSTOMER
        );

        assertEquals("Ahmad", user.getName());
        assertEquals("ahmad123", user.getUsername());
        assertEquals(Role.CUSTOMER, user.getRole());
    }

    @Test
    void checkPasswordReturnsTrueForCorrectPassword() {
        User user = new User(
                "Ahmad",
                "ahmad123",
                "pass123",
                Role.CUSTOMER
        );

        assertTrue(user.checkPassword("pass123"));
    }

    @Test
    void checkPasswordReturnsFalseForWrongPassword() {
        User user = new User(
                "Ahmad",
                "ahmad123",
                "pass123",
                Role.CUSTOMER
        );

        assertFalse(user.checkPassword("wrong"));
    }
}
