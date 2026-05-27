package org.example.lfrs_group_4_oop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.lfrs_group_4_oop.entity.Item;
import org.example.lfrs_group_4_oop.model.ItemModel;
import org.example.lfrs_group_4_oop.repository.ItemRepository;

import java.util.List;

/**
 * Manages the lifecycle and administration of lost and found items.
 * This controller provides the interface for adding new items, searching existing records,
 * and maintaining the status of reports.
 */
@SuppressWarnings("unused")
public class ItemController {

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField locationField;

    /** Dropdown selection for the current operational status (e.g., Lost, Found, Claimed). */
    @FXML
    private ComboBox<String> statusComboBox;

    /** Visual table displaying current item records to the user. */
    @FXML
    private TableView<Item> itemTable;

    /** Feedback label for operational status messages. */
    @FXML
    private Label statusLabel;

    private ItemModel itemModel;
    private ItemRepository itemRepository;

    /**
     * @param itemModel The domain model responsible for item-related business logic.
     * @param itemRepository The persistence repository for item data.
     */
    public void setDependencies(ItemModel itemModel, ItemRepository itemRepository) {
        this.itemModel = itemModel;
        this.itemRepository = itemRepository;
    }

    /**
     * Initializes the UI components.
     */
    @FXML
    public void initialize() {
        // Setup table columns and status options
        if (statusComboBox != null) {
            statusComboBox.getItems().addAll("Lost", "Found", "Claimed");
        }
    }

    /**
     * Handles the creation of a new item report.
     * Collects user input, constructs a transient {@link Item} entity, and submits it
     * for registration via the model. Refreshes the display table on success.
     */
    @FXML
    protected void onAddItemClick() {
        try {
            Item item = new Item();
            item.setDescription(descriptionField.getText());
            item.setLocation(locationField.getText());
            item.setStatus(statusComboBox.getValue());
            
            itemModel.registerItem(item);
            statusLabel.setText("Item added successfully.");
            refreshTable();
        } catch (Exception e) {
            statusLabel.setText("Error adding item: " + e.getMessage());
        }
    }

    /**
     * Synchronizes the UI table with the current state of the database.
     */
    private void refreshTable() {
        if (itemTable != null) {
            itemTable.getItems().setAll(itemRepository.findAll());
        }
    }
}
