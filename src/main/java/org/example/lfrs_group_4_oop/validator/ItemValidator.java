package org.example.lfrs_group_4_oop.validator;

import org.example.lfrs_group_4_oop.exception.ValidationException;
import java.time.LocalDateTime;

/**
 * Validates Item input data.
 */
public class ItemValidator {

    private ItemValidator() {
        // Utility class
    }

    public static void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Item title cannot be empty.");
        }
        if (title.length() > 100) {
            throw new ValidationException("Item title cannot exceed 100 characters.");
        }
    }

    public static void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Item description cannot be empty.");
        }
    }

    public static void validateReportDate(LocalDateTime date) {
        if (date == null) {
            throw new ValidationException("Report date is required.");
        }
        if (date.isAfter(LocalDateTime.now())) {
            throw new ValidationException("Report date cannot be in the future.");
        }
    }
}
