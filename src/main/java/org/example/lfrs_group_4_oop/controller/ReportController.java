package org.example.lfrs_group_4_oop.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.example.lfrs_group_4_oop.SceneManager;
import org.example.lfrs_group_4_oop.entity.Category;
import org.example.lfrs_group_4_oop.entity.Item;
import org.example.lfrs_group_4_oop.entity.MapZone;
import org.example.lfrs_group_4_oop.entity.User;
import org.example.lfrs_group_4_oop.model.ItemModel;
import org.example.lfrs_group_4_oop.repository.CategoryRepository;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

/** gabs
 *
 */
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import javafx.scene.control.Alert;


/**
 * Controller responsible for managing the Report Item view.
 * <p>
 * This class handles user interactions for submitting new lost item reports. 
 * It manages form inputs, image selection via a file chooser, populates categories 
 * dynamically from the database, and processes the final submission by persisting 
 * the newly created {@link Item} entity through the {@link ItemModel}.
 * </p>
 */
public class ReportController {

    // UI Field Injections
    @FXML
    private ToggleGroup reportTypeGroup;
    @FXML
    private ToggleButton btnLost;
    @FXML
    private ToggleButton btnFound;
    @FXML
    private VBox lostItemForm;
    @FXML
    private VBox foundItemInstruction;

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ComboBox<Category> categoryComboBox;

    //@FXML
    //private TextField locationField;

    @FXML
    private Label imagePathLabel;

    @FXML
    private Label statusLabel;

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
    private javafx.scene.image.ImageView headerAvatar;

    /**gabs
     */
    @FXML
    private javafx.scene.image.ImageView uploadPreview;
    @FXML
    private Label uploadIcon;
    @FXML
    private VBox uploadInstructions;

    @FXML
    private VBox customOverlay; // Make sure this injection field is at the top with your other fields!
    @FXML private ComboBox<MapZone> locationComboBox;

    // Dependencies and State
    private ItemModel itemModel;
    private CategoryRepository categoryRepository;
    private User currentUser;
    private String selectedImagePath;

