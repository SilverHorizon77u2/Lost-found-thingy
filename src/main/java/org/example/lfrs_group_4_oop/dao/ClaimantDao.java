package org.example.lfrs_group_4_oop.dao;

import org.example.lfrs_group_4_oop.entity.Claimant;
import org.example.lfrs_group_4_oop.exception.DatabaseException;
import org.example.lfrs_group_4_oop.repository.ClaimantRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete JDBC SQLite implementation of the {@link ClaimantRepository} interface.
 * This class coordinates database transactions for claimants (individuals receiving claimed items),
 * using explicit SQL column projections, try-with-resources parameter mappings,
 * and wrapping low-level sql exceptions into {@link DatabaseException} wrappers.
 */
public class ClaimantDao extends BaseDao implements ClaimantRepository {

    /**
     * Default constructor required for instantiation.
     */
    public ClaimantDao() {
        super();
    }

    /**
     * Saves a new claimant record into the SQLite database.
     * <p>
     * Automatically retrieves and binds the generated auto-increment primary key ID to the entity.
     * </p>
     *
     * @param claimant The claimant entity containing name and ID number to save.
     * @throws DatabaseException If a database write error occurs.
     */
    @Override
    public void save(Claimant claimant) {
        String sql = "INSERT INTO claimants (name, id_number) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, claimant.getName());
            pstmt.setString(2, claimant.getIdNumber());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    claimant.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error saving claimant: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing claimant record in the database.
     *
     * @param claimant The claimant entity to update, identified by its primary key ID.
     * @throws DatabaseException If a database error occurs.
     */
    @Override
    public void update(Claimant claimant) {
        String sql = "UPDATE claimants SET name = ?, id_number = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, claimant.getName());
            pstmt.setString(2, claimant.getIdNumber());
            pstmt.setInt(3, claimant.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating claimant: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a claimant record by its primary key ID.
     * Primary usage locked to test cleanup scripts.
     *
     * @param id The primary key ID of the claimant to delete.
     * @throws DatabaseException If a database error occurs.
     */
    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM claimants WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting claimant: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a claimant record by its primary key ID.
     *
     * @param id The primary key ID of the claimant.
     * @return The populated {@link Claimant} entity, or {@code null} if no matching record exists.
     * @throws DatabaseException If a database error occurs.
     */
    @Override
    public Claimant findById(Integer id) {
        String sql = "SELECT id, name, id_number FROM claimants WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToClaimant(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding claimant by id: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Lists all claimant records persisted in the database.
     *
     * @return A list containing all {@link Claimant} entities, or an empty list if no claimants exist.
     * @throws DatabaseException If a database error occurs.
     */
    @Override
    public List<Claimant> findAll() {
        List<Claimant> claimants = new ArrayList<>();
        String sql = "SELECT id, name, id_number FROM claimants";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                claimants.add(mapResultSetToClaimant(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error listing all claimants: " + e.getMessage(), e);
        }
        return claimants;
    }

    /**
     * Retrieves a claimant record by their exact unique ID number (e.g. Student ID or Employee ID).
     *
     * @param idNumber The unique identification number of the claimant.
     * @return The populated {@link Claimant} entity, or {@code null} if no matching record exists.
     * @throws DatabaseException If a database error occurs.
     */
    @Override
    public Claimant findByIdNumber(String idNumber) {
        String sql = "SELECT id, name, id_number FROM claimants WHERE id_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, idNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToClaimant(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding claimant by id number: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Maps the active row of a database {@link ResultSet} to a new {@link Claimant} POJO.
     *
     * @param rs The ResultSet cursors.
     * @return The mapped {@link Claimant} entity.
     * @throws SQLException If an error occurs while accessing ResultSet columns.
     */
    private Claimant mapResultSetToClaimant(ResultSet rs) throws SQLException {
        Claimant claimant = new Claimant();
        claimant.setId(rs.getInt("id"));
        claimant.setName(rs.getString("name"));
        claimant.setIdNumber(rs.getString("id_number"));
        return claimant;
    }
}
