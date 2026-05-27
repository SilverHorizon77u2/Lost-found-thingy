package org.example.lfrs_group_4_oop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.example.lfrs_group_4_oop.SceneManager;
import org.example.lfrs_group_4_oop.entity.User;
import org.example.lfrs_group_4_oop.model.UserModel;
import org.example.lfrs_group_4_oop.util.AnimationUtils;
import org.example.lfrs_group_4_oop.validator.UserValidator;
import org.example.lfrs_group_4_oop.exception.ValidationException;

/**
 * Controller responsible for orchestrating the user account registration flow.
 * This class coordinates the signup inputs (name, email, password strength, student number,
 * program, and section), registers real-time validation listeners on field values,
 * maintains password text synchronization via bi-directional FXML bindings,
 * and calls the {@link UserModel} to persist new credentials into SQLite.
 */
public class SignupController {

    @FXML
    private javafx.scene.layout.HBox rootNode;

    @FXML
    private VBox leftPane;

    @FXML
    private VBox rightPane;

    @FXML
    private TextField nameField;

    @FXML
    private Label nameErrorLabel;

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
    private TextField studentNoField;

    @FXML
    private Label studentNoErrorLabel;

    @FXML
    private TextField programField;

    @FXML
    private TextField sectionField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button signupButton;

    private UserModel userModel;

    private boolean isPasswordVisible = false;

    /**
     * Default constructor required for JavaFX FXML Loader instantiation.
     * Ensures strict SonarQube static compliance by documenting explicitly.
     */
    public SignupController() {
        // Required default constructor for JavaFX FXML Loader
    }

    /**
     * Injects the required user business model dependency.
     *
     * @param userModel The user model coordinates register policies and validations.
     */
    public void setDependencies(UserModel userModel) {
        this.userModel = userModel;
    }

    /**
     * FXML Lifecycle method triggered automatically after FXML nodes are loaded.
     * Configures enter fade transitions, registers password bi-directional bindings,
     * and maps text property listeners to name, email, password, and student number
     * inputs to deliver instant, keystroke-by-keystroke validation feedback.
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
        if (signupButton != null) {
            AnimationUtils.makeTactile(signupButton);
        }

        // Synchronizes inputs between secure PasswordField and visible TextField
        if (passwordField != null && passwordVisibleField != null) {
            passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
        }

        // Binds real-time inline validators to input changes
        if (nameField != null) {
            nameField.textProperty().addListener((obs, oldV, newV) -> validateNameInline(newV));
        }
        if (emailField != null) {
            emailField.textProperty().addListener((obs, oldV, newV) -> validateEmailInline(newV));
        }
        if (passwordField != null) {
            passwordField.textProperty().addListener((obs, oldV, newV) -> validatePasswordInline(newV));
        }
        if (studentNoField != null) {
            studentNoField.textProperty().addListener((obs, oldV, newV) -> validateStudentNoInline(newV));
        }
    }

    /**
     * Checks name field value presence.
     *
     * @param name The current full name value.
     */
    private void validateNameInline(String name) {
        if (nameErrorLabel == null) return;
        if (name == null || name.trim().isEmpty()) {
            showError(nameErrorLabel, nameField, "Name cannot be empty.");
        } else {
            clearError(nameErrorLabel, nameField);
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
     * Triggers inline password complexity analysis on the typed password.
     *
     * @param password The current password text value.
     */
    private void validatePasswordInline(String password) {
        if (passwordErrorLabel == null) return;
        try {
            UserValidator.validatePasswordStrength(password);
            clearError(passwordErrorLabel, passwordField);
            if (passwordVisibleField != null) passwordVisibleField.setStyle("");
        } catch (ValidationException e) {
            showError(passwordErrorLabel, passwordField, e.getMessage());
            if (passwordVisibleField != null) passwordVisibleField.setStyle("-fx-border-color: #EF4444;");
        }
    }

    /**
     * Checks student number input presence.
     *
     * @param studentNo The current student number text.
     */
    private void validateStudentNoInline(String studentNo) {
        if (studentNoErrorLabel == null) return;
        if (studentNo == null || studentNo.trim().isEmpty()) {
            showError(studentNoErrorLabel, studentNoField, "Student number is required.");
        } else {
            clearError(studentNoErrorLabel, studentNoField);
        }
    }

    /**
     * Renders red border highlights on text fields and exposes error tags.
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
     * FXML action handler triggered when the user clicks the "Sign Up" button.
     * <p>
     * Re-runs all field validation checks. If valid, builds a new {@link User} entity,
     * registers it via {@link UserModel}, and prints a green success confirmation status.
     * </p>
     */
    @FXML
    protected void onSignupButtonClick() {
        // Trigger all validations explicitly
        validateNameInline(nameField.getText());
        validateEmailInline(emailField.getText());
        validatePasswordInline(passwordField.getText());
        validateStudentNoInline(studentNoField.getText());

        if (nameErrorLabel.isVisible() || emailErrorLabel.isVisible() || 
            passwordErrorLabel.isVisible() || studentNoErrorLabel.isVisible()) {
            showError(statusLabel, null, "Please fix the errors above.");
            return;
        }

        try {
            User user = new User();
            user.setName(nameField.getText());
            user.setEmail(emailField.getText());
            user.setPassword(passwordField.getText());
            user.setStudentNo(studentNoField.getText());
            user.setProgram(programField.getText());
            user.setSection(sectionField.getText());

            userModel.registerUser(user);
            
            clearError(statusLabel, null);
            statusLabel.setText("Account created! Please log in.");
            statusLabel.setStyle("-fx-text-fill: -fx-status-found;"); // Green success message
            statusLabel.setVisible(true);
            statusLabel.setManaged(true);
        } catch (Exception e) {
            showError(statusLabel, null, "Signup error: " + e.getMessage());
        }
    }

    /**
     * FXML action handler that redirects the user back to the login screen.
     */
    @FXML
    protected void onBackToLoginClick() {
        SceneManager.showLogin();
    }
}
