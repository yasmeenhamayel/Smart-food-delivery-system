
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

    @Test
    void managerLoginWithCorrectCredentialsReturnsManager() {
        User manager = new User(
                "Test Manager",
                "manager1",
                "admin",
                Role.MANAGER
        );

        AuthService authService = new AuthService(List.of(manager));

        User result = authService.login("manager1", "admin");

        assertNotNull(result);
        assertEquals(Role.MANAGER, result.getRole());
    }

    @Test
    void authenticateWithCorrectCustomerCredentialsReturnsUser() {
        User customer = new User(
                "Test Customer", "customer1", "1234", Role.CUSTOMER);
        User manager = new User(
                "Test Manager", "manager1", "admin", Role.MANAGER);

        AuthService authService = new AuthService(List.of(customer, manager));

        User result = authService.authenticate("customer1", "1234");

        assertSame(customer, result);
    }

    @Test
    void authenticateWithCorrectManagerCredentialsReturnsUser() {
        User customer = new User(
                "Test Customer", "customer1", "1234", Role.CUSTOMER);
        User manager = new User(
                "Test Manager", "manager1", "admin", Role.MANAGER);

        AuthService authService = new AuthService(List.of(customer, manager));

        User result = authService.authenticate("manager1", "admin");

        assertSame(manager, result);
        assertEquals(Role.MANAGER, result.getRole());
    }

    @Test
    void authenticateWithWrongPasswordReportsErrorMessage() {
        User user = new User(
                "Test Customer", "customer1", "1234", Role.CUSTOMER);

        AuthService authService = new AuthService(List.of(user));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.authenticate("customer1", "wrong"));

        assertEquals(AuthService.INVALID_CREDENTIALS_MESSAGE,
                exception.getMessage());
    }

    @Test
    void authenticateWithUnknownUsernameReportsSameErrorMessage() {
        User user = new User(
                "Test Customer", "customer1", "1234", Role.CUSTOMER);

        AuthService authService = new AuthService(List.of(user));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.authenticate("unknown", "1234"));

        assertEquals(AuthService.INVALID_CREDENTIALS_MESSAGE,
                exception.getMessage());
    }

    @Test
    void authenticateWithNullCredentialsReportsErrorMessage() {
        User user = new User(
                "Test Customer", "customer1", "1234", Role.CUSTOMER);

        AuthService authService = new AuthService(List.of(user));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.authenticate(null, null));

        assertEquals(AuthService.INVALID_CREDENTIALS_MESSAGE,
                exception.getMessage());
    }

    @Test
    void authenticateWithEmptyUserListReportsErrorMessage() {
        AuthService authService = new AuthService(List.of());

        assertThrows(IllegalArgumentException.class,
                () -> authService.authenticate("customer1", "1234"));
    }

}