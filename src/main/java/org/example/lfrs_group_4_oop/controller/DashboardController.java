package org.example.lfrs_group_4_oop.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.example.lfrs_group_4_oop.SceneManager;
import org.example.lfrs_group_4_oop.dto.ItemDisplayDto;
import org.example.lfrs_group_4_oop.dto.StatusTrend;
import org.example.lfrs_group_4_oop.dto.TrendMetrics;
import org.example.lfrs_group_4_oop.entity.Category;
import org.example.lfrs_group_4_oop.entity.Item;
import org.example.lfrs_group_4_oop.entity.User;
import org.example.lfrs_group_4_oop.repository.CategoryRepository;
import org.example.lfrs_group_4_oop.repository.ItemRepository;
import org.example.lfrs_group_4_oop.service.ItemDataAggregator;
import org.example.lfrs_group_4_oop.service.ReportService;

import java.time.Year;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller responsible for orchestrating the main lost and found dashboard.
 * This class coordinates the dashboard interactions, providing role-based views
 * (context-aware columns and administrative overviews), complex multi-filter
 * database search queries, Month-over-Month (MoM) trend indicator cards, and a
 * high-performance client-side TableView pagination toolbar supporting large datasets.
 */
public class DashboardController {

    private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());
    private static final String STYLE_ACTIVE = "active";

    @FXML
    private Label welcomeLabel;

    @FXML
    private HBox welcomeContainer;

    @FXML
    private Label totalReportsLabel;

    @FXML
    private Label lostItemsLabel;

    @FXML
    private Label foundItemsLabel;

    @FXML
    private Label claimedLabel;

    @FXML
    private Label unclaimedLabel;

    @FXML
    private Label totalTrendLabel;

    @FXML
    private Label lostTrendLabel;

    @FXML
    private Label foundTrendLabel;

    @FXML
    private Label claimedTrendLabel;

    @FXML
    private Label unclaimedTrendLabel;

    @FXML
    private TableView<ItemDisplayDto> itemTable;

    @FXML
    private TableColumn<ItemDisplayDto, String> titleColumn;

    @FXML
    private TableColumn<ItemDisplayDto, String> statusColumn;

    @FXML
    private TableColumn<ItemDisplayDto, String> dateColumn;

    @FXML
    private TableColumn<ItemDisplayDto, String> reportedByColumn;

    @FXML
    private TextField searchField;

    @FXML
    private VBox filterPanel;

    @FXML
    private ComboBox<String> statusFilter;

    @FXML
    private ComboBox<Category> categoryFilter;

    @FXML
    private ComboBox<Integer> yearFilter;

    @FXML
    private ComboBox<String> monthFilter;

    @FXML
    private Button searchButton;

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
    private ImageView headerAvatar;

    @FXML
    private ComboBox<Integer> pageSizeComboBox;

    @FXML
    private Button firstPageButton;

    @FXML
    private Button prevPageButton;

    @FXML
    private Label pageLabel;

    @FXML
    private Button nextPageButton;

    @FXML
    private Button lastPageButton;

    @FXML
    private Label totalItemsLabel;

    private int currentPage = 1;

    private int pageSize = 10;

    private int totalPages = 1;

    private List<ItemDisplayDto> allFilteredDtos = new java.util.ArrayList<>();

    private ReportService reportService;

    private ItemRepository itemRepository;

    private CategoryRepository categoryRepository;

    private ItemDataAggregator itemDataAggregator;

    private User currentUser;

    private boolean isMyReportsView = false;

    /**
     * Default constructor required for JavaFX FXML Loader instantiation.
     * Ensures strict SonarQube static compliance by documenting explicitly.
     */
    public DashboardController() {
        // Required default constructor for JavaFX FXML Loader
    }

    /**
     * Configures the dashboard mode to display either all reports or only the user's reports.
     * Toggles welcome containers, contextual columns, and updates global navigation links.
     *
     * @param isMyReportsView True to lock view to current user reports, false for admin overview.
     */
    public void setMyReportsMode(boolean isMyReportsView) {
        this.isMyReportsView = isMyReportsView;
        
        if (welcomeContainer != null) {
            welcomeContainer.setVisible(!isMyReportsView);
            welcomeContainer.setManaged(!isMyReportsView);
        }

        if (isMyReportsView && currentUser != null) {
            welcomeLabel.setText("My Reports - " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        }
        
        if (reportedByColumn != null) {
            reportedByColumn.setVisible(!isMyReportsView);
        }

        updateNavStyles();
        refreshMetrics();
    }

    /**
     * Updates navigation link CSS style classes to reflect the currently active view mode.
     */
    private void updateNavStyles() {
        if (navDashboard != null && navMyReports != null) {
            navDashboard.getStyleClass().removeAll(STYLE_ACTIVE);
            navMyReports.getStyleClass().removeAll(STYLE_ACTIVE);
            if (navReport != null) navReport.getStyleClass().removeAll(STYLE_ACTIVE);

            if (isMyReportsView) {
                navMyReports.getStyleClass().add(STYLE_ACTIVE);
            } else {
                navDashboard.getStyleClass().add(STYLE_ACTIVE);
            }
        }
    }

    /**
     * FXML Lifecycle method triggered automatically after FXML nodes are loaded.
     * Configures navigation tab classes, drop-down filters, double-click row triggers,
     * and dynamic table cell styling factories.
     */
    @FXML
    public void initialize() {
        updateNavStyles();
        setupFilters();
        setupTableInteraction();
        configureTableColumns();
    }

    /**
     * Prepopulates drop-down sizes and registers action listeners for page limits.
     */
    private void setupFilters() {
        if (statusFilter != null) {
            statusFilter.setItems(FXCollections.observableArrayList("All", "Lost", "Found", "Claimed"));
        }
        
        if (pageSizeComboBox != null) {
            pageSizeComboBox.setItems(FXCollections.observableArrayList(10, 25, 50, 100));
            pageSizeComboBox.setValue(10);
            pageSizeComboBox.setOnAction(_ -> {
                pageSize = pageSizeComboBox.getValue();
                totalPages = (int) Math.ceil((double) allFilteredDtos.size() / pageSize);
                if (totalPages == 0) totalPages = 1;
                currentPage = 1;
                updatePaginatedTable();
            });
        }
        
        if (searchButton != null) {
            org.example.lfrs_group_4_oop.util.AnimationUtils.makeTactile(searchButton);
        }
    }

    /**
     * Registers mouse click handlers for TableView rows.
     * double-clicking an active row opens the item detailed details window.
     */
    private void setupTableInteraction() {
        if (itemTable != null) {
            itemTable.setRowFactory(tv -> {
                TableRow<ItemDisplayDto> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        ItemDisplayDto rowData = row.getItem();
                        // CHANGED 'selectedItem' to 'rowData'
                        SceneManager.showItemDetails(rowData, currentUser);
                    }
                });
                return row;
            });
        }
    }

    /**
     * Configures cell styling factories to customize column renderings (e.g. status badges).
     */
    private void configureTableColumns() {
        if (titleColumn != null) {
            titleColumn.setCellFactory(_ -> createTitleCell());
        }

        if (statusColumn != null) {
            statusColumn.setCellFactory(_ -> createStatusCell());
        }
        
        if (dateColumn != null) {
            dateColumn.setCellFactory(_ -> createDateCell());
        }
    }

    /**
     * Creates custom cells with bold text styling for the item title column.
     *
     * @return TableCell configured with title style.
     */
    private TableCell<ItemDisplayDto, String> createTitleCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: -fx-color-text-primary;");
                }
            }
        };
    }

    /**
     * Creates custom cells configured with dynamic pill badges for the item status column.
     *
     * @return TableCell displaying a colored status badge.
     */
    private TableCell<ItemDisplayDto, String> createStatusCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.setStyle("-fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 12;");
                    applyStatusBadgeStyle(badge, item);
                    setGraphic(badge);
                    setText(null);
                }
            }
        };
    }

    /**
     * Applies dynamic CSS background fills to status badges based on the item state.
     *
     * @param badge  The label representing the status badge.
     * @param status The string representing the status value (e.g., Lost, Found, Claimed).
     */
    private void applyStatusBadgeStyle(Label badge, String status) {
        String baseStyle = badge.getStyle();
        if ("Lost".equalsIgnoreCase(status)) {
            badge.setStyle(baseStyle + "-fx-background-color: -fx-brand-red; -fx-text-fill: white;");
        } else if ("Found".equalsIgnoreCase(status)) {
            badge.setStyle(baseStyle + "-fx-background-color: -fx-status-found; -fx-text-fill: white;");
        } else if ("Claimed".equalsIgnoreCase(status)) {
            badge.setStyle(baseStyle + "-fx-background-color: -fx-status-claimed; -fx-text-fill: white;");
        } else {
            badge.setStyle(baseStyle + "-fx-background-color: -fx-brand-yellow; -fx-text-fill: white;");
        }
    }

    /**
     * Creates custom cell styling factories for the date column.
     *
     * @return TableCell configured with date formatting style.
     */
    private TableCell<ItemDisplayDto, String> createDateCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: -fx-color-text-secondary;");
                }
            }
        };
    }

    /**
     * Injects dependencies and initializes the active dashboard view.
     * 
     * @param reportService      Business logic service handles trends and dashboard counts.
     * @param itemRepository     Repository coordinating database writes and filters.
     * @param categoryRepository Repository coordinating categories search.
     * @param itemDataAggregator Aggregator helper creating display DTO elements.
     * @param currentUser        The currently active session user.
     */
    public void setDependencies(ReportService reportService, 
                                ItemRepository itemRepository,
                                CategoryRepository categoryRepository,
                                ItemDataAggregator itemDataAggregator,
                                User currentUser) {
        this.reportService = reportService;
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.itemDataAggregator = itemDataAggregator;
        this.currentUser = currentUser;

        org.example.lfrs_group_4_oop.util.ImageUtils.loadHeaderAvatar(headerAvatar, currentUser);
        setupView();
        populateFilters();
    }

    /**
     * Populates ComboBox filter options (Categories, Years, and Month details) from repositories.
     */
    private void populateFilters() {
        if (categoryFilter != null && categoryRepository != null) {
            List<Category> categories = categoryRepository.findAll();
            Category allCategory = new Category(null, "All Categories");
            categories.addFirst(allCategory);
            categoryFilter.setItems(FXCollections.observableArrayList(categories));
            
            categoryFilter.setConverter(new StringConverter<>() {
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
        }

        if (yearFilter != null) {
            int currentYear = Year.now().getValue();
            yearFilter.getItems().add(null);
            for (int i = currentYear; i >= 2000; i--) {
                yearFilter.getItems().add(i);
            }
        }

        if (monthFilter != null) {
            monthFilter.getItems().add(null);
            for (java.time.Month month : java.time.Month.values()) {
                String name = month.name();
                monthFilter.getItems().add(name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());
            }
        }
    }

    /**
     * Sets greeting text and triggers search query refresh.
     */
    private void setupView() {
        if (currentUser == null) return;
        welcomeLabel.setText("Welcome, " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        refreshMetrics();
    }

    /**
     * Clears all filters, resets pages, and refreshes database listings.
     */
    @FXML
    public void refreshMetrics() {
        if (searchField != null) searchField.clear();
        if (statusFilter != null) statusFilter.setValue(null);
        if (categoryFilter != null) categoryFilter.getSelectionModel().selectFirst();
        if (yearFilter != null) yearFilter.setValue(null);
        if (monthFilter != null) monthFilter.setValue(null);

        onPerformSearchClick();
    }

    /**
     * Overwrites Overview cards counts with updated metric integers.
     *
     * @param metrics Container class displaying overview totals.
     */
    private void updateOverviewUI(org.example.lfrs_group_4_oop.dto.DashboardMetrics metrics) {
        if (totalReportsLabel != null) totalReportsLabel.setText(String.valueOf(metrics.total()));
        if (lostItemsLabel != null) lostItemsLabel.setText(String.valueOf(metrics.lost()));
        if (foundItemsLabel != null) foundItemsLabel.setText(String.valueOf(metrics.found()));
        if (claimedLabel != null) claimedLabel.setText(String.valueOf(metrics.claimed()));
        if (unclaimedLabel != null) unclaimedLabel.setText(String.valueOf(metrics.unclaimed()));
    }

    /**
     * FXML handler that toggles the collapsible filter panel visibility.
     */
    @FXML
    public void toggleFilterPanel() {
        if (filterPanel != null) {
            boolean isVisible = filterPanel.isVisible();
            filterPanel.setVisible(!isVisible);
            filterPanel.setManaged(!isVisible);
        }
    }

    /**
     * FXML handler that performs active search using keyword inputs and Combo-Box filters.
     */
    @FXML
    public void onPerformSearchClick() {
        if (itemRepository == null || itemDataAggregator == null || itemTable == null) return;

        String keyword = searchField.getText();
        String selectedStatus = statusFilter != null ? statusFilter.getValue() : null;
        
        Integer categoryId = null;
        if (categoryFilter != null && categoryFilter.getValue() != null && categoryFilter.getValue().getId() != null) {
            categoryId = categoryFilter.getValue().getId();
        }

        Integer year = yearFilter != null ? yearFilter.getValue() : null;
        Integer month = null;
        if (monthFilter != null && monthFilter.getValue() != null) {
            month = java.time.Month.valueOf(monthFilter.getValue().toUpperCase()).getValue();
        }

        try {
            Integer reporterId = isMyReportsView ? currentUser.getId() : null;
            List<Item> items = itemRepository.searchWithFilters(keyword, selectedStatus, categoryId, year, month, reporterId);
            
            if (reportService != null) {
                org.example.lfrs_group_4_oop.dto.DashboardMetrics metrics = reportService.calculateMetrics(items);
                updateOverviewUI(metrics);
            }

            List<ItemDisplayDto> dtos = itemDataAggregator.aggregateList(items);
            allFilteredDtos = dtos;
            totalPages = (int) Math.ceil((double) allFilteredDtos.size() / pageSize);
            if (totalPages == 0) totalPages = 1;
            currentPage = 1;
            updatePaginatedTable();

            loadTrends();
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Error performing search", e);
        }
    }

    /**
     * Fetches trends metrics from services.
     */
    private void loadTrends() {
        if (reportService == null) return;
        try {
            Integer reporterId = isMyReportsView ? currentUser.getId() : null;
            TrendMetrics trends = reportService.calculateTrends(reporterId);
            updateTrendUI(trends);
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.WARNING, "Could not load trend data", e);
        }
    }

    /**
     * Updates Trend indicator styling and value labels.
     *
     * @param trends The trends metrics object.
     */
    private void updateTrendUI(TrendMetrics trends) {
        applyTrend(totalTrendLabel, trends.totalTrend());
        applyTrend(lostTrendLabel, trends.lostTrend());
        applyTrend(foundTrendLabel, trends.foundTrend());
        applyTrend(claimedTrendLabel, trends.claimedTrend());
        applyTrend(unclaimedTrendLabel, trends.unclaimedTrend());
    }

    /**
     * Applies styling classes to trends tags (e.g. green for upward founds).
     *
     * @param label The trend display label.
     * @param trend The calculated trends indicator metrics wrapper.
     */
    private void applyTrend(Label label, StatusTrend trend) {
        if (label == null) return;
        label.setText(trend.toDisplayString());
        label.getStyleClass().removeAll("trend-up", "trend-down", "trend-neutral");
        label.getStyleClass().add(trend.getStyleClass());
    }

    /**
     * FXML handler that redirects the user to the active dashboard.
     */
    @FXML
    public void onDashboardClick() {
        SceneManager.showDashboard(currentUser);
    }

    /**
     * FXML handler that redirects the user to My Reports view.
     */
    @FXML
    public void onMyReportsClick() {
        SceneManager.showMyReports();
    }

    @FXML
    public void onGalleryClick() {
        SceneManager.showGallery(currentUser); // Passing 'currentUser' ensures the nav bar avatar stays loaded!
    }


    /**
     * FXML handler that opens the report creation panel.
     */
    @FXML
    public void onReportClick() {
        try {
            LOGGER.info("Debug: Report button clicked in DashboardController");
            SceneManager.showReport();
        } catch (Exception e) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Could not open Report page");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void onMapClick() {
        SceneManager.showMap(currentUser);
    }

    /**
     * FXML handler that redirects the session to the login screen.
     */
    @FXML
    protected void onLogoutClick() {
        SceneManager.showLogin();
    }

    /**
     * FXML handler that opens the Profile view.
     */
    @FXML
    public void onProfileClick() {
        SceneManager.showProfile();
    }

    /**
     * Computes the subset page slice based on active pageIndex and populates the TableView.
     * Toggles enabling states of pagination controls (First, Prev, Next, Last).
     */
    private void updatePaginatedTable() {
        int fromIndex = (currentPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, allFilteredDtos.size());
        
        List<ItemDisplayDto> pageItems;
        if (fromIndex < allFilteredDtos.size()) {
            pageItems = allFilteredDtos.subList(fromIndex, toIndex);
        } else {
            pageItems = java.util.Collections.emptyList();
        }
        
        itemTable.setItems(FXCollections.observableArrayList(pageItems));
        
        if (pageLabel != null) {
            pageLabel.setText(String.format("Page %d of %d", currentPage, totalPages));
        }
        if (totalItemsLabel != null) {
            totalItemsLabel.setText("Total Records: " + allFilteredDtos.size());
        }
        
        if (firstPageButton != null) firstPageButton.setDisable(currentPage == 1);
        if (prevPageButton != null) prevPageButton.setDisable(currentPage == 1);
        if (nextPageButton != null) nextPageButton.setDisable(currentPage == totalPages);
        if (lastPageButton != null) lastPageButton.setDisable(currentPage == totalPages);
    }

    /**
     * FXML handler that jumps back to the first page in pagination.
     */
    @FXML
    public void onFirstPageClick() {
        if (currentPage > 1) {
            currentPage = 1;
            updatePaginatedTable();
        }
    }

    /**
     * FXML handler that decrements page index by 1.
     */
    @FXML
    public void onPrevPageClick() {
        if (currentPage > 1) {
            currentPage--;
            updatePaginatedTable();
        }
    }

    /**
     * FXML handler that increments page index by 1.
     */
    @FXML
    public void onNextPageClick() {
        if (currentPage < totalPages) {
            currentPage++;
            updatePaginatedTable();
        }
    }

    /**
     * FXML handler that jumps to the final page in pagination.
     */
    @FXML
    public void onLastPageClick() {
        if (currentPage < totalPages) {
            currentPage = totalPages;
            updatePaginatedTable();
        }
    }
}