# Frontend Developer Guide: Lost and Found Reporting System (LFRS)

This guide provides an overview of the frontend development environment, UI technologies, and directory structure for the LFRS project.

## Technical Stack
- **Framework:** JavaFX 21
- **Layout:** FXML (XML-based language for defining UI structure)
- **Styling:** CSS (JavaFX CSS - `.css`)
- **Icons/Images:** Standard image formats (PNG, JPG) and SVG.

## Frontend Directory Structure
All frontend assets are located in the `src/main/resources` directory.

### Core Path
`src/main/resources/org/example/lfrs_group_4_oop/`

### Recommended Folder Structure
For better organization, FXML files are grouped by domain within the `/fxml` folder:
- `/fxml/auth`: `Login.fxml`, `Signup.fxml`
- `/fxml/item`: Item management and general list views.
- `/fxml/report`: `Report.fxml` for submitting new lost/found reports.
- `/fxml/user`: `Profile.fxml` for managing user account/student details.
- `/fxml/category`: Administrative category management.
- `/styles`: Store all `.css` stylesheets here.
- `/images`: Store icons, logos, and UI-specific images here.

## Development Workflows

### 1. Navigation & Global UI
- **Navigation Bar:** Redesigned with a solid Brand Red background (`#D32F2F`). The PUP logo and primary navigation links (**Dashboard**, **My Reports**, **Report**) are left-aligned. The user's profile avatar is right-aligned and interactive.
- **Authentication UI:** Features a modernized 50/50 split-pane layout for Login and Signup. 
    - **Marketing Pane:** Left-aligned section featuring high-contrast branding and descriptive copy.
    - **Form Pane:** Responsive card-based layout with modern gradients, animated transitions, and inline validation.
    - **Branding:** Consistent logo placement and professional typography.
- **Edge-to-Edge Layout:** Header and Footer use negative margins to stretch to the full window width, bypassing root padding.
- **Active States:** Links automatically highlight when active using the `.active` CSS class.
- **SceneManager:** Do not load FXML files manually in controllers. Use the `SceneManager` utility for all transitions. It maintains a static `currentUser` session to preserve user context across screens. It also provides a global **Log Out** transition which securely clears the session (`currentUser = null`) and routes the user back to the Login view.
- **Log Out Button:** Styled as a clean red-themed `.btn-outline` button in the profile card header. Hovering over it applies a smooth, tactile background highlighted red state while clicking terminates the active session.

### 2. Form Handling & Data Display
- **User Profile:** The `Profile.fxml` implements a "View/Edit" lifecycle where fields are read-only by default. Users can toggle "Edit Mode" to modify personal information. Updates trigger high-fidelity fade transitions upon success, and a tactile shake animation (`AnimationUtils.shake`) on the status feedback label upon validation errors.
- **Avatar Management:** The profile picture `ImageView` uses direct interaction via a `StackPane`. Clicking the avatar opens a `ContextMenu` to upload a new picture (via `FileChooser`) or delete the current one, persisting the absolute path to the database immediately. Uses **deduplicated loading logic** to ensure performance and reliability.
- **Password Security & Strength:** The Profile view includes a dedicated section for secure password updates requiring the current password. It features a dynamic inline password strength indicator listener displaying dynamic text and styling based on complexity: "Weak" (<6 chars, `.strength-weak`), "Fair" (<10 chars, `.strength-fair`), or "Strong" (>=10 chars, `.strength-strong`).
- **Reporting Toggles & Inputs:** The `Report.fxml` features a professional ToggleGroup segment (`btnLost` / `btnFound`) enabling users to declare their intent. Toggling "I Lost an Item" displays the standard report form, whereas "I Found an Item" collapses the form and displays user instructions to surrender the item directly to the Admin Office. The description input uses a high-fidelity multiline `TextArea` with modern custom scroll overrides and text wrap.
- **Photo Submission:** Use `javafx.stage.FileChooser` in controllers to allow users to select images. Store the absolute file path in the database.
- **TableViews & Dashboards:** The main Dashboard and "My Reports" view share a `TableView<ItemDisplayDto>`.
    - **Proportions:** Uses `CONSTRAINED_RESIZE_POLICY` to ensure all columns fill the width equally (25% each in global view, 33% each in "My Reports").
    - **Contextual UI:** The "Reported By" column is programmatically hidden in the "My Reports" view to prevent redundancy.
    - **Filters:** Includes a dedicated "Apply Filters" button within the filter panel for immediate dataset updates, styled with vector SVG path icons for filter/search/reset.
    - **Pagination Toolbar:** Incorporates a beautiful, high-performance in-memory pagination toolbar at the bottom of the TableView. Displays:
        - 'Items per page' drop-down selector (10 (default), 25, 50, 100 choices).
        - Boundary-aware navigation controls (`<<`, `<`, `>`, `>>`) that automatically enable/disable based on page position.
        - Page descriptor (`Page X of Y`) and total records indicator (`Total Records: Z`).
        - Global overview metrics cards continue to aggregate global filtered data in-memory for complete consistency.
- **Overview Statistics & MoM Trends:** The Dashboard metrics (Total, Lost, etc.) update dynamically in-memory based on the current filtered search results. They are supplemented by database-driven **Month-over-Month (MoM) Trend Labels** (`totalTrendLabel`, etc.) that show percentage differences vs the prior month. Trend styling is context-aware: `.trend-up` (green with ↑), `.trend-down` (red with ↓), and `.trend-neutral` (gray with —).
- **Item Details:** The `ItemDetail.fxml` displays specific information. It dynamically renders contextual instructions ("How to claim" vs "If found") based on item status and conditionally renders status update controls for Administrators.
- **ComboBox Customization:** Use `StringConverter` to ensure `ComboBox` elements display human-readable names (e.g., Category Name) instead of internal entity strings.

### 3. Styling the UI
- **BootstrapFX:** Apply professional styling using classes like `btn`, `btn-primary`, or `lbl-info`.
- **Global CSS:** Use `styles/main.css` for consistent project-wide layouts.

## Best Practices
- **UI Decoupling:** Keep controllers focused on UI state. Delegate all data logic to Models/Repositories.
- **View Safety:** Check for `null` in controller `initialize()` or `setDependencies()` methods before populating components.
- **Naming Conventions:**
    - FXML files: PascalCase (e.g., `Login.fxml`).
    - IDs: camelCase (e.g., `#emailField`).
