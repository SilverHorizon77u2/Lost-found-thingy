package org.example.lfrs_group_4_oop;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.lfrs_group_4_oop.controller.*;
import org.example.lfrs_group_4_oop.dao.CategoryDao;
import org.example.lfrs_group_4_oop.dao.ItemDao;
import org.example.lfrs_group_4_oop.dao.UserDao;
import org.example.lfrs_group_4_oop.entity.User;
import org.example.lfrs_group_4_oop.model.ItemModel;
import org.example.lfrs_group_4_oop.model.UserModel;
import org.example.lfrs_group_4_oop.repository.CategoryRepository;
import org.example.lfrs_group_4_oop.repository.ItemRepository;
import org.example.lfrs_group_4_oop.repository.UserRepository;
import org.example.lfrs_group_4_oop.service.ItemDataAggregator;
import org.example.lfrs_group_4_oop.service.ReportService;

import java.io.IOException;
import java.util.logging.Logger;

/**gabs
 *
 */
import org.example.lfrs_group_4_oop.dto.ItemDisplayDto;

/**
 * Manages scene switching and dependency injection for the application.
 */
public class SceneManager {

    private static final Logger LOGGER = Logger.getLogger(SceneManager.class.getName());

    private SceneManager() {
        // Private constructor to hide the implicit public one
    }

