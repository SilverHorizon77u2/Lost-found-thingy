package org.example.lfrs_group_4_oop.model;

import org.example.lfrs_group_4_oop.entity.User;
import org.example.lfrs_group_4_oop.exception.ValidationException;
import org.example.lfrs_group_4_oop.repository.UserRepository;
import org.example.lfrs_group_4_oop.validator.UserValidator;

/**
 * This model handles user registration, role validation, and ensures data integrity
 */
public class UserModel {

    public static final String ROLE_ADMIN = "Administrator";
    public static final String ROLE_USER = "Standard User";

    private final UserRepository userRepository;

    /**
     * @param userRepository used for user data queries.
     */
    public UserModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Validates the format and requirements of a user's email address.
     * 
     * @param email The email address to validate.
     * @throws ValidationException if the email format is invalid or empty.
     */
    public void validateEmail(String email) {
        UserValidator.validateEmail(email);
    }

    /**
     * Validates that the provided role matches a recognized system role.
     * 
     * @param role The role name to validate.
     * @throws ValidationException if the role is null or not in the allowed set.
     */
    public void validateRole(String role) {
        UserValidator.validateRole(role);
    }

    /**
     * Authenticates a user by their email address.
     * 
     * @param email The email to check.
     * @return The authenticated User object.
     * @throws ValidationException if the user is not found.
     */
    public User authenticate(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ValidationException("Authentication failed: User not found.");
        }
        return user;
    }

    /**
     * Updates an existing user's profile information.
     *
     * @param user The user entity with updated information.
     * @throws ValidationException if any validation check fails.
     */
    public void updateProfile(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new ValidationException("Name cannot be empty.");
        }
        validateEmail(user.getEmail());
        
        if (user.getStudentNo() == null || user.getStudentNo().trim().isEmpty()) {
            throw new ValidationException("Student number cannot be empty.");
        }
        
        if (user.getProgram() == null || user.getProgram().trim().isEmpty()) {
            throw new ValidationException("Program cannot be empty.");
        }

        userRepository.update(user);
        
        // Sync with SceneManager to ensure the session is updated globally
        org.example.lfrs_group_4_oop.SceneManager.setCurrentUser(user);
    }

    /**
     * Updates a user's password after verifying the current one and validating the new one.
     * 
     * @param user The user whose password is being updated.
     * @param current The current password entered by the user.
     * @param newPass The new password to set.
     * @param confirm The confirmation of the new password.
     */
    public void updatePassword(User user, String current, String newPass, String confirm) {
        if (user == null || current == null || current.isEmpty()) {
            throw new ValidationException("Current password is required.");
        }

        // Verify current password
        if (!user.getPassword().equals(current)) {
            throw new ValidationException("Incorrect current password.");
        }

        UserValidator.validatePasswordStrength(newPass);
        UserValidator.validatePasswordMatch(newPass, confirm);

        user.setPassword(newPass);
        userRepository.update(user);
        
        // Sync with SceneManager
        org.example.lfrs_group_4_oop.SceneManager.setCurrentUser(user);
    }

    /**
     * Registers a new user in the system after enforcing business rules.
     * 
     * Business Rules:
     *  Email must follow a valid format.
     *  Role must be a recognized system role.
     *  The email address must not already exist in the database.
     * 
     * @param user The user entity to register.
     * @throws ValidationException if any business rule or validation check fails.
     */
    public void registerUser(User user) {

        validateEmail(user.getEmail());
        
        // Default role to Standard User if not specified (e.g., during public signup)
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole(ROLE_USER);
        } else {
            validateRole(user.getRole());
        }

        // Enforce uniqueness constraint at the business level
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new ValidationException("A user with this email already exists.");
        }

        userRepository.save(user);
    }
}
