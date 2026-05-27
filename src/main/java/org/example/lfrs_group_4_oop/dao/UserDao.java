package org.example.lfrs_group_4_oop.dao;

import org.example.lfrs_group_4_oop.entity.User;
import org.example.lfrs_group_4_oop.exception.DatabaseException;
import org.example.lfrs_group_4_oop.repository.UserRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLite implementation of the UserRepository.
 */
public class UserDao extends BaseDao implements UserRepository {

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (name, email, password, role, student_no, program, section, avatar_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRole());
            pstmt.setString(5, user.getStudentNo());
            pstmt.setString(6, user.getProgram());
            pstmt.setString(7, user.getSection());
            pstmt.setString(8, user.getAvatarPath());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error saving user: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, password = ?, role = ?, student_no = ?, program = ?, section = ?, avatar_path = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRole());
            pstmt.setString(5, user.getStudentNo());
            pstmt.setString(6, user.getProgram());
            pstmt.setString(7, user.getSection());
            pstmt.setString(8, user.getAvatarPath());
            pstmt.setInt(9, user.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating user: " + e.getMessage(), e);
        }
    }

    // This code is for testing purposes only as it will not be implemented in the production.
    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting user: " + e.getMessage(), e);
        }
    }

    @Override
    public User findById(Integer id) {
        String sql = "SELECT id, name, email, password, role, student_no, program, section, avatar_path FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding user by id: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, email, password, role, student_no, program, section, avatar_path FROM users";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error listing all users: " + e.getMessage(), e);
        }
        return users;
    }

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT id, name, email, password, role, student_no, program, section, avatar_path FROM users WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding user by email: " + e.getMessage(), e);
        }
        return null;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setStudentNo(rs.getString("student_no"));
        user.setProgram(rs.getString("program"));
        user.setSection(rs.getString("section"));
        user.setAvatarPath(rs.getString("avatar_path"));
        return user;
    }
}
