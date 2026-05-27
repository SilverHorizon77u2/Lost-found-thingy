# Backend Developer Guide: Lost and Found Reporting System (LFRS)

This guide provides an overview of the backend architecture, technologies, and development workflows for the LFRS project.

## Technical Stack
- **Language:** Java 24
- **Database:** SQLite
- **Persistence:** JDBC (Java Database Connectivity)
- **Build System:** Maven
- **Architecture:** Layered (6-Layer)

## Architecture Overview
The project follows a **Layered Architecture** to ensure separation of concerns and maintainability.

### Package Structure
- `org.example.lfrs_group_4_oop.database`: Contains `DatabaseManager.java`. Uses **Batch Processing** and `INSERT OR IGNORE` with explicit IDs for reliable seeding and referential integrity, preventing user profiles, passwords, and avatar path resets on startup. Includes automated schema verification to handle missing columns (e.g., 'title') during initialization.
- `org.example.lfrs_group_4_oop.entity`: POJOs representing database tables. Includes expanded `User` entity with `studentNo`, `program`, `section`, and `avatarPath`. All entities must have documented constructors to satisfy static analysis requirements.
- `org.example.lfrs_group_4_oop.dao`: SQL implementation for CRUD operations. **Strict Rule:** Use explicit column names in SELECT queries (No `SELECT *`). Ensures consistency for columns like 'title' across all queries.
- `org.example.lfrs_group_4_oop.repository`: Interfaces defining DAO contracts.
- `org.example.lfrs_group_4_oop.validator`: Stateless input validation. Utility classes must have private constructors.
- `org.example.lfrs_group_4_oop.model`: Business logic and workflow coordination (e.g., `UserModel.registerUser`, `UserModel.updateProfile`, `UserModel.updatePassword`, `ItemModel.registerItem`). **Note:** Empty marker models (like `BaseModel`) are avoided to comply with static analysis.
- `org.example.lfrs_group_4_oop.service`: High-level services implemented as **Java 24 Records** (e.g., `ItemMatcherService`, `ItemDataAggregator`, `ReportService`). Uses modern Java Streams (`.toList()`).
- `org.example.lfrs_group_4_oop.controller`: Bridge logic between UI and models. Maintain low cognitive complexity in `initialize` methods by delegating logic to models or services.
- `org.example.lfrs_group_4_oop.dto`: Data Transfer Objects optimized for the UI. Includes `ItemDisplayDto` (implemented using the **Builder Pattern**), `MonthlyStatusCount` (maps a single database row of month + status count), `StatusTrend` (trend calculation wrapper for percent change and UI styling), and `TrendMetrics` (aggregated MoM container for all stats).
- `org.example.lfrs_group_4_oop.exception`: Custom exceptions. Use `DatabaseException` to wrap low-level JDBC failures.

## Key Functional Capabilities
- **Authentication:** Managed by `SignupController` (registration) and `LoginController` (credential verification).
- **User Roles:** New users default to "Standard User". Manual administrative creation is disabled.
- **Profile Management:** `UserModel.updateProfile` handles the validation and persistence of user/student details.
- **Reporting:** `ItemModel.registerItem` handles the business logic for submitting lost/found reports, including category validation. All new submissions default to the 'Lost' status.
- **Metrics & Overview:** `ReportService` provides dynamic in-memory statistics calculation from filtered item lists, avoiding redundant database `COUNT` queries. It also computes month-over-month trend metrics using `calculateTrends(Integer reporterId)` which executes a single optimized SQL aggregation query via `ItemRepository.getMonthlyStatusCounts` (grouped by `strftime('%Y-%m', date_reported)`) to prevent N+1 query overhead. Additionally, a high-performance in-memory TableView pagination system segments lists dynamically while maintaining global aggregation for all overview metrics.
- **Matching Engine:** `ItemMatcherService` identifies potential matches using keyword analysis and category filtering.
- **Search Engine:** Keyword-based searching and multi-parameter filtering (including `reporterId` for "My Reports") via `ItemRepository`. Optimized using SQL `LEFT JOIN` on `users` table to retrieve `reporter_name` efficiently.

## Database Management
### SQLite Integration
Local persistence file: `src/main/java/org/example/lfrs_group_4_oop/database/lfrs.db`.
All `Item` retrieval queries must join the `users` table to populate the transient `reporterName` field.

### Session Management
- **SceneManager:** Maintains a static `currentUser` session object. Use `SceneManager.getCurrentUser()` to access the logged-in user across different controllers.

### Best Practices
- **Try-With-Resources:** Mandatory for all JDBC objects (Connection, Statement, ResultSet).
- **Explicit Columns:** Never use `SELECT *` in production code.
- **Referential Integrity:** During seeding or testing, always ensure parent entities (Users/Categories) have fixed IDs or are verified before inserting child entities (Items). Use `PRAGMA foreign_keys = OFF` during data cleanup in tests.
- **Error Handling:** Always wrap `SQLException` in the custom `DatabaseException`.

## Testing Strategy
1.  **Unit Tests:** isolated logic verification.
2.  **Data Tests:** Verification using live SQLite. Ensure referential integrity by disabling foreign keys during setup/cleanup.
3.  **Simulation:** Full lifecycle testing in `SystemEndToEndSimulationTest.java`.

Run all tests: `./mvnw test` (All 110 tests pass successfully)
