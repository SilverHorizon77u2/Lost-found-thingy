package org.example.lfrs_group_4_oop.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import org.example.lfrs_group_4_oop.SceneManager;
import org.example.lfrs_group_4_oop.dto.ItemDisplayDto;
import org.example.lfrs_group_4_oop.entity.Category;
import org.example.lfrs_group_4_oop.entity.Item;
import org.example.lfrs_group_4_oop.entity.User;
import org.example.lfrs_group_4_oop.model.ItemModel;
import org.example.lfrs_group_4_oop.repository.CategoryRepository;
import org.example.lfrs_group_4_oop.repository.ItemRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryController {

    @FXML private TilePane galleryTilePane;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ComboBox<Category> categoryFilter;
    @FXML private Button searchButton;
    @FXML private Button resetButton;

    @FXML private Button navDashboard;
    @FXML private Button navMyReports;
    @FXML private Button navReport;
    @FXML private Button navGallery;
    @FXML private ImageView headerAvatar;
    @FXML private StackPane navAvatarContainer;

    @FXML private ComboBox<String> locationFilter;

    private ItemModel itemModel;
    private ItemRepository itemRepository;
    private CategoryRepository categoryRepository;
    private User currentUser;

    // Backing collections tracking state changes internally
    private List<ItemDisplayDto> allGalleryDtos = new ArrayList<>();
    private List<ItemDisplayDto> filteredGalleryDtos = new ArrayList<>();

    public void setDependencies(ItemModel itemModel, ItemRepository itemRepository, CategoryRepository categoryRepository, User currentUser) {
        this.itemModel = itemModel;
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.currentUser = currentUser;

        org.example.lfrs_group_4_oop.util.ImageUtils.loadHeaderAvatar(headerAvatar, currentUser);

        if (searchButton != null) {
            org.example.lfrs_group_4_oop.util.AnimationUtils.makeTactile(searchButton);
        }

        setupFilters();
        initializeMasterData();
    }
    /**
     * Receives an initial location filter from the interactive campus map.
     */
    public void setInitialLocationFilter(org.example.lfrs_group_4_oop.entity.MapZone initialZone) {
        if (locationFilter != null && initialZone != null) {
            locationFilter.setValue(initialZone.getDisplayName());
            // Immediately apply the filter parameters
            applyFiltersAndSearch();
        }
    }

    private void setupFilters() {
        if (statusFilter != null) {
            statusFilter.setItems(FXCollections.observableArrayList("All", "Lost", "Found", "Claimed"));
            statusFilter.setValue("All");
            statusFilter.setOnAction(event -> applyFiltersAndSearch());
        }

        if (categoryFilter != null && categoryRepository != null) {
            List<Category> categories = categoryRepository.findAll();
            Category allCategory = new Category(null, "All Categories");
            categories.addFirst(allCategory);
            categoryFilter.setItems(FXCollections.observableArrayList(categories));

            categoryFilter.setConverter(new StringConverter<Category>() {
                @Override
                public String toString(Category object) {
                    return object == null ? null : object.getCategoryName();
                }
                @Override
                public Category fromString(String string) {
                    return null;
                }
            });
            categoryFilter.getSelectionModel().selectFirst();
            categoryFilter.setOnAction(event -> applyFiltersAndSearch());
        }

        // NEW: Populate locations from your MapZone enum names
        if (locationFilter != null) {
            List<String> zoneNames = new ArrayList<>();
            for (org.example.lfrs_group_4_oop.entity.MapZone zone : org.example.lfrs_group_4_oop.entity.MapZone.values()) {
                zoneNames.add(zone.getDisplayName());
            }
            locationFilter.setItems(FXCollections.observableArrayList(zoneNames));
            locationFilter.setValue("All Locations"); // Default choice
            locationFilter.setOnAction(event -> applyFiltersAndSearch());
        }
    }
    /**
     * Loads raw items from the DB one time, converts them using your builder, and stores them in memory.
     */
    private void initializeMasterData() {
        allGalleryDtos.clear();
        if (itemRepository == null) return;

        List<Item> rawItems = itemRepository.findAll();
        if (rawItems == null) return;

        for (Item item : rawItems) {
            if (item.getImagePath() == null || item.getImagePath().trim().isEmpty()) {
                continue;
            }

            File imgFile = new File(item.getImagePath());
            if (!imgFile.exists()) {
                continue;
            }

            // DYNAMIC DATABASE LOOKUP CHANNELS VIA ID POINTERS
            String currentItemCategory = "Other";
            if (categoryRepository != null && item.getCategoryId() != null) {
                Category catEntity = categoryRepository.findById(item.getCategoryId());
                if (catEntity != null && catEntity.getCategoryName() != null) {
                    currentItemCategory = catEntity.getCategoryName();
                }
            }

            // Inside the for(Item item : rawItems) loop in GalleryController.java
            String locationString = (item.getMapZone() != null)
                    ? item.getMapZone().getDisplayName()
                    : "Unknown Location";

            ItemDisplayDto dto = new ItemDisplayDto.Builder()
                    .id(item.getId())
                    .title(item.getTitle() != null ? item.getTitle() : "Untitled Item")
                    .description(item.getDescription())
                    .zoneName(locationString)     // Add the new MapZone display name
                    .location(item.getLocation())
                    .status(item.getStatus())
                    .imagePath(item.getImagePath())
                    .rawDate(item.getDateReported())
                    .categoryName(currentItemCategory)
                    .reporterName(item.getReporterName() != null ? item.getReporterName() : "Unknown User")
                    .build();

            allGalleryDtos.add(dto);
        }

        applyFiltersAndSearch();
    }

    /**
     * Filters the master list based on search keywords and combo box preferences.
     */
    private void applyFiltersAndSearch() {
        filteredGalleryDtos.clear();

        String keyword = (searchField != null) ? searchField.getText().trim().toLowerCase() : "";
        String statusCriterion = (statusFilter != null) ? statusFilter.getValue() : "All";

        Category selectedCategory = (categoryFilter != null) ? categoryFilter.getValue() : null;
        String categoryCriterion = (selectedCategory != null && selectedCategory.getCategoryName() != null)
                ? selectedCategory.getCategoryName()
                : "All Categories";

        // Extract selected location string criteria safely
        String locationCriterion = (locationFilter != null) ? locationFilter.getValue() : "All Locations";

        for (ItemDisplayDto dto : allGalleryDtos) {
            boolean matchesStatus = "All".equalsIgnoreCase(statusCriterion) ||
                    (dto.getStatus() != null && dto.getStatus().equalsIgnoreCase(statusCriterion));

            boolean matchesKeyword = keyword.isEmpty() ||
                    (dto.getTitle() != null && dto.getTitle().toLowerCase().contains(keyword)) ||
                    (dto.getDescription() != null && dto.getDescription().toLowerCase().contains(keyword));

            boolean matchesCategory = "All Categories".equalsIgnoreCase(categoryCriterion) ||
                    (dto.getCategoryName() != null && dto.getCategoryName().equalsIgnoreCase(categoryCriterion));

            // NEW: Evaluate location string matches item's location property
            boolean matchesLocation = "All Locations".equalsIgnoreCase(locationCriterion) ||
                    (dto.getLocation() != null && dto.getLocation().equalsIgnoreCase(locationCriterion));

            // Must satisfy ALL 4 filter rules to display on grid canvas workspace
            if (matchesStatus && matchesKeyword && matchesCategory && matchesLocation) {
                filteredGalleryDtos.add(dto);
            }
        }

        renderGalleryUiGrid();
    }
    /**
     * Repaints the actual visual cards in the TilePane display grid.
     */
    private void renderGalleryUiGrid() {
        galleryTilePane.getChildren().clear();

        if (filteredGalleryDtos.isEmpty()) {
            Label noItemsLabel = new Label("No matching items found.");
            noItemsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748B; -fx-font-style: italic;");
            galleryTilePane.getChildren().add(noItemsLabel);
            return;
        }

        for (ItemDisplayDto dto : filteredGalleryDtos) {
            File imgFile = new File(dto.getImagePath());

            VBox card = new VBox();
            card.setSpacing(12);
            card.setPadding(new Insets(0, 0, 12, 0));
            card.setPrefWidth(230); card.setMinWidth(230); card.setMaxWidth(230);
            card.setPrefHeight(260); card.setMinHeight(260); card.setMaxHeight(260);
            card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 8px; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 12, 0, 0, 4); -fx-cursor: hand; -fx-overflow: hidden;");

            StackPane imgWrapper = new StackPane();
            imgWrapper.setPrefHeight(150); imgWrapper.setMaxHeight(150); imgWrapper.setMinHeight(150);
            imgWrapper.setStyle("-fx-background-color: #F1F5F9; -fx-background-radius: 8px 8px 0px 0px;");

            ImageView imageView = new ImageView();
            imageView.setFitWidth(230);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);
            imageView.setImage(new Image(imgFile.toURI().toString()));

            StackPane.setAlignment(imageView, javafx.geometry.Pos.CENTER);
            imgWrapper.getChildren().add(imageView);

            VBox textContainer = new VBox();
            textContainer.setSpacing(4);
            textContainer.setPadding(new Insets(0, 12, 0, 12));

            Label titleLabel = new Label(dto.getTitle());
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1E293B;");
            titleLabel.setWrapText(false);

            Label descLabel = new Label(dto.getDescription());
            descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
            descLabel.setWrapText(true);
            descLabel.setPrefHeight(34); descLabel.setMaxHeight(34);

            textContainer.getChildren().addAll(titleLabel, descLabel);
            card.getChildren().addAll(imgWrapper, textContainer);

            card.setOnMouseClicked(event -> {
                SceneManager.showItemDetails(dto, currentUser);
            });

            galleryTilePane.getChildren().add(card);
        }
    }

    // --- Interactive Search Form FXML Actions ---

    @FXML
    public void onSearchClick() {
        applyFiltersAndSearch();
    }

    @FXML
    public void onResetClick() {
        if (searchField != null) searchField.clear();
        if (statusFilter != null) statusFilter.setValue("All");
        if (categoryFilter != null) categoryFilter.getSelectionModel().selectFirst();
        if (locationFilter != null) locationFilter.setValue("All Locations"); // CLEAR LOCATION FILTER
        applyFiltersAndSearch();
    }
    // --- Global Navigation Links ---
    @FXML public void onDashboardClick() { SceneManager.showDashboard(currentUser); }
    @FXML public void onMyReportsClick() { SceneManager.showMyReports(); }
    @FXML public void onReportClick() { SceneManager.showReport(); }
    @FXML public void onGalleryClick() { /* Do nothing - already here */ }
    @FXML public void onProfileClick() { SceneManager.showProfile(); }
    @FXML
    public void onMapClick() {
        SceneManager.showMap(currentUser);
    }
}