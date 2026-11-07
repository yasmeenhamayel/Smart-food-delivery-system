
package com.smartdelivery.service;

import com.smartdelivery.domain.User;
import java.util.List;

/**
 * Handles user authentication for the food delivery system.
 */
public class AuthService {

    /** The error message reported when the credentials are invalid. */
    public static final String INVALID_CREDENTIALS_MESSAGE =
            "Invalid username or password";

    /** The users available for authentication. */
    private final List<User> users;

    /**
     * Creates an authentication service.
     *
     * @param users the users whose credentials will be checked
     */
    public AuthService(List<User> users) {
        this.users = users;
    }

    /**
     * Authenticates a user using their username and password.
     *
     * @param username the username entered by the user
     * @param password the password entered by the user
     * @return the authenticated user, or null if the credentials are invalid
     */
    public User login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username)
                    && user.checkPassword(password)) {
                return user;
            }
        }

        return null;
    }

    /**
     * Authenticates a user and reports an error message when the
     * credentials are invalid.
     *
     * @param username the username entered by the user
     * @param password the password entered by the user
     * @return the authenticated user
     * @throws IllegalArgumentException with {@link #INVALID_CREDENTIALS_MESSAGE}
     *         if the username or password is wrong
     */
    public User authenticate(String username, String password) {
        User user = login(username, password);

        if (user == null) {
            throw new IllegalArgumentException(INVALID_CREDENTIALS_MESSAGE);
        }

        return user;
    }
}