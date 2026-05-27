package org.example.lfrs_group_4_oop.model;

import org.example.lfrs_group_4_oop.entity.Item;
import org.example.lfrs_group_4_oop.exception.InvalidWorkflowException;
import org.example.lfrs_group_4_oop.exception.ValidationException;
import org.example.lfrs_group_4_oop.repository.CategoryRepository;
import org.example.lfrs_group_4_oop.repository.ItemRepository;
import org.example.lfrs_group_4_oop.validator.ItemValidator;
import java.time.LocalDateTime;

/**
 * Service-layer model for managing the lifecycle and business rules of Items.
 */
public class ItemModel {

    public static final String STATUS_FOUND = "Found";
    public static final String STATUS_LOST = "Lost";
    public static final String STATUS_CLAIMED = "Claimed";

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    /**
     * @param itemRepository The repository for item data.
     * @param categoryRepository The repository for category verification.
     */
    public ItemModel(ItemRepository itemRepository, CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Validates that an item's description meets requirements.
     * 
     * @param description The description text to validate.
     * @throws ValidationException if the description is null or empty.
     */
    public void validateDescription(String description) {
        ItemValidator.validateDescription(description);
    }

    /**
     * Validates that a reporting date is realistic (not in the future).
     * 
     * @param date The date to validate.
     * @throws ValidationException if the date is null or in the future.
     */
    public void validateReportDate(LocalDateTime date) {
        ItemValidator.validateReportDate(date);
    }

    /**
     * Determines if an item's current state allows it to be marked as "Claimed".
     * 
     * @param currentStatus The current status of the item.
     * @return {@code true} if transition is allowed; {@code false} otherwise.
     */
    public boolean canTransitionToClaimed(String currentStatus) {
        return STATUS_FOUND.equalsIgnoreCase(currentStatus);
    }

    /**
     * Updates an item's status while enforcing workflow rules.
     * 
     * Transitions to 'Claimed' are strictly blocked unless the item
     * is currently 'Found'.
     * 
     * @param item The item entity to update.
     * @param newStatus The target status.
     * @throws InvalidWorkflowException if the transition violates business rules.
     */
    public void transitionStatus(Item item, String newStatus) {
        if (item == null || newStatus == null) return;

        // Enforce the Found -> Claimed transition rule
        if (STATUS_CLAIMED.equalsIgnoreCase(newStatus) && !canTransitionToClaimed(item.getStatus())) {
            throw new InvalidWorkflowException("Cannot mark item as Claimed unless its status is Found.");
        }

        // Apply change locally and persist to database
        item.setStatus(newStatus);
        itemRepository.updateStatus(item.getId(), newStatus);
    }

    /**
     * Registers a new lost or found item in the system.
     * 
     * Business Rules:
     *  Description must not be empty.
     *  Report date must not be in the future.
     *  Referential Integrity:</b> The item must be linked to an existing category.
     * 
     * @param item The item entity to save.
     * @throws ValidationException if validation or category verification fails.
     */
    public void registerItem(Item item) {
        ItemValidator.validateTitle(item.getTitle());
        validateDescription(item.getDescription());
        validateReportDate(item.getDateReported());

        // Ensure category exists before saving
        if (item.getCategoryId() == null || categoryRepository.findById(item.getCategoryId()) == null) {
            throw new ValidationException("Item must have a valid category.");
        }

        itemRepository.save(item);
    }
}
