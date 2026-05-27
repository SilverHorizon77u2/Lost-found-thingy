package org.example.lfrs_group_4_oop.exception;

/**
 * Thrown when a requested resource (User, Item, etc.) is not found.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
