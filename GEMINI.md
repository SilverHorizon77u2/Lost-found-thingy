# Project Instructions: LFRS Group 4 OOP

## Foundational Mandate
- **Documentation Alignment:** The instructions in this file must ALWAYS follow and align with the technical standards, architectural patterns, and development workflows defined in the `docs/` folder (e.g., `BACKEND_DEVELOPER_GUIDE.md`, `FRONTEND_DEVELOPER_GUIDE.md`). The `docs/` folder is the primary source of truth for the project.
- **Cursor Codex Rules Alignment:** ALWAYS follow and align with the AI-specific instructions, guidelines, and workflows defined in the `cursor-codex-rules/` folder and its files (e.g., `llm-coding-rules/` and `skills/`).

## Project Overview
This is a modular JavaFX application developed using Java 24 and Maven. It follows the Java Platform Module System (JPMS).

## Technical Stack
- **Language:** Java 24
- **Framework:** JavaFX 21.0.6
- **Database:** SQLite (Local Persistence)
- **Persistence:** JDBC
- **Build System:** Maven (using Maven Wrapper `./mvnw`)
- **Architecture:** 6-Layer Modular Architecture

## Project Structure
- **Module Name:** `org.example.lfrs_group_4_oop`
- **Base Package:** `org.example.lfrs_group_4_oop`
- **Layered Architecture:**
  - `.controller`: Bridge logic between UI and backend (Dependency Injection pattern). Includes `DashboardController`, `ReportController`, `ItemDetailController`, and `UserController` (Profile management).
  - `.service`: High-level business logic (e.g., `ItemMatcherService`, `ItemDataAggregator`, `ReportService`).
  - `.model`: Stateful business rules and workflow coordination (e.g., `ItemModel`, `UserModel` with `updateProfile`).
  - `.validator`: Stateless input validation rules.
  - `.repository`: Interface-based persistence contracts for loose coupling.
  - `.dao`: Concrete JDBC/SQLite implementations of repositories. Uses `INSERT OR REPLACE` for reliable seeding.
  - `.entity`: POJOs representing database tables. `User` entity includes `studentNo`, `program`, and `section`.
  - `.dto`: Data Transfer Objects for UI optimization. Includes `ItemDisplayDto`, `MonthlyStatusCount` (group query mapping), `StatusTrend` (trend calculation wrapper), and `TrendMetrics` (MoM metrics record).
  - `.exception`: Custom system exceptions (e.g., `DatabaseException`).
  - `.database`: `DatabaseManager.java` and `lfrs.db`.
- **Main Application:** `src/main/java/org/example/lfrs_group_4_oop/HelloApplication.java`
- **Resources:**
  - `fxml/`: Subfolders organized by domain (auth, item, category, claim, claimant, report, user).
  - `styles/`: CSS files.
  - `images/`: UI assets.

## Development Workflows
- **Database:** Tables are auto-initialized and seeded on startup via `DatabaseManager`. Includes automated schema verification and seeding to ensure columns like 'title' exist and are populated. Use `try-with-resources` for all JDBC calls. All DAOs must use explicit column names in SELECT queries.
- **Session Management:** `SceneManager` maintains a static `currentUser` session object. Access via `SceneManager.getCurrentUser()`.
- **Navigation:** Use `SceneManager` for all view transitions. It handles FXML loading, absolute resource path resolution, and Dependency Injection. Includes a global navigation bar with a solid Red background, left-aligned branding (PUP Logo) and links, and a right-aligned interactive profile avatar.
- **Authentication UI:** Modernized Login and Signup screens featuring a professional 50/50 split-pane design. The left side hosts brand-aligned marketing content (left-aligned), while the right side contains a responsive card-based form with modern gradients, inline validation, and animations.
- **Profile Management:** Users can view and update their personal/student details via the dedicated Profile page. Transitions between read-only "View" mode and "Edit" mode. Includes support for Avatar management (direct click on picture to upload/delete with deduplicated loading logic), secure Password changes, inline password strength listener feedback, and micro-animations (fade on success, tactile shake on error).
- **Reporting:** Use the dedicated Report page for submitting new items. Form supports dual segmented modes ("I Lost an Item" / "I Found an Item") with dynamic toggle-pane transitions. Submissions default to 'Lost'. Supports photo selection via `FileChooser`, dynamic category population, and high-fidelity text-area descriptions.
- **Dashboard & My Reports:** Administrators have a comprehensive overview via the Dashboard, including dynamic in-memory overview statistics and database-driven Month-over-Month (MoM) trend indicator cards showing percentage changes. The TableView uses `CONSTRAINED_RESIZE_POLICY` for equally proportionate columns. Includes a "Reported By" column that is context-aware and hidden in the "My Reports" view.
- **Performance:** All Item data retrieval queries use SQL `LEFT JOIN` on the `users` table to fetch reporter names in a single database trip, preventing N+1 query overhead. Trend metrics calculations group counts in a single query via `strftime` for maximum efficiency.
- **Testing:** 
  - Unit/Data tests in `src/test/java`.
  - Automated E2E simulation in `src/test/java/.../integration/SystemEndToEndSimulationTest.java`.
  - Use `./mvnw test` to run all verification. All 108 tests pass.

## Coding Standards
- Adhere to standard Java naming conventions (PascalCase for classes, camelCase for methods/variables).
- Enforce strict static analysis compliance (e.g., SonarQube): keep cognitive complexity low (especially in `initialize` methods), remove unused fields/classes (no empty marker models like `BaseModel`), use deferred string concatenation in logging, and extract duplicated logic.
- Ensure all entity classes have documented constructors for SonarQube compliance.
- Use `StringConverter` for clean `ComboBox` object displays.
- Ensure all FXML files have corresponding controllers in the same package structure.
- Maintain strict type safety and avoid raw types.
- Utility classes must have private constructors to prevent instantiation.

