package org.example.lfrs_group_4_oop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.example.lfrs_group_4_oop.util.AnimationUtils;
import org.example.lfrs_group_4_oop.model.UserModel;
import javafx.scene.paint.Color;
import org.example.lfrs_group_4_oop.SceneManager;
import org.example.lfrs_group_4_oop.entity.User;
import org.example.lfrs_group_4_oop.exception.ValidationException;

import java.io.File;

/**
 * Controller responsible for orchestrating user profile management.
 * This class coordinates high-fidelity user interactions on the profile screen,
 * including visual switching between read-only and editable form fields,
 * real-time profile picture (avatar) uploads and deletions with visual indicators,
 * password strength evaluation, and secure password updates via the {@link UserModel}.
 */
public class UserController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField studentNoField;

    @FXML
    private TextField programField;

    @FXML
    private TextField sectionField;

    @FXML
    private TextField emailField;

    @FXML
    private Label roleLabel;

    @FXML
    private Label userNameHeader;

    @FXML
    private ImageView avatarImageView;

    @FXML
    private ImageView headerAvatar;

    @FXML
    private StackPane navAvatarContainer;

    @FXML
    private Button navDashboard;

    @FXML
    private Button navMyReports;

    @FXML
    private Button navReport;

    @FXML
    private Button editProfileButton;

    @FXML
    private HBox editActionsBox;

    @FXML
    private VBox avatarEditBadge;

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label passwordStrengthLabel;

    @FXML
    private Label statusLabel;

    private UserModel userModel;

    private User currentUser;

    /**
     * Default constructor required for JavaFX FXML instantiation.
     * Ensures strict SonarQube static compliance by documenting explicitly.
     */
    public UserController() {
        // Required default constructor for JavaFX FXML Loader
    }

    /**
     * FXML Lifecycle method triggered automatically after FXML nodes are loaded.
     * Configures real-time text property listeners on the new password input field
     * to trigger password complexity validation on each keystroke.
     */
    @FXML
    public void initialize() {
        if (newPasswordField != null && passwordStrengthLabel != null) {
            newPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
                updatePasswordStrength(newVal);
            });
        }
    }

    /**
     * Injects required business models and user entities.
     *
     * @param userModel   The user model holding logical rules for credentials and updates.
     * @param currentUser The currently authenticated session user.
     */
    public void setDependencies(UserModel userModel, User currentUser) {
        this.userModel = userModel;
        this.currentUser = currentUser;
        populateFields();
        setEditMode(false);
        if (navAvatarContainer != null) {
            navAvatarContainer.getStyleClass().add("active");
        }
        if (editProfileButton != null) {
            AnimationUtils.makeTactile(editProfileButton);
        }
    }

    /**
     * Populates all UI input and text elements with the current user's profile details.
     */
    private void populateFields() {
        if (currentUser != null) {
            if (nameField != null) nameField.setText(currentUser.getName());
            if (studentNoField != null) studentNoField.setText(currentUser.getStudentNo());
            if (programField != null) programField.setText(currentUser.getProgram());
            if (sectionField != null) sectionField.setText(currentUser.getSection());
            if (emailField != null) emailField.setText(currentUser.getEmail());
            
            if (roleLabel != null) roleLabel.setText(currentUser.getRole());
            if (userNameHeader != null) userNameHeader.setText(currentUser.getName());
            
            loadAvatar();
        }
    }

    /**
     * Loads the profile picture into the circular display nodes.
     * If the user has a valid avatar path, it is rendered. Otherwise, a default
     * placeholder resource avatar is rendered. Additionally, the "Click to edit"
     * overlay is displayed only if the user is using the default placeholder image.
     */
    private void loadAvatar() {
        if (avatarImageView != null) {
            Image image = null;
            boolean hasUploadedAvatar = currentUser.getAvatarPath() != null && !currentUser.getAvatarPath().isEmpty();
            
            if (hasUploadedAvatar) {
                File file = new File(currentUser.getAvatarPath());
                if (file.exists()) {
                    image = new Image(file.toURI().toString());
                } else {
                    hasUploadedAvatar = false; // Fall back to placeholder if file was deleted externally
                }
            }
            
            if (image == null) {
                java.io.InputStream resource = getClass().getResourceAsStream("/org/example/lfrs_group_4_oop/images/placeholder-avatar.png");
                if (resource != null) {
                    image = new Image(resource);
                }
            }

            avatarImageView.setImage(image);
            if (headerAvatar != null) {
                headerAvatar.setImage(image);
            }
            
            // Toggle the click to edit instruction overlay based on avatar upload state
            if (avatarEditBadge != null) {
                avatarEditBadge.setVisible(!hasUploadedAvatar);
                avatarEditBadge.setManaged(!hasUploadedAvatar);
            }
        }
    }

    /**
     * Configures the editability of personal detail input fields.
     *
     * @param isEditing True to enable typing and show edit actions, false to read-only lock.
     */
    private void setEditMode(boolean isEditing) {
        if (nameField != null) nameField.setEditable(isEditing);
        if (studentNoField != null) studentNoField.setEditable(isEditing);
        if (programField != null) programField.setEditable(isEditing);
        if (sectionField != null) sectionField.setEditable(isEditing);
        if (emailField != null) emailField.setEditable(isEditing);

        if (editProfileButton != null) {
            editProfileButton.setVisible(!isEditing);
            editProfileButton.setManaged(!isEditing);
        }
        if (editActionsBox != null) {
            editActionsBox.setVisible(isEditing);
            editActionsBox.setManaged(isEditing);
        }
    }

    /**
     * FXML handler triggered when the user clicks the "Edit Profile" button.
     * Enables text field inputs.
     */
    @FXML
    public void onEditClick() {
        setEditMode(true);
    }

    /**
     * FXML handler triggered when the user cancels profile editing.
     * Reverts any typed changes and locks the inputs.
     */
    @FXML
    public void onCancelClick() {
        populateFields();
        setEditMode(false);
        showStatus("", false);
    }

    /**
     * FXML handler triggered when the user clicks "Save Changes".
     * Validates inputs, persists profile modifications, and triggers a clean fade transition.
     */
    @FXML
    public void onSaveClick() {
        try {
            currentUser.setName(nameField.getText());
            currentUser.setStudentNo(studentNoField.getText());
            currentUser.setProgram(programField.getText());
            currentUser.setSection(sectionField.getText());
            currentUser.setEmail(emailField.getText());

            userModel.updateProfile(currentUser);
            
            setEditMode(false);
            userNameHeader.setText(currentUser.getName());
            showStatus("Profile updated successfully!", false);
            AnimationUtils.applyFadeTransition(userNameHeader);
        } catch (ValidationException e) {
            showStatus(e.getMessage(), true);
            AnimationUtils.shake(statusLabel);
        } catch (Exception e) {
            showStatus("An unexpected error occurred: " + e.getMessage(), true);
        }
    }

    /**
     * FXML mouse handler that renders the avatar options context menu.
     * Gives the user the choice to upload a new profile picture or remove their custom picture.
     *
     * @param event The mouse click trigger event.
     */
    @FXML
    public void onAvatarClick(javafx.scene.input.MouseEvent event) {
        ContextMenu contextMenu = new ContextMenu();
        
        MenuItem uploadItem = new MenuItem("Upload Picture");
        uploadItem.setOnAction(_ -> handleUploadPicture());
        
        MenuItem deleteItem = new MenuItem("Delete Picture");
        deleteItem.setOnAction(_ -> handleDeletePicture());
        
        contextMenu.getItems().addAll(uploadItem, deleteItem);
        contextMenu.show(avatarImageView, event.getScreenX(), event.getScreenY());
    }

    /**
     * Handles local photo selection via a FileChooser dialog.
     * Persists the path and updates the profile image view instantly.
     */
    private void handleUploadPicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        
        File selectedFile = fileChooser.showOpenDialog(avatarImageView.getScene().getWindow());
        if (selectedFile != null) {
            try {
                currentUser.setAvatarPath(selectedFile.getAbsolutePath());
                userModel.updateProfile(currentUser);
                loadAvatar();
                showStatus("Avatar updated successfully!", false);
                AnimationUtils.applyFadeTransition(avatarImageView);
            } catch (Exception e) {
                showStatus("Error updating avatar: " + e.getMessage(), true);
            }
        }
    }

    /**
     * Deletes the user's custom profile picture path, reverting it to the default placeholder.
     */
    private void handleDeletePicture() {
        try {
            currentUser.setAvatarPath(null);
            userModel.updateProfile(currentUser);
            loadAvatar();
            showStatus("Avatar deleted.", false);
        } catch (Exception e) {
            showStatus("Error deleting avatar: " + e.getMessage(), true);
        }
    }

    /**
     * FXML handler triggered when the user updates their account password.
     * Clears all fields upon successful validation.
     */
    @FXML
    public void onUpdatePasswordClick() {
        try {
            userModel.updatePassword(
                currentUser,
                currentPasswordField.getText(),
                newPasswordField.getText(),
                confirmPasswordField.getText()
            );
            
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
            if (passwordStrengthLabel != null) {
                passwordStrengthLabel.setVisible(false);
                passwordStrengthLabel.setManaged(false);
            }
            
            showStatus("Password updated successfully!", false);
        } catch (ValidationException e) {
            showStatus(e.getMessage(), true);
            AnimationUtils.shake(statusLabel);
        } catch (Exception e) {
            showStatus("Password update error: " + e.getMessage(), true);
        }
    }

    /**
     * Performs real-time evaluations on password complexity as the user types.
     * Modifies the text and styling class of the strength badge dynamically.
     *
     * @param password The raw password text currently entered.
     */
    private void updatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            if (passwordStrengthLabel != null) {
                passwordStrengthLabel.setVisible(false);
                passwordStrengthLabel.setManaged(false);
            }
            return;
        }

        if (passwordStrengthLabel != null) {
            passwordStrengthLabel.setVisible(true);
            passwordStrengthLabel.setManaged(true);
            passwordStrengthLabel.getStyleClass().removeAll("strength-weak", "strength-fair", "strength-strong");

            if (password.length() < 6) {
                passwordStrengthLabel.setText("Strength: Weak");
                passwordStrengthLabel.getStyleClass().add("strength-weak");
            } else if (password.length() < 10) {
                passwordStrengthLabel.setText("Strength: Fair");
                passwordStrengthLabel.getStyleClass().add("strength-fair");
            } else {
                passwordStrengthLabel.setText("Strength: Strong");
                passwordStrengthLabel.getStyleClass().add("strength-strong");
            }
        }
    }

    /**
     * Redirects the user session to the central dashboard screen.
     */
    @FXML
    private void onDashboardClick() {
        SceneManager.showDashboard();
    }

    /**
     * Redirects the user session to the My Reports page.
     */
    @FXML
    private void onMyReportsClick() {
        SceneManager.showMyReports();
    }

    @FXML
    public void onMapClick() {
        SceneManager.showMap(currentUser);
    }

    /**
     * Redirects the user session to the report submission page.
     */
    @FXML
    private void onReportClick() {
        SceneManager.showReport();
    }

    /**
     * Refreshes and displays the user profile screen.
     */
    @FXML
    private void onProfileClick() {
        SceneManager.showProfile();
    }

    /**
     * Logs out the user session and redirects to the Login screen.
     */
    @FXML
    public void onLogoutClick() {
        SceneManager.showLogin();
    }
    @FXML
    public void onGalleryClick() {
        SceneManager.showGallery(currentUser);
    }

    /**
     * Renders feedback messages on the screen with corresponding status colors.
     *
     * @param message The feedback message content to display.
     * @param isError True to render text in red (error), false to render in green (success).
     */
    private void showStatus(String message, boolean isError) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setTextFill(isError ? Color.RED : Color.GREEN);
        }
    }
}