    @FXML
    public void initialize() {
        locationComboBox.setItems(FXCollections.observableArrayList(MapZone.values()));
        // Custom converter to show the "Display Name" instead of the Enum name
        locationComboBox.setConverter(new StringConverter<MapZone>() {
            @Override
            public String toString(MapZone zone) { return zone == null ? null : zone.getDisplayName(); }
            @Override
            public MapZone fromString(String string) { return null; }
        });
        if (reportTypeGroup != null) {
            reportTypeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
                // If user clicks currently selected toggle, newVal might be null, 
                // so we re-select the old value to prevent unselecting both.
                if (newVal == null) {
                    oldVal.setSelected(true);
                    return;
                }
                
                if (newVal == btnFound) {
                    lostItemForm.setVisible(false);
                    lostItemForm.setManaged(false);
                    foundItemInstruction.setVisible(true);
                    foundItemInstruction.setManaged(true);
                } else {
                    lostItemForm.setVisible(true);
                    lostItemForm.setManaged(true);
                    foundItemInstruction.setVisible(false);
                    foundItemInstruction.setManaged(false);
                }
            });
        }
    }

    /**
     * Injects essential dependencies and initializes the form state.
     *
     * @param itemModel          The business logic model for item registration and workflow.
     * @param categoryRepository The repository to fetch available categories for the form.
     * @param currentUser        The currently authenticated user submitting the report.
     */
    public void setDependencies(ItemModel itemModel, CategoryRepository categoryRepository, User currentUser) {
        this.itemModel = itemModel;
        this.categoryRepository = categoryRepository;
        this.currentUser = currentUser;

        org.example.lfrs_group_4_oop.util.ImageUtils.loadHeaderAvatar(headerAvatar, currentUser);
        initializeForm();
    }

    /**
     * Prepares the category combo box.
     * Configures the {@link javafx.util.StringConverter} to display the category name,
     * fetches the list of available categories from the database, and pre-selects the first option.
     */
    private void initializeForm() {
        categoryComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Category category) {
                return category == null ? "" : category.getCategoryName();
            }

            @Override
            public Category fromString(String string) {
                return null; // Not needed for a non-editable ComboBox
            }
        });

        List<Category> categories = categoryRepository.findAll();
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));
        if (!categories.isEmpty()) {
            categoryComboBox.setValue(categories.get(0));
        }
    }

    /**
     * Handles the "Upload Photo" button click event.
     * Opens a {@link FileChooser} dialog restricted to image files (PNG, JPG, GIF).
     * If a file is selected, its absolute path is stored in state and its name is displayed in the UI.
     */
    @FXML
    public void onCloseOverlayClick() {
        if (customOverlay != null) {
            customOverlay.setVisible(false);
            customOverlay.setManaged(false);
        }

        // Redirect them back to the dashboard panel automatically
        onDashboardClick();
    }
    @FXML
    public void onSelectImageClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Item Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            selectedImagePath = selectedFile.getAbsolutePath();
            imagePathLabel.setText(selectedFile.getName());

            try {
                // Load the chosen file directly into a JavaFX Image object
                javafx.scene.image.Image previewImg = new javafx.scene.image.Image(selectedFile.toURI().toString());
                uploadPreview.setImage(previewImg);

                // Swap out placeholders to make way for the gorgeous preview
                uploadIcon.setVisible(false);
                uploadIcon.setManaged(false);
                uploadInstructions.setVisible(false);
                uploadInstructions.setManaged(false);

                uploadPreview.setVisible(true);
                uploadPreview.setManaged(true);
            } catch (Exception e) {
                System.err.println("Could not render image preview: " + e.getMessage());
            }
        }
    }
    /**
     * Handles the "Submit" button click event.
     * Constructs a new {@link Item} entity from the form inputs, hardcodes the status to "Lost",
     * attaches the currently logged-in user as the reporter, and registers the item via the model.
     * On success, clears the form and displays a success message. On failure, catches the exception
     * and displays an error message.
     */
    @FXML
    public void onSubmitClick() {
        if (btnFound != null && btnFound.isSelected()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Found Items");
            alert.setContentText("Please follow the instructions on the screen to surrender found items to the Admin office.");
            alert.showAndWait();
            return;
        }

        // 1. VALIDATION FIRST: Validate that a location zone is actually selected
        MapZone selectedZone = locationComboBox.getValue();
        if (selectedZone == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Location");
            alert.setHeaderText("Location Required");
            alert.setContentText("Please select the last known location zone from the dropdown menu.");
            alert.showAndWait();
            return; // Halts execution safely BEFORE anything gets saved
        }

        try {
            Item item = new Item();
            item.setTitle(titleField.getText());
            item.setDescription(descriptionField.getText());
            item.setStatus(ItemModel.STATUS_LOST);

            // 2. DATA SYNCHRONIZATION: Set BOTH properties so the old DTO Builder doesn't pull a null value
            item.setMapZone(selectedZone);
            item.setLocation(selectedZone.getDisplayName());

            // Image Handling
            if (selectedImagePath != null) {
                File sourceFile = new File(selectedImagePath);
                if (sourceFile.exists()) {
                    File uploadDir = new File("uploads");
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }

                    String extension = "";
                    int i = sourceFile.getName().lastIndexOf('.');
                    if (i > 0) { extension = sourceFile.getName().substring(i); }
                    String uniqueName = UUID.randomUUID().toString() + extension;

                    File destinationFile = new File(uploadDir, uniqueName);
                    Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    item.setImagePath(destinationFile.getPath());
                }
            } else {
                item.setImagePath(null);
            }

            item.setDateReported(LocalDateTime.now());

            Category selectedCategory = categoryComboBox.getValue();
            if (selectedCategory != null) {
                item.setCategoryId(selectedCategory.getId());
            }

            if (currentUser != null) {
                item.setReporterId(currentUser.getId());
            }

            // 3. Save to Database
            itemModel.registerItem(item);

            // 4. TOGGLE CUSTOM OVERLAY VISIBILITY
            if (customOverlay != null) {
                customOverlay.setVisible(true);
                customOverlay.setManaged(true);
            }

            // 5. Reset the main form inputs behind the overlay smoothly
            titleField.clear();
            descriptionField.clear();
            selectedImagePath = null;
            imagePathLabel.setText("No file selected");

            uploadPreview.setImage(null);
            uploadPreview.setVisible(false);
            uploadPreview.setManaged(false);

            uploadIcon.setVisible(true);
            uploadIcon.setManaged(true);
            uploadInstructions.setVisible(true);
            uploadInstructions.setManaged(true);

        } catch (Exception e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Submission Error");
            errorAlert.setHeaderText("Could not save the report");
            errorAlert.setContentText("Error details: " + e.getMessage());
            errorAlert.showAndWait();
        }
    }
    /**
     * Navigates the user to the main Dashboard view.
     */



    @FXML
    public void onDashboardClick() {
        SceneManager.showDashboard(currentUser);
    }
    @FXML
    public void onMapClick() {
        SceneManager.showMap(currentUser);
    }

    /**
     * Navigates the user to their specific "My Reports" view.
     */
    @FXML
    public void onMyReportsClick() {
        SceneManager.showMyReports();
    }

    /**
     * Empty handler for the Report navigation button, as the user is already on the Report page.
     */
    @FXML
    public void onReportClick() {
        // Already on report page, do nothing or refresh
    }

    /**
     * Navigates the user to the Profile management view.
     */
    @FXML
    public void onProfileClick() {
        SceneManager.showProfile();
    }
    @FXML
    public void onGalleryClick() {
        SceneManager.showGallery(currentUser); // Passing 'currentUser' ensures the nav bar avatar stays loaded!
    }

}
