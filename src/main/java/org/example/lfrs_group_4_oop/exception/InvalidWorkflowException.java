package org.example.lfrs_group_4_oop.exception;

/**
 * Thrown when a business workflow rule is violated.
 */
public class InvalidWorkflowException extends RuntimeException {
    public InvalidWorkflowException(String message) {
        super(message);
    }
}
