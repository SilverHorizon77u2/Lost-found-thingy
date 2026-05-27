package org.example.lfrs_group_4_oop.dao;

import org.example.lfrs_group_4_oop.database.DatabaseManager;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Abstract base class for all SQLite DAO implementations.
 * Provides connection handling logic.
 */
public abstract class BaseDao {

    /**
     * Helper to get a database connection.
     * @return Connection object.
     * @throws SQLException on database error.
     */
    protected Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection();
    }
}
