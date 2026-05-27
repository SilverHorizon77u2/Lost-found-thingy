package org.example.lfrs_group_4_oop.dao;

import org.example.lfrs_group_4_oop.entity.Claim;
import org.example.lfrs_group_4_oop.exception.DatabaseException;
import org.example.lfrs_group_4_oop.repository.ClaimRepository;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete JDBC SQLite implementation of the {@link ClaimRepository} interface.
 * This class coordinates database transactions for item claim registries,
 * using explicit column projections, try-with-resources parameter bindings,
 * thread-safe ISO date-time formatting, and wrapping low-level sql errors into {@link DatabaseException}s.
 */
public class ClaimDao extends BaseDao implements ClaimRepository {

    /** Formatter matching the SQLite text-date storage format standard ("yyyy-MM-dd HH:mm:ss"). */
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Default constructor required for instantiation.
     */
    public ClaimDao() {
        super();
    }

    /**
     * Saves a new claim record into the SQLite database.
     * Automatically binds the generated auto-increment primary key ID to the entity.
     *
     * @param claim The claim entity containing item and claimant IDs to save.
     * @throws DatabaseException If a database write error occurs.
     */
    @Override
    public void save(Claim claim) {
        String sql = "INSERT INTO claims (item_id, claimant_id, claim_date) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setObject(1, claim.getItemId());
            pstmt.setObject(2, claim.getClaimantId());
            pstmt.setString(3, claim.getClaimDate().format(formatter));
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    claim.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error saving claim: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing claim record.
     *
     * @param claim The claim entity to update, identified by its primary key ID.
     * @throws DatabaseException If a database error occurs.
     */
    @Override
    public void update(Claim claim) {
        String sql = "UPDATE claims SET item_id = ?, claimant_id = ?, claim_date = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, claim.getItemId());
            pstmt.setObject(2, claim.getClaimantId());
            pstmt.setString(3, claim.getClaimDate().format(formatter));
            pstmt.setInt(4, claim.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating claim: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a claim record by its primary key ID.
     * Primary usage locked to test cleanup scripts.
     *
     * @param id The primary key ID of the claim to delete.
     * @throws DatabaseException If a database error occurs.
     */
    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM claims WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting claim: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a claim record by its primary key ID.
     *
     * @param id The primary key ID of the claim.
     * @return The populated {@link Claim} entity, or {@code null} if no matching record exists.
     * @throws DatabaseException If a database error occurs.
     */
    @Override
    public Claim findById(Integer id) {
        String sql = "SELECT id, item_id, claimant_id, claim_date FROM claims WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToClaim(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding claim by id: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Lists all claim records persisted in the database.
     *
     * @return A list containing all {@link Claim} entities, or an empty list if no claims exist.
     * @throws DatabaseException If a database error occurs.
     */
    @Override
    public List<Claim> findAll() {
        List<Claim> claims = new ArrayList<>();
        String sql = "SELECT id, item_id, claimant_id, claim_date FROM claims";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                claims.add(mapResultSetToClaim(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error listing all claims: " + e.getMessage(), e);
        }
        return claims;
    }

    /**
     * Retrieves all claim records associated with a specific item ID.
     *
     * @param itemId The primary key ID of the item.
     * @return A list of {@link Claim} records, or an empty list if no claims exist for the item.
     * @throws DatabaseException If a database error occurs.
     */
    @Override
    public List<Claim> findByItemId(Integer itemId) {
        List<Claim> claims = new ArrayList<>();
        String sql = "SELECT id, item_id, claimant_id, claim_date FROM claims WHERE item_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    claims.add(mapResultSetToClaim(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding claims by item id: " + e.getMessage(), e);
        }
        return claims;
    }

    /**
     * Maps the active row of a database {@link ResultSet} to a new {@link Claim} POJO.
     *
     * @param rs The ResultSet cursors.
     * @return The mapped {@link Claim} entity.
     * @throws SQLException If an error occurs while accessing ResultSet columns.
     */
    private Claim mapResultSetToClaim(ResultSet rs) throws SQLException {
        Claim claim = new Claim();
        claim.setId(rs.getInt("id"));
        claim.setItemId(rs.getObject("item_id", Integer.class));
        claim.setClaimantId(rs.getObject("claimant_id", Integer.class));
        String dateStr = rs.getString("claim_date");
        if (dateStr != null) {
            claim.setClaimDate(LocalDateTime.parse(dateStr, formatter));
        }
        return claim;
    }
}
