package org.example.lfrs_group_4_oop.model;

import org.example.lfrs_group_4_oop.entity.Claim;
import org.example.lfrs_group_4_oop.entity.Claimant;
import org.example.lfrs_group_4_oop.entity.Item;
import org.example.lfrs_group_4_oop.exception.InvalidWorkflowException;
import org.example.lfrs_group_4_oop.exception.ValidationException;
import org.example.lfrs_group_4_oop.repository.ClaimRepository;
import org.example.lfrs_group_4_oop.repository.ClaimantRepository;
import org.example.lfrs_group_4_oop.repository.ItemRepository;
import org.example.lfrs_group_4_oop.validator.ClaimValidator;

/**
 * This model handles the coordination between claimants, items, and claim records.
 * It ensures that claims are processed as business operations, maintaining
 * consistency across multiple database entities.
 */
public class ClaimModel {

    private final ClaimRepository claimRepository;
    private final ClaimantRepository claimantRepository;
    private final ItemRepository itemRepository;

    /**
     * Constructs a new ClaimModel with its required repository dependencies.
     * 
     * @param claimRepository The repository for claim records.
     * @param claimantRepository The repository for claimant information.
     * @param itemRepository The repository for item status updates.
     */
    public ClaimModel(ClaimRepository claimRepository, 
                      ClaimantRepository claimantRepository, 
                      ItemRepository itemRepository) {
        this.claimRepository = claimRepository;
        this.claimantRepository = claimantRepository;
        this.itemRepository = itemRepository;
    }

    /**
     * Performs a pre-check to determine if a claim can be legally processed.
     * 
     * @param item The item being claimed.
     * @param claimant The person making the claim.
     * @throws ValidationException if claimant data is incomplete.
     * @throws InvalidWorkflowException if the item status is not 'Found'.
     */
    public void validateClaim(Item item, Claimant claimant) {
        ClaimValidator.validateItemAvailability(item);
        ClaimValidator.validateClaimant(claimant);
    }

    /**
     * Orchestrates the multi-step process of claiming a found item.
     * 
     * Business Workflow:
     *   Validation: Verify that the item is available and claimant data is valid.
     *   Claimant Registration: Persist the claimant's information to the database.
     *   Item Update: Transition the item's status from 'Found' to 'Claimed'.
     *   Claim Logging: Create a formal claim record linking the item and claimant.
     * 
     * @param item The item being claimed.
     * @param claimant The entity representing the claimant.
     * @param claim The claim record entity to be populated and saved.
     * @throws ValidationException if any part of the data validation fails.
     * @throws InvalidWorkflowException if the item is not in a claimable state.
     */
    public void processClaim(Item item, Claimant claimant, Claim claim) {

        validateClaim(item, claimant);

        claimantRepository.save(claimant);

        item.setStatus(ItemModel.STATUS_CLAIMED);
        itemRepository.updateStatus(item.getId(), ItemModel.STATUS_CLAIMED);

        claim.setItemId(item.getId());
        claim.setClaimantId(claimant.getId());
        if (claim.getClaimDate() == null) {
            claim.setClaimDate(java.time.LocalDateTime.now());
        }
        claimRepository.save(claim);
    }
}
