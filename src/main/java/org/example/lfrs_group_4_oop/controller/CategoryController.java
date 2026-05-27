package org.example.lfrs_group_4_oop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.lfrs_group_4_oop.entity.Category;
import org.example.lfrs_group_4_oop.repository.CategoryRepository;

/**
 * Facilitates the categorization of lost and found items.
 * This controller provides administrative capabilities to define new item categories
 * (e.g., Electronics, Documents) and view the existing catalog.
 */
@SuppressWarnings("unused")
public class CategoryController {

    @FXML
    private TextField categoryNameField;

    @FXML
    private ListView<Category> categoryList;

    @FXML
    private Label statusLabel;

    private CategoryRepository categoryRepository;

    /**
     * Injects the required data repository.
     * 
     * @param categoryRepository The repository for category persistence.
     */
    public void setDependencies(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @FXML
    public void initialize() {
        refreshList();
    }

    /**
     * Handles the addition of a new category to the system.
     */
    @FXML
    protected void onAddCategoryClick() {
        try {
            Category category = new Category();
            category.setCategoryName(categoryNameField.getText());
            categoryRepository.save(category);
            statusLabel.setText("Category added.");
            refreshList();
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Synchronizes the UI list with the current database state.
     */
    private void refreshList() {
        if (categoryList != null && categoryRepository != null) {
            categoryList.getItems().setAll(categoryRepository.findAll());
        }
    }
}
