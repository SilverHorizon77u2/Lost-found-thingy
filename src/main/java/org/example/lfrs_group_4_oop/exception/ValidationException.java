package org.example.lfrs_group_4_oop.exception;

/**
 * Thrown when user input fails validation rules.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
