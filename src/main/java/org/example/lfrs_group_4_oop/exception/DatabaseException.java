package org.example.lfrs_group_4_oop.exception;

/**
 * Custom exception for database-related errors.
 * Replaces generic RuntimeExceptions in DAO classes to provide better error context.
 */
public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
