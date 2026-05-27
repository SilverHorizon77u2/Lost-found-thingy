package org.example.lfrs_group_4_oop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.example.lfrs_group_4_oop.SceneManager;
import org.example.lfrs_group_4_oop.entity.User;
import org.example.lfrs_group_4_oop.repository.UserRepository;
import org.example.lfrs_group_4_oop.util.AnimationUtils;
import org.example.lfrs_group_4_oop.validator.UserValidator;
import org.example.lfrs_group_4_oop.exception.ValidationException;

/**
 * Controller responsible for orchestrating the user authentication/login flow.
 * This class coordinates login input validations (both inline real-time feedback
 * and form submission checks), password visibility toggling via bi-directional FXML
 * bindings, split-pane enter transitions, and redirects to the central dashboard
 * upon successful validation via the {@link UserRepository}.
 */
public class LoginController {

    @FXML
    private javafx.scene.layout.HBox rootNode;

    private VBox leftPane;

    @FXML
    private VBox rightPane;

    @FXML
    private TextField emailField;

    @FXML
    private Label emailErrorLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordVisibleField;

    @FXML
    private Button togglePasswordButton;

    @FXML
    private Label passwordErrorLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Button loginButton;

    private UserRepository userRepository;

    private boolean isPasswordVisible = false;

    /**
     * Default constructor required for JavaFX FXML Loader instantiation.
     * Ensures strict SonarQube static compliance by documenting explicitly.
     */
    public LoginController() {
        // Required default constructor for JavaFX FXML Loader
    }

    /**
     * Injects the required user data repository dependency.
     *
     * @param userRepository The user repository service coordinates database credentials verification.
     */
    public void setDependencies(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * FXML Lifecycle method triggered automatically after FXML nodes are loaded.
     * Sets up tactile button scaling, fades in the brand and form cards, registers
     * bi-directional bindings for plain/masked password syncing, and binds
     * real-time keystroke text listeners to email and password fields for inline feedback.
     */
    @FXML
    public void initialize() {
        if (leftPane != null) {
            AnimationUtils.applyFadeTransition(leftPane);
        }
        if (rightPane != null) {
            javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.millis(800), rightPane);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.setDelay(javafx.util.Duration.millis(200));
            ft.play();
        }
        if (loginButton != null) {
            AnimationUtils.makeTactile(loginButton);
        }

        // Bi-directional binding syncs plain-text and masked-text password entries seamlessly
        if (passwordField != null && passwordVisibleField != null) {
            passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
        }

        // Binds real-time inline validation triggers to input text properties
        if (emailField != null) {
            emailField.textProperty().addListener((obs, oldV, newV) -> validateEmailInline(newV));
        }
        if (passwordField != null) {
            passwordField.textProperty().addListener((obs, oldV, newV) -> validatePasswordInline(newV));
        }
    }

    /**
     * Triggers inline real-time regex checking on the user's email input.
     *
     * @param email The current email text value.
     */
    private void validateEmailInline(String email) {
        if (emailErrorLabel == null) return;
        try {
            UserValidator.validateEmail(email);
            clearError(emailErrorLabel, emailField);
        } catch (ValidationException e) {
            showError(emailErrorLabel, emailField, e.getMessage());
        }
    }

    /**
     * Triggers inline checking on the password length and presence.
     *
     * @param password The current password text value.
     */
    private void validatePasswordInline(String password) {
        if (passwordErrorLabel == null) return;
        if (password == null || password.isEmpty()) {
            showError(passwordErrorLabel, passwordField, "Password cannot be empty.");
            if (passwordVisibleField != null) passwordVisibleField.setStyle("-fx-border-color: #EF4444;");
        } else {
            clearError(passwordErrorLabel, passwordField);
            if (passwordVisibleField != null) passwordVisibleField.setStyle("");
        }
    }

    /**
     * Renders validation error highlights on input fields and error tags.
     *
     * @param label   The feedback error label.
     * @param field   The text field containing the invalid input.
     * @param message The validation error message to display.
     */
    private void showError(Label label, TextField field, String message) {
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
        if (field != null) {
            field.setStyle("-fx-border-color: #EF4444;");
        }
    }

    /**
     * Hides error indicators and reverts input field borders to standard styles.
     *
     * @param label The feedback error label to clear.
     * @param field The text field to reset.
     */
    private void clearError(Label label, TextField field) {
        label.setVisible(false);
        label.setManaged(false);
        if (field != null) {
            field.setStyle("");
        }
    }

    /**
     * FXML action handler triggered when clicking the Show/Hide password toggle.
     * Switches visibility states between the masked PasswordField and plain-text TextField.
     */
    @FXML
    protected void onTogglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordButton.setText("Hide");
        } else {
            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            togglePasswordButton.setText("Show");
        }
    }

    /**
     * FXML action handler triggered when the user clicks the "Sign In" button.
     * <p>
     * Validates input presence, executes database credential comparisons,
     * updates session contexts, and redirects authenticated users to the dashboard.
     * </p>
     */
    @FXML
    protected void onLoginButtonClick() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email == null || email.isEmpty()) {
            showError(statusLabel, null, "Please enter an email.");
            return;
        }

        if (password == null || password.isEmpty()) {
            showError(statusLabel, null, "Please enter a password.");
            return;
        }

        try {
            User user = userRepository.findByEmail(email);
            if (user != null) {
                if (user.getPassword().equals(password)) {
                    clearError(statusLabel, null);
                    statusLabel.setText("Login successful! Welcome, " + user.getName());
                    SceneManager.showDashboard(user);
                } else {
                    showError(statusLabel, null, "Invalid password.");
                }
            } else {
                showError(statusLabel, null, "User not found.");
            }
        } catch (Exception e) {
            showError(statusLabel, null, "Login error: " + e.getMessage());
        }
    }

    /**
     * FXML action handler redirecting standard users to the Signup panel.
     */
    @FXML
    protected void onGoToSignup() {
        SceneManager.showSignup();
    }
}
