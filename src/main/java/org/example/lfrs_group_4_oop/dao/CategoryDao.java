package org.example.lfrs_group_4_oop.dao;

import org.example.lfrs_group_4_oop.entity.Category;
import org.example.lfrs_group_4_oop.exception.DatabaseException;
import org.example.lfrs_group_4_oop.repository.CategoryRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete JDBC SQLite implementation of the {@link CategoryRepository} interface.
 * This class handles transactional database persistence for item categories,
 * utilizing the explicit column projection pattern (avoiding SELECT *) to prevent SQL vulnerabilities,
 * structured JDBC try-with-resources blocks to guarantee connection/statement closing,
 * and wrapping underlying {@link SQLException} errors into system {@link DatabaseException}s.
 */
public class CategoryDao extends BaseDao implements CategoryRepository {

    /**
     * Default constructor required for instantiation.
     */
    public CategoryDao() {
        super();
    }

    /**
     * Persists a new category record into the SQLite database.
     * Automatically retrieves and binds the generated auto-increment primary key ID to the entity.
     *
     * @param category The category entity to save.
     * @throws DatabaseException If a low-level SQLite database error occurs during query execution.
     */
    @Override
    public void save(Category category) {
        String sql = "INSERT INTO categories (category_name) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, category.getCategoryName());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error saving category: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing category record in the database.
     *
     * @param category The category entity to update, identified by its primary key ID.
     * @throws DatabaseException If a database error occurs during query execution.
     */
    @Override
    public void update(Category category) {
        String sql = "UPDATE categories SET category_name = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category.getCategoryName());
            pstmt.setInt(2, category.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating category: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a category record by its primary key ID.
     * Note: This method is primarily used in unit testing routines to clean up transactional data.
     *
     * @param id The primary key ID of the category record to delete.
     * @throws DatabaseException If a database error occurs during query execution.
     */
    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting category: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a category record by its primary key ID.
     *
     * @param id The primary key ID of the category.
     * @return The populated {@link Category} entity, or {@code null} if no matching record is found.
     * @throws DatabaseException If a database error occurs during query execution.
     */
    @Override
    public Category findById(Integer id) {
        String sql = "SELECT id, category_name FROM categories WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategory(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding category by id: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Lists all category records persisted in the categories table.
     *
     * @return A list containing all {@link Category} entities, or an empty list if no categories exist.
     * @throws DatabaseException If a database error occurs during query execution.
     */
    @Override
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT id, category_name FROM categories";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error listing all categories: " + e.getMessage(), e);
        }
        return categories;
    }

    /**
     * Retrieves a category record by its exact category name.
     *
     * @param categoryName The unique name of the category.
     * @return The populated {@link Category} entity, or {@code null} if no matching record is found.
     * @throws DatabaseException If a database error occurs during query execution.
     */
    @Override
    public Category findByCategoryName(String categoryName) {
        String sql = "SELECT id, category_name FROM categories WHERE category_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, categoryName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategory(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding category by name: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Maps the active row of a database {@link ResultSet} to a new {@link Category} POJO.
     *
     * @param rs The ResultSet cursors.
     * @return The mapped {@link Category} entity.
     * @throws SQLException If an error occurs while accessing ResultSet columns.
     */
    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getInt("id"));
        category.setCategoryName(rs.getString("category_name"));
        return category;
    }
}