    private static Stage primaryStage;
    private static User currentUser;
    private static final UserRepository userRepository = new UserDao();
    private static final ItemRepository itemRepository = new ItemDao();
    private static final CategoryRepository categoryRepository = new CategoryDao();
    private static final UserModel userModel = new UserModel(userRepository);
    private static final ReportService reportService = new ReportService(itemRepository);
    private static final ItemDataAggregator itemDataAggregator = new ItemDataAggregator(categoryRepository);

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    private static void switchScene(Parent root, String title) {
        if (primaryStage.getScene() == null) {
            primaryStage.setScene(new Scene(root));
        } else {
            primaryStage.getScene().setRoot(root);
        }

        javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.millis(200), root);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        primaryStage.setTitle(title);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void showLogin() {
        currentUser = null;
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/org/example/lfrs_group_4_oop/fxml/auth/Login.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();
            controller.setDependencies(userRepository);

            switchScene(root, "LFRS - Login");
        } catch (Exception e) {
            handleError("Login Load Error", e);
        }
    }

    public static void showSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/org/example/lfrs_group_4_oop/fxml/auth/Signup.fxml"));
            Parent root = loader.load();

            SignupController controller = loader.getController();
            controller.setDependencies(userModel);

            switchScene(root, "LFRS - Create Account");
        } catch (Exception e) {
            handleError("Signup Load Error", e);
        }
    }

    public static void showDashboard(User user) {
        if (user != null) {
            currentUser = user;
        }
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/org/example/lfrs_group_4_oop/fxml/Dashboard.fxml"));
            Parent root = loader.load();

            DashboardController controller = loader.getController();
            controller.setDependencies(reportService, itemRepository, categoryRepository, itemDataAggregator, currentUser);

            switchScene(root, "LFRS - Dashboard");
        } catch (Exception e) {
            handleError("Dashboard Load Error", e);
        }
    }

    public static void showDashboard() {
        showDashboard(currentUser);
    }

    public static void showMyReports() {
        if (currentUser == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/org/example/lfrs_group_4_oop/fxml/Dashboard.fxml"));
            Parent root = loader.load();

            DashboardController controller = loader.getController();
            controller.setDependencies(reportService, itemRepository, categoryRepository, itemDataAggregator, currentUser);
            controller.setMyReportsMode(true);

            switchScene(root, "LFRS - My Reports");
        } catch (Exception e) {
            handleError("My Reports Load Error", e);
        }
    }

    public static void showItemDetails(ItemDisplayDto itemDto, User user) {
        try {
            LOGGER.info("SceneManager.showItemDetails() called for item ID: " + itemDto.getId());
            java.net.URL fxmlUrl = SceneManager.class.getResource("/org/example/lfrs_group_4_oop/fxml/item/ItemDetail.fxml"); // Adjust path if needed!
            if (fxmlUrl == null) {
                throw new IOException("Cannot find FXML resource for Item Detail View");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            ItemDetailController controller = loader.getController();
            if (controller == null) {
                throw new IllegalStateException("ItemDetailController is null after loading FXML");
            }

            // Set up dependencies and inject the targeted item data cleanly
            controller.setDependencies(itemRepository, user);
            controller.setItem(itemDto);

            switchScene(root, "LFRS - Item Details");
        } catch (Exception e) {
            handleError("Item Detail View Load Error", e);
        }
    }
    public static void showReport() {
        try {
            LOGGER.info("SceneManager.showReport() called.");
            if (primaryStage == null) {
                throw new IllegalStateException("primaryStage is null");
            }

            java.net.URL fxmlUrl = SceneManager.class.getResource("/org/example/lfrs_group_4_oop/fxml/report/Report.fxml");
            LOGGER.log(java.util.logging.Level.INFO, () -> "Debug: FXML URL: " + fxmlUrl);

            if (fxmlUrl == null) {
                throw new IOException("Cannot find FXML resource: /org/example/lfrs_group_4_oop/fxml/report/Report.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            ReportController controller = loader.getController();
            if (controller == null) {
                throw new IllegalStateException("ReportController is null after loading FXML");
            }

            ItemModel itemModel = new ItemModel(itemRepository, categoryRepository);
            controller.setDependencies(itemModel, categoryRepository, currentUser);

            switchScene(root, "LFRS - Report Item");
            LOGGER.info("Report scene shown successfully.");
        } catch (Exception e) {
            handleError("Report Page Load Error", e);
        }
    }

    public static void showProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/org/example/lfrs_group_4_oop/fxml/user/Profile.fxml"));
            Parent root = loader.load();

            UserController controller = loader.getController();
            controller.setDependencies(userModel, currentUser);

            switchScene(root, "LFRS - User Profile");
        } catch (Exception e) {
            handleError("Profile Page Load Error", e);
        }
    }

    public static void showGallery(User user) {
        try {
            LOGGER.info("SceneManager.showGallery() called.");
            if (primaryStage == null) {
                throw new IllegalStateException("primaryStage is null");
            }

            java.net.URL fxmlUrl = SceneManager.class.getResource("/org/example/lfrs_group_4_oop/fxml/gallery/Gallery.fxml");
            if (fxmlUrl == null) {
                throw new IOException("Cannot find FXML resource: /org/example/lfrs_group_4_oop/fxml/gallery/Gallery.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            GalleryController controller = loader.getController();

            if (controller == null) {
                throw new IllegalStateException("GalleryController is null after loading FXML");
            }

            // Pass repositories and model dependencies down natively
            ItemModel itemModel = new ItemModel(itemRepository, categoryRepository);
            controller.setDependencies(itemModel, itemRepository, categoryRepository, user);

            switchScene(root, "LFRS - Photo Gallery");
            LOGGER.info("Gallery scene shown successfully.");
        } catch (Exception e) {
            handleError("Gallery Page Load Error", e);
        }
    }


    /**
     * Launches the map preview scene utility stage environment framework layout.
     */
    public static void showMap(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/org/example/lfrs_group_4_oop/fxml/map/Map.fxml"));
            Parent root = loader.load();

            MapController controller = loader.getController();

            // LOCAL FIX: Create the model instance here
            ItemModel itemModel = new ItemModel(itemRepository, categoryRepository);
            controller.setDependencies(itemModel, itemRepository, categoryRepository, user);

            switchScene(root, "LFRS - Campus Map");
        } catch (Exception e) {
            handleError("Map Load Error", e);
        }
    }

    /**
     * Bridges navigation from the Map View over to the Gallery.
     */
    public static void showGalleryWithZone(User user, org.example.lfrs_group_4_oop.entity.MapZone targetedZone) {
        try {
            // NOTE: Check your path! You had "/org/example/lfrs_group_4_oop.fxml/Gallery.fxml"
            // It should likely be "/org/example/lfrs_group_4_oop/fxml/gallery/Gallery.fxml"
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/org/example/lfrs_group_4_oop/fxml/gallery/Gallery.fxml"));
            Parent root = loader.load();

            GalleryController controller = loader.getController();

            // LOCAL FIX: Create the model instance here
            ItemModel itemModel = new ItemModel(itemRepository, categoryRepository);
            controller.setDependencies(itemModel, itemRepository, categoryRepository, user);

            if (targetedZone != null) {
                controller.setInitialLocationFilter(targetedZone);
            }

            switchScene(root, "LFRS - Photo Gallery");
        } catch (Exception e) {
            handleError("Gallery Load Error", e);
        }
    }


    private static void handleError(String title, Exception e) {
        LOGGER.log(java.util.logging.Level.SEVERE, e, () -> title + ": " + e.getMessage());

        Alert alert = new Alert(Alert.AlertType.ERROR);        alert.setTitle("System Error");
        alert.setHeaderText(title);
        alert.setContentText(e.getMessage() != null ? e.getMessage() : e.toString());
        alert.showAndWait();
    }
}