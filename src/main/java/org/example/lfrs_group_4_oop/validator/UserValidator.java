package org.example.lfrs_group_4_oop.validator;

import org.example.lfrs_group_4_oop.exception.ValidationException;
import java.util.regex.Pattern;

/**
 * Validates User input data.
 */
public class UserValidator {

    private UserValidator() {
        // Utility class
    }

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email cannot be empty.");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Invalid email format.");
        }
    }

    public static void validateRole(String role) {
        if (!"Administrator".equals(role) && !"Standard User".equals(role)) {
            throw new ValidationException("Invalid user role selected.");
        }
    }

    /**
     * Validates that the new password meets security requirements.
     * Enforces a minimum length of 8 characters.
     */
    public static void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long.");
        }
    }

    /**
     * Ensures that the confirmation password matches the new password.
     */
    public static void validatePasswordMatch(String newPass, String confirmPass) {
        if (newPass == null || !newPass.equals(confirmPass)) {
            throw new ValidationException("New passwords do not match.");
        }
    }
}
