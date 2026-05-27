package org.example.lfrs_group_4_oop.validator;

import org.example.lfrs_group_4_oop.entity.Claimant;
import org.example.lfrs_group_4_oop.entity.Item;
import org.example.lfrs_group_4_oop.exception.InvalidWorkflowException;
import org.example.lfrs_group_4_oop.exception.ValidationException;

/**
 * Validates Claim processing data and rules.
 */
public class ClaimValidator {

    private ClaimValidator() {
        // Utility class
    }

    public static void validateClaimant(Claimant claimant) {
        if (claimant == null) {
            throw new ValidationException("Claimant information is required.");
        }
        if (claimant.getName() == null || claimant.getName().trim().isEmpty()) {
            throw new ValidationException("Claimant name is required.");
        }
    }

    public static void validateItemAvailability(Item item) {
        if (item == null) {
            throw new ValidationException("Item information is required.");
        }
        if (!"Found".equalsIgnoreCase(item.getStatus())) {
            throw new InvalidWorkflowException("Only items with 'Found' status can be claimed.");
        }
    }
}
