package org.example.lfrs_group_4_oop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.lfrs_group_4_oop.entity.Claim;
import org.example.lfrs_group_4_oop.entity.Claimant;
import org.example.lfrs_group_4_oop.entity.Item;
import org.example.lfrs_group_4_oop.model.ClaimModel;
import org.example.lfrs_group_4_oop.repository.ClaimantRepository;
import org.example.lfrs_group_4_oop.repository.ItemRepository;

/**
 * Manages the workflow for validating and recording item claims.
 * This controller serves as the interface for processing claims when a found item
 * is being returned to its rightful owner.
 */
@SuppressWarnings("unused")
public class ClaimController {

    @FXML
    private TextField itemIdField;

    @FXML
    private TextField claimantIdField;

    /** Feedback label for claim processing results. */
    @FXML
    private Label statusLabel;

    private ClaimModel claimModel;
    private ClaimantRepository claimantRepository;
    private ItemRepository itemRepository;

    /**
     * @param claimModel The domain model responsible for claim-related business logic.
     * @param claimantRepository The data repository for claimant information.
     * @param itemRepository The data repository for item information.
     */
    public void setDependencies(ClaimModel claimModel, 
                                ClaimantRepository claimantRepository,
                                ItemRepository itemRepository) {
        this.claimModel = claimModel;
        this.claimantRepository = claimantRepository;
        this.itemRepository = itemRepository;
    }

    /**
     * Attempts to process a new claim for a reported item.
     * Parses numeric inputs and constructs a {@link Claim} request. Invokes the model
     * to perform validation and storage. Handles both format and business exceptions.
     */
    @FXML
    protected void onProcessClaimClick() {
        try {
            Integer itemId = Integer.parseInt(itemIdField.getText());
            Integer claimantId = Integer.parseInt(claimantIdField.getText());

            // Retrieve the item and claimant from repositories
            Item item = itemRepository.findById(itemId);
            Claimant claimant = claimantRepository.findById(claimantId); 

            if (item == null) {
                statusLabel.setText("Item not found.");
                return;
            }
            if (claimant == null) {
                statusLabel.setText("Claimant not found.");
                return;
            }

            Claim claim = new Claim();
            claim.setItemId(itemId);
            claim.setClaimantId(claimantId);

            claimModel.processClaim(item, claimant, claim);
            statusLabel.setText("Claim processed successfully.");
        } catch (NumberFormatException _) {
            statusLabel.setText("Invalid ID format.");
        } catch (Exception e) {
            statusLabel.setText("Claim error: " + e.getMessage());
        }
    }
}
