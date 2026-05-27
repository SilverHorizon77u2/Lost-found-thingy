package org.example.lfrs_group_4_oop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import org.example.lfrs_group_4_oop.SceneManager;
import org.example.lfrs_group_4_oop.entity.MapZone;
import org.example.lfrs_group_4_oop.entity.User;
import org.example.lfrs_group_4_oop.model.ItemModel;
import org.example.lfrs_group_4_oop.repository.CategoryRepository;
import org.example.lfrs_group_4_oop.repository.ItemRepository;

import java.io.File;

public class MapController {

    @FXML private Pane mapContainerPane;
    @FXML private ImageView mapGraphicImageView;
    @FXML private Label zoneTitleLabel;
    @FXML private Label zoneDescLabel;

    // FXML Polygons mapped directly from layout view file
    @FXML private Polygon buildingZone;
    @FXML private Polygon nearCourtZone;
    @FXML private Polygon courtZone;
    @FXML private Polygon canteenZone;
    @FXML private Polygon parkingZone;
    @FXML private Polygon kubosZone;

    @FXML private ImageView headerAvatar;

    private ItemModel itemModel;
    private ItemRepository itemRepository;
    private CategoryRepository categoryRepository;
    private User currentUser;

    // Standard Uniform Color Constants representing styling properties rules matrix
    private static final String FILL_IDLE = "-fx-fill: rgba(255, 255, 255, 0.01); -fx-stroke: transparent; -fx-cursor: hand;";
    private static final String FILL_HOVER = "-fx-fill: rgba(194, 39, 39, 0.25); -fx-stroke: #C22727; -fx-stroke-width: 2px; -fx-cursor: hand;";

    @FXML
    public void initialize() {
        setupPolygonCoordinates();
        configureZoneInteractions();
    }

    public void setDependencies(ItemModel itemModel, ItemRepository itemRepository, CategoryRepository categoryRepository, User currentUser) {
        this.itemModel = itemModel;
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.currentUser = currentUser;

        org.example.lfrs_group_4_oop.util.ImageUtils.loadHeaderAvatar(headerAvatar, currentUser);
        loadCampusMapGraphic();
    }

    private void loadCampusMapGraphic() {
        try {
            // Use the ClassLoader to find the resource inside the JAR/classpath
            String path = "/org/example/lfrs_group_4_oop/images/image_f887a4.png";
            java.net.URL imageUrl = getClass().getResource(path);

            if (imageUrl != null) {
                mapGraphicImageView.setImage(new Image(imageUrl.toExternalForm()));
            } else {
                System.err.println("Graphic asset not found at: " + path);
            }
        } catch (Exception e) {
            System.err.println("Error loading map graphic: " + e.getMessage());
        }
    }
    /**
     * Maps coordinate bounds for each polygon zone based on a fixed 600x600 px view scale window box matrix.
     */
    private void setupPolygonCoordinates() {
        // 1. Building (Main Complex) Left Box Area bounds
        buildingZone.getPoints().addAll(new Double[]{
                30.0, 30.0,
                210.0, 30.0,
                210.0, 520.0,
                30.0, 520.0
        });

        // 2. Near Court (Yellow Zone Center Box)
        nearCourtZone.getPoints().addAll(new Double[]{
                245.0, 315.0,
                375.0, 315.0,
                375.0, 510.0,
                245.0, 510.0
        });

        // 3. Court (Basketball Court Green Box Area)
        courtZone.getPoints().addAll(new Double[]{
                380.0, 315.0,
                580.0, 315.0,
                580.0, 590.0,
                380.0, 590.0
        });

        // 4. Canteen (Bottom Dark Blue Box Diagonal Wedge Shape)
        canteenZone.getPoints().addAll(new Double[]{
                60.0, 540.0,
                375.0, 540.0,
                375.0, 590.0,
                260.0, 590.0,
                60.0, 560.0
        });

        // 5. Parking and Gate (Top Right Blue polygon zone shape)
        parkingZone.getPoints().addAll(new Double[]{
                385.0, 30.0,
                550.0, 30.0,
                580.0, 250.0,
                385.0, 250.0
        });

        // 6. Kubos And Leon Arcillas (Top Center Green Forest Trees Node grid area)
        kubosZone.getPoints().addAll(new Double[]{
                240.0, 30.0,
                380.0, 30.0,
                380.0, 310.0,
                240.0, 310.0
        });
    }

    /**
     * Loops across all overlay vector assets to hook up event listeners cleanly.
     */
    private void configureZoneInteractions() {
        bindZoneActions(buildingZone, MapZone.BUILDING, "Building (Main Complex)", "Main academic facilities, laboratory networks, complex lecture halls, and main administrative check offices.");
        bindZoneActions(nearCourtZone, MapZone.NEAR_COURT, "Near Court", "Central common walkway perimeter plaza sitting right adjacent to the structural sports gym entrance lines.");
        bindZoneActions(courtZone, MapZone.COURT, "Sports Court Arena", "Outdoor basketball and recreational activities facility court area footprint matrices.");
        bindZoneActions(canteenZone, MapZone.CANTEEN, "Campus Canteen", "Main dining lounge workspace, food production stalls, and student gathering social courtyard spaces.");
        bindZoneActions(parkingZone, MapZone.PARKING_GATE, "Parking and Gate Lot", "Main entry vehicle checkpoints, secure barricades, automotive storage bays, and front transit drop points.");
        bindZoneActions(kubosZone, MapZone.KUBOS_LEON_ARCILLAS, "Kubos & Leon Arcillas", "Outdoor open-air thatched huts, resting alcoves, tree clusters, and green study park environments.");
    }

    private void bindZoneActions(Polygon poly, MapZone zone, String title, String description) {
        // Set transparent overlay initialization
        poly.setStyle(FILL_IDLE);

        // Hover Enter Action
        poly.setOnMouseEntered(event -> {
            poly.setStyle(FILL_HOVER);
            zoneTitleLabel.setText(title);
            zoneDescLabel.setText(description);
        });

        // Hover Exit Action
        poly.setOnMouseExited(event -> {
            poly.setStyle(FILL_IDLE);
            zoneTitleLabel.setText("No Region Selected");
            zoneDescLabel.setText("Move your mouse cursor over the school layout layout map areas to inspect local zones.");
        });

        // Click Action: Jump to Gallery with predefined filters!
        poly.setOnMouseClicked(event -> {
            System.out.println("Navigating to gallery focused on zone: " + zone.getDisplayName());
            SceneManager.showGalleryWithZone(currentUser, zone); // Wired up cleanly next!
        });
    }

    // --- Action Controls Nav Handlers ---
    @FXML public void onDashboardClick() { SceneManager.showDashboard(currentUser); }
    @FXML public void onMyReportsClick() { SceneManager.showMyReports(); }
    @FXML public void onReportClick() { SceneManager.showReport(); }
    @FXML public void onGalleryClick() { SceneManager.showGallery(currentUser); }
    @FXML public void onProfileClick() { SceneManager.showProfile(); }
    @FXML
    public void onMapClick() {
        SceneManager.showMap(currentUser);
    }
}