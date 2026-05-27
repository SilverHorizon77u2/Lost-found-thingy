package org.example.lfrs_group_4_oop.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.lfrs_group_4_oop.SceneManager;
import org.example.lfrs_group_4_oop.dto.ItemDisplayDto;
import org.example.lfrs_group_4_oop.entity.User;
import org.example.lfrs_group_4_oop.model.UserModel;
import org.example.lfrs_group_4_oop.repository.ItemRepository;

import java.io.File;

/**
 * Controller responsible for managing the Item Detail view.
 * This class handles the display of an individual item's information, including its image.
 * It provides role-based functionality, revealing status update controls exclusively to
 * administrative users. Additionally, it dynamically surfaces contextual instructions
 * ("How to claim" vs. "If found instructions") based on the item's current status.
 */
public class ItemDetailController {

    // UI Field Injections
    @FXML
    private Label titleLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label categoryLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private ImageView imageView;

    @FXML
    private StackPane imageContainer;

    // Admin UI Injections
    @FXML
    private VBox adminStatusBox;

    @FXML
    private ComboBox<String> adminStatusComboBox;

    // Dynamic Instruction UI Injections
    @FXML
    private VBox instructionBox;

    @FXML
    private Label instructionTitle;

    @FXML
    private Label instructionBody;

    @FXML
    @SuppressWarnings("unused")
    private Button navDashboard;
    @FXML
    @SuppressWarnings("unused")
    private Button navMyReports;
    @FXML
    @SuppressWarnings("unused")
    private Button navReport;
    @FXML
    @SuppressWarnings("unused")
    private StackPane navAvatarContainer;

    @FXML
    private ImageView headerAvatar;

    // Dependencies and State
    private ItemRepository itemRepository;
    private User currentUser;
    private ItemDisplayDto currentItem;

    @FXML
    private javafx.scene.control.Button updateStatusButton;

    private static final String STATUS_FOUND = "Found";
    private static final String STATUS_LOST = "Lost";

    /**
     * Injects essential dependencies required for the controller's operation.
     *
     * @param itemRepository The repository interface used for data access operations, such as status updates.
     * @param currentUser    The currently authenticated user, used for role-based access control.
     */
    public void setDependencies(ItemRepository itemRepository, User currentUser) {
        this.itemRepository = itemRepository;
        this.currentUser = currentUser;

        org.example.lfrs_group_4_oop.util.ImageUtils.loadHeaderAvatar(headerAvatar, currentUser);
        
        if (updateStatusButton != null) {
            org.example.lfrs_group_4_oop.util.AnimationUtils.makeTactile(updateStatusButton);
        }
    }

    /**
     * Initializes the view with the specific details of the selected item.
     * This method orchestrates several view updates:
     * Populates standard text fields (title, description, location, etc.).
     * Resolves and displays the item's image, hiding the container if no image exists.
     * Evaluates the user's role to show or hide the administrator status update controls.
     * Injects dynamic contextual instructions depending on whether the item is "Lost" or "Found".
     *
     * @param item The {@link ItemDisplayDto} containing the presentation-ready data of the item.
     */
    public void setItem(ItemDisplayDto item) {
        this.currentItem = item;
        if (item != null) {
            populateBasicDetails(item);
            handleImageRendering(item);
            enforceRoleBasedVisibility(item);
            updateContextualInstructions(item.getStatus());
        }
    }

    private void populateBasicDetails(ItemDisplayDto item) {
        titleLabel.setText(item.getTitle());
        descriptionLabel.setText(item.getDescription());
        categoryLabel.setText(item.getCategoryName());
        locationLabel.setText(item.getLocation());
        statusLabel.setText(item.getStatus());
        dateLabel.setText(item.getDisplayDate());
    }

    private void handleImageRendering(ItemDisplayDto item) {
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            File file = new File(item.getImagePath());
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
                imageContainer.setVisible(true);
                imageContainer.setManaged(true);
                return;
            }
        }
        imageContainer.setVisible(false);
        imageContainer.setManaged(false);
    }

    private void enforceRoleBasedVisibility(ItemDisplayDto item) {
        if (currentUser != null && UserModel.ROLE_ADMIN.equalsIgnoreCase(currentUser.getRole())) {
            adminStatusBox.setVisible(true);
            adminStatusBox.setManaged(true);
            adminStatusComboBox.setItems(FXCollections.observableArrayList(STATUS_LOST, STATUS_FOUND, "Claimed"));
            adminStatusComboBox.setValue(item.getStatus());
        } else {
            adminStatusBox.setVisible(false);
            adminStatusBox.setManaged(false);
        }
    }

    private void updateContextualInstructions(String status) {
        if (STATUS_LOST.equalsIgnoreCase(status)) {
            instructionTitle.setText("If found instructions:");
            instructionBody.setText("If you have found this item, please submit a 'Found' report in the system and mention this item's title. Alternatively, turn it in to the administration office.");
            instructionBox.setVisible(true);
            instructionBox.setManaged(true);
        } else if (STATUS_FOUND.equalsIgnoreCase(status)) {
            instructionTitle.setText("How to claim?");
            instructionBody.setText("To claim this item, please visit the administration office with your student ID and proof of ownership.");
            instructionBox.setVisible(true);
            instructionBox.setManaged(true);
        } else {
            instructionBox.setVisible(false);
            instructionBox.setManaged(false);
        }
    }

    /**
     * Handles the action triggered when the user clicks the "Back" button.
     * Returns the user to the main dashboard view.
     */
    @FXML
    public void onBackClick() {
        SceneManager.showDashboard(currentUser);
    }

    @FXML
    public void onGalleryClick() {
        SceneManager.showGallery(currentUser); // Passing 'currentUser' ensures the nav bar avatar stays loaded!
    }

    @FXML
    public void onProfileClick() {
        SceneManager.showProfile();
    }

    @FXML
    public void onMapClick() {
        SceneManager.showMap(currentUser);
    }

    @FXML
    public void onDashboardClick() {
        SceneManager.showDashboard(currentUser);
    }

    @FXML
    public void onMyReportsClick() {
        SceneManager.showMyReports();
    }

    @FXML
    public void onReportClick() {
        SceneManager.showReport();
    }

    @FXML
    public void onLogoutClick() {
        SceneManager.showLogin();
    }

    @FXML
    public void onUpdateStatusClick() {
        if (adminStatusComboBox.getValue() != null && currentItem != null && itemRepository != null) {
            String newStatus = adminStatusComboBox.getValue();
            
            // Persist the status update to the database
            itemRepository.updateStatus(currentItem.getId(), newStatus);
            statusLabel.setText(newStatus);
            
            // Re-evaluate and apply contextual instructions based on the updated status
            updateContextualInstructions(newStatus);
        }
    }
}