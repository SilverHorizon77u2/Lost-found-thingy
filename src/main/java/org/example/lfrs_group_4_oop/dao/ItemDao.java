package org.example.lfrs_group_4_oop.dao;

import org.example.lfrs_group_4_oop.dto.MonthlyStatusCount;
import org.example.lfrs_group_4_oop.entity.Item;
import org.example.lfrs_group_4_oop.exception.DatabaseException;
import org.example.lfrs_group_4_oop.repository.ItemRepository;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) implementation for the {@link ItemRepository}.
 * This class provides SQLite-specific database operations for {@link Item} entities.
 * It handles the creation, retrieval, updating, and deletion of lost and found items,
 * along with robust mapping between JDBC {@link ResultSet} and Java objects.
 *
 * Thread Safety: The methods use local {@link Connection} objects retrieved via the 
 * {@link BaseDao#getConnection()} method, making individual method invocations thread-safe.
 */
public class ItemDao extends BaseDao implements ItemRepository {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private static final String BASE_SELECT = "SELECT items.id, items.title, items.description, items.image_path, " +
            "items.date_reported, items.status, items.location, items.category_id, items.reporter_id, " +
            "users.name AS reporter_name FROM items LEFT JOIN users ON items.reporter_id = users.id";

    /**
     * Persists a new {@link Item} to the database.
     * If the insertion is successful, the auto-generated database ID is retrieved 
     * and populated back into the provided {@link Item} object.
     *
     * @param item the {@link Item} entity to save; must not be null.
     * @throws DatabaseException if a database access error occurs or the SQL execution fails.
     */
    @Override
    public void save(Item item) {
        String sql = "INSERT INTO items (title, description, image_path, date_reported, status, location, category_id, reporter_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, item.getTitle());
            pstmt.setString(2, item.getDescription());
            pstmt.setString(3, item.getImagePath());
            pstmt.setString(4, item.getDateReported().format(formatter));
            pstmt.setString(5, item.getStatus());
            pstmt.setString(6, item.getLocation());
            pstmt.setObject(7, item.getCategoryId());
            pstmt.setObject(8, item.getReporterId());
            pstmt.executeUpdate();

            // Retrieve and assign the auto-generated primary key
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error saving item: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing {@link Item} record in the database.
     * The item is identified by its primary key {@code id}. All fields (except the ID) 
     * will be overwritten with the values present in the provided object.
     *
     * @param item the {@link Item} entity containing updated values; must have a valid ID.
     * @throws DatabaseException if a database access error occurs during the update.
     */
    @Override
    public void update(Item item) {
        String sql = "UPDATE items SET title = ?, description = ?, image_path = ?, date_reported = ?, status = ?, location = ?, category_id = ?, reporter_id = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, item.getTitle());
            pstmt.setString(2, item.getDescription());
            pstmt.setString(3, item.getImagePath());
            pstmt.setString(4, item.getDateReported().format(formatter));
            pstmt.setString(5, item.getStatus());
            pstmt.setString(6, item.getLocation());
            pstmt.setObject(7, item.getCategoryId());
            pstmt.setObject(8, item.getReporterId());
            pstmt.setInt(9, item.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating item: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes an {@link Item} from the database by its ID.
     * This code is primarily for testing purposes as hard deletion
     * is generally avoided in production environments in favor of soft deletion.
     *
     * @param id the unique identifier of the item to delete.
     * @throws DatabaseException if an error occurs while executing the delete query.
     */
    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM items WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting item: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves an {@link Item} by its unique identifier.
     *
     * @param id the primary key of the item to find.
     * @return the {@link Item} if found, or {@code null} if no record matches the given ID.
     * @throws DatabaseException if a data access error occurs during the lookup.
     */
    @Override
    public Item findById(Integer id) {
        String sql = BASE_SELECT + " WHERE items.id = ?";
        List<Item> results = executeQuery(sql, id);
        return results.isEmpty() ? null : results.getFirst();
    }

    /**
     * Retrieves all recorded items from the database.
     *
     * @return a list of all {@link Item} objects. Returns an empty list if the table is empty.
     * @throws DatabaseException if an error occurs while reading the records.
     */
    @Override
    public List<Item> findAll() {
        return executeQuery(BASE_SELECT);
    }

    /**
     * Searches for items whose description partially matches the provided keyword.
     * The search is case-insensitive (dependent on SQLite's LIKE behavior) and 
     * surrounds the keyword with SQL wildcards ({@code %keyword%}).
     *
     * @param keyword the substring to search for within the item descriptions.
     * @return a list of matching {@link Item} records.
     * @throws DatabaseException if a database access error occurs during the search.
     */
    @Override
    public List<Item> searchByDescription(String keyword) {
        String sql = BASE_SELECT + " WHERE items.description LIKE ?";
        return executeQuery(sql, "%" + keyword + "%");
    }

    /**
     * Retrieves items filtered strictly by their current status (e.g., "Lost", "Found", "Claimed").
     *
     * @param status the exact status string to match.
     * @return a list of {@link Item} objects that match the specified status.
     * @throws DatabaseException if an error occurs executing the query.
     */
    @Override
    public List<Item> findByStatus(String status) {
        String sql = BASE_SELECT + " WHERE items.status = ?";
        return executeQuery(sql, status);
    }

    /**
     * Executes a dynamic multi-parameter search against the items table.
     * This method dynamically constructs an SQL query based on the presence of the provided parameters.
     * Parameters that are {@code null} or empty (in the case of Strings) are ignored, effectively applying
     * an unconstrained filter for that specific dimension.
     *
     * @param keyword    a substring search parameter for the description field.
     * @param status     an exact match parameter for the status field. "All" or blank values are ignored.
     * @param categoryId an exact match parameter for the associated category ID.
     * @param year       a year filter to match against the item's reported date.
     * @param month      a month filter (1-12) to match against the item's reported date.
     * @param reporterId an exact match parameter to limit results to a specific user's reports.
     * @return a filtered list of {@link Item} objects meeting all provided criteria.
     * @throws DatabaseException if a database access error occurs during dynamic query construction or execution.
     */
    @Override
    public List<Item> searchWithFilters(String keyword, String status, Integer categoryId, Integer year, Integer month, Integer reporterId) {
        StringBuilder sql = new StringBuilder(BASE_SELECT).append(" WHERE 1=1");
        List<Object> params = new ArrayList<>();

        // Append optional filters dynamically
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND items.description LIKE ?");
            params.add("%" + keyword + "%");
        }
        if (status != null && !status.equals("All") && !status.isBlank()) {
            sql.append(" AND items.status = ?");
            params.add(status);
        }
        if (categoryId != null) {
            sql.append(" AND items.category_id = ?");
            params.add(categoryId);
        }
        if (year != null) {
            sql.append(" AND strftime('%Y', items.date_reported) = ?");
            params.add(String.valueOf(year));
        }
        if (month != null) {
            sql.append(" AND strftime('%m', items.date_reported) = ?");
            params.add(String.format("%02d", month));
        }
        if (reporterId != null) {
            sql.append(" AND items.reporter_id = ?");
            params.add(reporterId);
        }

        return executeQuery(sql.toString(), params.toArray());
    }

    /**
     * Executes a query and maps the results to a list of Items.
     */
    private List<Item> executeQuery(String sql, Object... params) {
        List<Item> items = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToItem(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error executing query: " + e.getMessage(), e);
        }
        return items;
    }

    /**
     * Updates only the status field of a specific item.
     * This is a lightweight, targeted update operation used primarily by administrators 
     * during the claim workflow or when a lost item is found.
     *
     * @param id     the primary key of the item to update.
     * @param status the new status to apply (e.g., "Claimed").
     * @throws DatabaseException if an error occurs executing the update query.
     */
    @Override
    public void updateStatus(Integer id, String status) {
        String sql = "UPDATE items SET status = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating item status: " + e.getMessage(), e);
        }
    }

    /**
     * Helper method to map the current row of a {@link ResultSet} to an {@link Item} entity.
     * This method encapsulates the column-to-field mapping logic and properly handles
     * nullable foreign key primitives (like category_id and reporter_id) to avoid 
     * unintentional assignment of '0' when the database field is actually NULL.
     *
     * @param rs the {@link ResultSet} pointing to the current row.
     * @return a fully populated {@link Item} instance.
     * @throws SQLException if a column label is not valid or a database access error occurs.
     */
    private Item mapResultSetToItem(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(rs.getInt("id"));
        item.setTitle(rs.getString("title"));
        item.setDescription(rs.getString("description"));
        item.setImagePath(rs.getString("image_path"));
        
        String dateStr = rs.getString("date_reported");
        if (dateStr != null) {
            item.setDateReported(LocalDateTime.parse(dateStr, formatter));
        }
        
        item.setStatus(rs.getString("status"));
        item.setLocation(rs.getString("location"));

        // Handle primitive wrappers for potentially null foreign keys
        int categoryId = rs.getInt("category_id");
        if (!rs.wasNull()) {
            item.setCategoryId(categoryId);
        }

        int reporterId = rs.getInt("reporter_id");
        if (!rs.wasNull()) {
            item.setReporterId(reporterId);
        }

        try {
            item.setReporterName(rs.getString("reporter_name"));
        } catch (SQLException e) {
            // Ignore if column doesn't exist (e.g. in some raw queries during tests)
        }

        return item;
    }

    @Override
    public List<MonthlyStatusCount> getMonthlyStatusCounts(
            String currentMonthKey, String previousMonthKey, Integer reporterId) {
        StringBuilder sql = new StringBuilder(
                "SELECT strftime('%Y-%m', date_reported) AS month_key, status, COUNT(*) AS count " +
                "FROM items WHERE strftime('%Y-%m', date_reported) IN (?, ?)");
        List<Object> params = new ArrayList<>();
        params.add(currentMonthKey);
        params.add(previousMonthKey);

        if (reporterId != null) {
            sql.append(" AND reporter_id = ?");
            params.add(reporterId);
        }
        sql.append(" GROUP BY month_key, status");

        List<MonthlyStatusCount> results = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new MonthlyStatusCount(
                            rs.getString("month_key"),
                            rs.getString("status"),
                            rs.getLong("count")));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Error fetching monthly status counts: " + e.getMessage(), e);
        }
        return results;
    }
}