package org.example.lfrs_group_4_oop.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.lfrs_group_4_oop.entity.User;

import java.io.File;
import java.io.InputStream;

/**
 * Utility class for handling image-related operations across controllers.
 */
public final class ImageUtils {

    private ImageUtils() {
        // Utility class
    }

    /**
     * Loads the header avatar for a user into an ImageView.
     *
     * @param imageView   The ImageView to populate.
     * @param currentUser The user whose avatar should be loaded.
     */
    public static void loadHeaderAvatar(ImageView imageView, User currentUser) {
        loadAvatar(imageView, currentUser);
    }

    /**
     * Loads the avatar for a user into an ImageView (either header or profile).
     *
     * @param imageView   The ImageView to populate.
     * @param currentUser The user whose avatar should be loaded.
     */
    public static void loadAvatar(ImageView imageView, User currentUser) {
        if (imageView == null) return;

        if (currentUser != null && currentUser.getAvatarPath() != null && !currentUser.getAvatarPath().isEmpty()) {
            File file = new File(currentUser.getAvatarPath());
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
                return;
            }
        }

        // Default placeholder
        InputStream resource = ImageUtils.class.getResourceAsStream("/org/example/lfrs_group_4_oop/images/placeholder-avatar.png");
        if (resource != null) {
            imageView.setImage(new Image(resource));
        } else {
            imageView.setImage(null);
        }
    }
}
