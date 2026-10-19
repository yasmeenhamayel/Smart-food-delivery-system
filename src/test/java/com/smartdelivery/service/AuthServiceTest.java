
package com.smartdelivery.service;

import com.smartdelivery.domain.Role;
import com.smartdelivery.domain.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    @Test
    void loginWithCorrectCredentialsReturnsUser() {
        User user = new User(
                "Test Customer",
                "customer1",
                "1234",
                Role.CUSTOMER
        );

        AuthService authService = new AuthService(List.of(user));

        User result = authService.login("customer1", "1234");

        assertNotNull(result);
        assertEquals("customer1", result.getUsername());
    }

    @Test
    void loginWithWrongPasswordReturnsNull() {
        User user = new User(
                "Test Customer",
                "customer1",
                "1234",
                Role.CUSTOMER
        );

        AuthService authService = new AuthService(List.of(user));

        User result = authService.login("customer1", "wrong");

        assertNull(result);
    }

    
@Test
void loginWithUnknownUsernameReturnsNull() {
    User user = new User(
            "Test Customer",
            "customer1",
            "1234",
            Role.CUSTOMER
    );

    AuthService authService = new AuthService(List.of(user));

    User result = authService.login("unknown", "1234");

    assertNull(result);
}

}
