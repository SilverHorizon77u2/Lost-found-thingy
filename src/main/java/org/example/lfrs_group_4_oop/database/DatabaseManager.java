package org.example.lfrs_group_4_oop.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages database connections and initialization for the LFRS application using SQLite.
 */
public class DatabaseManager {

    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());
    private static final String DATABASE_URL = "jdbc:sqlite:src/main/java/org/example/lfrs_group_4_oop/database/lfrs.db";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Total number of seeded users (IDs 1–9). */
    private static final int TOTAL_USERS = 9;

    /** Total number of seeded categories (IDs 1–8). */
    private static final int TOTAL_CATEGORIES = 8;

    /** Fixed random seed for deterministic, reproducible data generation. */
    private static final long RANDOM_SEED = 42L;

    private DatabaseManager() {
        // Utility class
    }

    /**
     * Establishes a connection to the SQLite database.
     * Enforces foreign key constraints for data integrity.
     * @return A Connection object.
     * @throws SQLException if a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DATABASE_URL);
        // Enable Foreign Key enforcement for this connection
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    /**
     * Initializes the database by creating necessary tables if they do not exist.
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            createTables(stmt);
            seedUsers(stmt);
            seedCategories(stmt);
            seedOriginalItems(stmt);
            seedOriginalClaimant(stmt);
            seedOriginalClaim(stmt);
            stmt.executeBatch();

            // Generate 5 years of historical data
            seedHistoricalItems(conn);

            // Schema Migrations
            ensureItemsTitleColumnExists(conn);
            ensureUsersAvatarColumnExists(conn);

            LOGGER.info("Database initialized and seeded successfully.");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing database", e);
        }
    }

    // ──────────────────────────────────────────────────────────────────
    // Table creation
    // ──────────────────────────────────────────────────────────────────

    private static void createTables(Statement stmt) throws SQLException {
        String[] tableCreationQueries = {
            "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "role TEXT DEFAULT 'Standard User'," +
                "student_no TEXT," +
                "program TEXT," +
                "section TEXT," +
                "avatar_path TEXT" +
            ");",

            "CREATE TABLE IF NOT EXISTS categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "category_name TEXT UNIQUE NOT NULL" +
            ");",

            "CREATE TABLE IF NOT EXISTS items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "description TEXT NOT NULL," +
                "image_path TEXT," +
                "date_reported DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "status TEXT NOT NULL," +
                "location TEXT," +
                "category_id INTEGER," +
                "reporter_id INTEGER," +
                "FOREIGN KEY (category_id) REFERENCES categories (id)," +
                "FOREIGN KEY (reporter_id) REFERENCES users (id)" +
            ");",

            "CREATE TABLE IF NOT EXISTS claimants (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "id_number TEXT NOT NULL" +
            ");",

            "CREATE TABLE IF NOT EXISTS claims (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "item_id INTEGER," +
                "claimant_id INTEGER," +
                "claim_date DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (item_id) REFERENCES items (id)," +
                "FOREIGN KEY (claimant_id) REFERENCES claimants (id)" +
            ");"
        };

        for (String query : tableCreationQueries) {
            stmt.addBatch(query);
        }
    }

    // ──────────────────────────────────────────────────────────────────
    // User seeding (IDs 1–9)
    // ──────────────────────────────────────────────────────────────────

    private static void seedUsers(Statement stmt) throws SQLException {
        String[] userInserts = {
            // Original 3 users
            "INSERT OR IGNORE INTO users (id, name, email, password, role, student_no, program, section) " +
                "VALUES (1, 'Randall Graida', 'randall.graida@gmail.com', 'randall123', 'Administrator', 'ADMIN-001', 'ADMIN', '001');",
            "INSERT OR IGNORE INTO users (id, name, email, password, role, student_no, program, section) " +
                "VALUES (2, 'John Doe', 'john.doe@example.com', 'john123', 'Standard User', '2023-00001', 'BSIT', '3A');",
            "INSERT OR IGNORE INTO users (id, name, email, password, role, student_no, program, section) " +
                "VALUES (3, 'Jane Smith', 'jane.smith@example.com', 'jane123', 'Standard User', '2023-00002', 'BSCS', '2B');",
            // 6 new users for reporter diversity
            "INSERT OR IGNORE INTO users (id, name, email, password, role, student_no, program, section) " +
                "VALUES (4, 'Carlos Reyes', 'carlos.reyes@example.com', 'carlos123', 'Standard User', '2022-00010', 'BSIT', '4A');",
            "INSERT OR IGNORE INTO users (id, name, email, password, role, student_no, program, section) " +
                "VALUES (5, 'Maria Santos', 'maria.santos@example.com', 'maria123', 'Standard User', '2023-00015', 'BSCS', '1A');",
            "INSERT OR IGNORE INTO users (id, name, email, password, role, student_no, program, section) " +
                "VALUES (6, 'Ahmed Khan', 'ahmed.khan@example.com', 'ahmed123', 'Standard User', '2022-00022', 'BSBA', '2C');",
            "INSERT OR IGNORE INTO users (id, name, email, password, role, student_no, program, section) " +
                "VALUES (7, 'Sofia Cruz', 'sofia.cruz@example.com', 'sofia123', 'Standard User', '2024-00003', 'BSA', '1B');",
            "INSERT OR IGNORE INTO users (id, name, email, password, role, student_no, program, section) " +
                "VALUES (8, 'David Park', 'david.park@example.com', 'david123', 'Standard User', '2023-00030', 'BSIT', '2A');",
            "INSERT OR IGNORE INTO users (id, name, email, password, role, student_no, program, section) " +
                "VALUES (9, 'Elena Rivera', 'elena.rivera@example.com', 'elena123', 'Standard User', '2024-00008', 'BSCS', '3B');"
        };
        for (String sql : userInserts) {
            stmt.addBatch(sql);
        }
    }

    // ──────────────────────────────────────────────────────────────────
    // Category seeding (IDs 1–8)
    // ──────────────────────────────────────────────────────────────────

    private static void seedCategories(Statement stmt) throws SQLException {
        String[] categoryInserts = {
            // Original 4 categories
            "INSERT OR IGNORE INTO categories (id, category_name) VALUES (1, 'Electronics');",
            "INSERT OR IGNORE INTO categories (id, category_name) VALUES (2, 'Documents');",
            "INSERT OR IGNORE INTO categories (id, category_name) VALUES (3, 'Personal Items');",
            "INSERT OR IGNORE INTO categories (id, category_name) VALUES (4, 'Other');",
            // 4 new categories
            "INSERT OR IGNORE INTO categories (id, category_name) VALUES (5, 'Clothing');",
            "INSERT OR IGNORE INTO categories (id, category_name) VALUES (6, 'Bags & Accessories');",
            "INSERT OR IGNORE INTO categories (id, category_name) VALUES (7, 'Books & Notebooks');",
            "INSERT OR IGNORE INTO categories (id, category_name) VALUES (8, 'Sports Equipment');"
        };
        for (String sql : categoryInserts) {
            stmt.addBatch(sql);
        }
    }

    // ──────────────────────────────────────────────────────────────────
    // Original 10 seed items (preserved exactly)
    // ──────────────────────────────────────────────────────────────────

    private static void seedOriginalItems(Statement stmt) throws SQLException {
        String[] seedItems = {
            "INSERT OR IGNORE INTO items (id, title, description, status, location, category_id, reporter_id) VALUES (1, 'Lost iPhone 13', 'i remember this was lost on my seat at the airport at 3pm', 'Lost', 'Airport', 1, 2);",
            "INSERT OR IGNORE INTO items (id, title, description, status, location, category_id, reporter_id) VALUES (2, 'Found MacBook Pro', 'Silver MacBook Pro found near the cafeteria entrance. Seems to have a custom sticker on the back.', 'Found', 'Cafeteria', 1, 3);",
            "INSERT OR IGNORE INTO items (id, title, description, status, location, category_id, reporter_id) VALUES (3, 'Lost Blue Wallet', 'Blue leather wallet containing some cash and student ID. I lost it after my workout session.', 'Lost', 'Gym', 3, 3);",
            "INSERT OR IGNORE INTO items (id, title, description, status, location, category_id, reporter_id) VALUES (4, 'Found Keys', 'A set of keys with a red Lego keychain. Found them on the ground in the parking lot.', 'Found', 'Parking Lot', 4, 1);",
            "INSERT OR IGNORE INTO items (id, title, description, status, location, category_id, reporter_id) VALUES (5, 'Claimed Passport', 'International Passport. I left it at the student center while filling out forms.', 'Claimed', 'Student Center', 2, 1);",
            "INSERT OR IGNORE INTO items (id, title, description, status, location, category_id, reporter_id) VALUES (6, 'Lost Water Bottle', 'Black insulated water bottle. Left it on the desk in Room 302.', 'Lost', 'Room 302', 4, 2);",
            "INSERT OR IGNORE INTO items (id, title, description, status, location, category_id, reporter_id) VALUES (7, 'Found Calculator', 'Scientific Calculator found on a desk in the science lab.', 'Found', 'Science Lab', 1, 1);",
            "INSERT OR IGNORE INTO items (id, title, description, status, location, category_id, reporter_id) VALUES (8, 'Claimed ID Card', 'Student ID Card. Dropped it near the Admin Office.', 'Claimed', 'Admin Office', 2, 3);",
            "INSERT OR IGNORE INTO items (id, title, description, status, location, category_id, reporter_id) VALUES (9, 'Lost Umbrella', 'Black folding umbrella. Forgotten near the main gate when it stopped raining.', 'Lost', 'Main Gate', 4, 2);",
            "INSERT OR IGNORE INTO items (id, title, description, status, location, category_id, reporter_id) VALUES (10, 'Found Earbuds', 'White wireless earbuds found on a table in the library reading room.', 'Found', 'Library', 1, 3);"
        };
        for (String sql : seedItems) {
            stmt.addBatch(sql);
        }
    }

    // ──────────────────────────────────────────────────────────────────
    // Original claimant & claim (preserved exactly)
    // ──────────────────────────────────────────────────────────────────

    private static void seedOriginalClaimant(Statement stmt) throws SQLException {
        String seedClaimant = "INSERT OR IGNORE INTO claimants (name, id_number) " +
                              "SELECT 'Alice Brown', 'ID123456' " +
                              "WHERE NOT EXISTS (SELECT 1 FROM claimants WHERE id_number = 'ID123456');";
        stmt.addBatch(seedClaimant);
    }

    private static void seedOriginalClaim(Statement stmt) throws SQLException {
        String seedClaim = "INSERT OR IGNORE INTO claims (item_id, claimant_id) " +
                           "SELECT i.id, c.id FROM items i, claimants c " +
                           "WHERE i.description = 'International Passport' AND c.id_number = 'ID123456' " +
                           "AND NOT EXISTS (SELECT 1 FROM claims cl WHERE cl.item_id = i.id AND cl.claimant_id = c.id);";
        stmt.addBatch(seedClaim);
    }

    // ──────────────────────────────────────────────────────────────────
    // 5-year historical data generation
    // ──────────────────────────────────────────────────────────────────

    /**
     * Generates ~600 items spanning May 2021 – May 2026 with realistic variation.
     * Uses a fixed random seed for deterministic, reproducible output.
     */
    private static void seedHistoricalItems(Connection conn) throws SQLException {
        // Quick check: skip if historical data already exists
        if (historicalDataExists(conn)) {
            return;
        }

        Random random = new Random(RANDOM_SEED);

        String[] titles = getItemTitles();
        String[] descriptions = getItemDescriptions();
        String[] locations = getLocations();

        YearMonth start = YearMonth.of(2021, 5);
        YearMonth end = YearMonth.of(2026, 5);
        int itemId = 11; // Start after original 10 seed items
        int claimantId = 2; // Start after original claimant (ID 1)

        try (Statement stmt = conn.createStatement()) {
            YearMonth current = start;
            while (!current.isAfter(end)) {
                int itemsThisMonth = calculateMonthlyItemCount(current, random);

                for (int i = 0; i < itemsThisMonth; i++) {
                    String dateStr = generateRandomDate(current, random);
                    int titleIdx = random.nextInt(titles.length);
                    String status = pickWeightedStatus(current, random);
                    int locationIdx = random.nextInt(locations.length);
                    int categoryId = 1 + random.nextInt(TOTAL_CATEGORIES);
                    int reporterId = 1 + random.nextInt(TOTAL_USERS);

                    String sql = String.format(
                        "INSERT OR IGNORE INTO items (id, title, description, date_reported, status, location, category_id, reporter_id) " +
                        "VALUES (%d, '%s', '%s', '%s', '%s', '%s', %d, %d);",
                        itemId,
                        escapeSql(titles[titleIdx]),
                        escapeSql(descriptions[titleIdx]),
                        dateStr,
                        status,
                        escapeSql(locations[locationIdx]),
                        categoryId,
                        reporterId
                    );
                    stmt.addBatch(sql);

                    // Generate claimant + claim for Claimed items
                    if ("Claimed".equals(status)) {
                        seedClaimForItem(stmt, itemId, claimantId, dateStr, random);
                        claimantId++;
                    }

                    itemId++;
                }
                current = current.plusMonths(1);
            }
            stmt.executeBatch();
        }

        LOGGER.log(Level.INFO, "Seeded {0} historical items spanning 5 years.", itemId - 11);
    }

    /**
     * Checks if historical item data (ID > 10) already exists to avoid re-seeding.
     */
    private static boolean historicalDataExists(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM items WHERE id > 10")) {
            return rs.next() && rs.getInt(1) > 500;
        }
    }

    /**
     * Creates a claimant and a corresponding claim for a Claimed item.
     */
    private static void seedClaimForItem(Statement stmt, int itemId, int claimantId, String dateStr, Random random) throws SQLException {
        String[] firstNames = {"Miguel", "Anna", "Leo", "Clara", "James", "Yuki", "Omar", "Priya", "Nathan", "Lisa",
                               "Kevin", "Mei", "Roberto", "Hannah", "Sam", "Diana", "Felix", "Grace", "Amir", "Chloe"};
        String[] lastNames = {"Garcia", "Lee", "Torres", "Nguyen", "Williams", "Tanaka", "Hassan", "Sharma", "Chen", "Martin",
                              "Brown", "Kim", "Santos", "Miller", "Davis", "Lopez", "Wang", "Taylor", "Ali", "Anderson"};

        String claimantName = firstNames[random.nextInt(firstNames.length)] + " " + lastNames[random.nextInt(lastNames.length)];
        String idNumber = String.format("ID%06d", 100000 + claimantId);

        stmt.addBatch(String.format(
            "INSERT OR IGNORE INTO claimants (id, name, id_number) VALUES (%d, '%s', '%s');",
            claimantId, escapeSql(claimantName), idNumber
        ));
        stmt.addBatch(String.format(
            "INSERT OR IGNORE INTO claims (item_id, claimant_id, claim_date) VALUES (%d, %d, '%s');",
            itemId, claimantId, dateStr
        ));
    }

    /**
     * Calculates item count per month with seasonal variation.
     * Higher counts in June (end of semester), September (start), and December (holiday rush).
     * Lower counts in April and August (break months).
     */
    private static int calculateMonthlyItemCount(YearMonth month, Random random) {
        int base = 8 + random.nextInt(5); // 8–12 items
        int monthVal = month.getMonthValue();

        // Seasonal adjustments
        if (monthVal == 6 || monthVal == 9 || monthVal == 12) {
            base += 3 + random.nextInt(3); // +3 to +5 for busy months
        } else if (monthVal == 4 || monthVal == 8) {
            base -= 2 + random.nextInt(2); // -2 to -3 for break months
        }
        return Math.max(5, base);
    }

    /**
     * Generates a random date-time within the given month (day 1–28, hours 7–21).
     */
    private static String generateRandomDate(YearMonth month, Random random) {
        int day = 1 + random.nextInt(28);
        int hour = 7 + random.nextInt(15);   // 07:00–21:00
        int minute = random.nextInt(60);
        int second = random.nextInt(60);

        LocalDateTime dateTime = LocalDateTime.of(month.getYear(), month.getMonthValue(), day, hour, minute, second);
        return dateTime.format(DATE_FMT);
    }

    /**
     * Picks a weighted status: Lost ~45%, Found ~35%, Claimed ~20%.
     * Older months (more than 6 months ago) have a higher Claimed ratio
     * to simulate the natural lifecycle of items being resolved.
     */
    private static String pickWeightedStatus(YearMonth month, Random random) {
        YearMonth now = YearMonth.of(2026, 5);
        long monthsAgo = java.time.temporal.ChronoUnit.MONTHS.between(month, now);

        int roll = random.nextInt(100);

        // For older items, increase Claimed probability
        if (monthsAgo > 6) {
            // Lost ~35%, Found ~30%, Claimed ~35%
            if (roll < 35) return "Lost";
            if (roll < 65) return "Found";
            return "Claimed";
        }

        // Recent items: Lost ~50%, Found ~35%, Claimed ~15%
        if (roll < 50) return "Lost";
        if (roll < 85) return "Found";
        return "Claimed";
    }

    /**
     * Escapes single quotes in SQL string literals.
     */
    private static String escapeSql(String value) {
        return value.replace("'", "''");
    }

    // ──────────────────────────────────────────────────────────────────
    // Data pools for realistic item generation
    // ──────────────────────────────────────────────────────────────────

    private static String[] getItemTitles() {
        return new String[] {
            // Electronics (category 1)
            "iPhone with Cracked Screen",
            "Samsung Galaxy Phone",
            "Wireless Bluetooth Headphones",
            "iPad Mini",
            "USB Flash Drive 32GB",
            "Laptop Charger",
            "Apple Watch",
            "Power Bank",
            // Documents (category 2)
            "Student ID Card",
            "National ID",
            "Class Schedule Printout",
            "Library Card",
            "Medical Certificate",
            "Enrollment Form",
            // Personal Items (category 3)
            "Black Leather Wallet",
            "Brown Bifold Wallet",
            "Prescription Eyeglasses",
            "House Keys with Keychain",
            "Car Key Fob",
            "Stainless Steel Water Bottle",
            "Tumbler with Sticker",
            "Folding Umbrella",
            // Other (category 4)
            "Transparent Pencil Case",
            "Scientific Calculator",
            "Art Supplies Pouch",
            "USB-C Dongle",
            "Lunch Box Container",
            // Clothing (category 5)
            "Blue Denim Jacket",
            "Black Hoodie",
            "White Lab Coat",
            "PE Uniform Shirt",
            "Gray Beanie Hat",
            // Bags & Accessories (category 6)
            "Black Backpack",
            "Canvas Tote Bag",
            "Sling Bag with Patches",
            "Laptop Sleeve",
            "Drawstring Gym Bag",
            // Books & Notebooks (category 7)
            "Calculus Textbook",
            "Blue Spiral Notebook",
            "Java Programming Book",
            "Chemistry Lab Manual",
            "Brown Leather Journal",
            // Sports Equipment (category 8)
            "Badminton Racket",
            "Basketball",
            "Yoga Mat",
            "Swimming Goggles",
            "Tennis Ball Can"
        };
    }

    private static String[] getItemDescriptions() {
        return new String[] {
            // Electronics
            "An iPhone with a visibly cracked screen and a blue silicone case. Found near the entrance.",
            "A Samsung Galaxy phone with a transparent case. The lock screen shows a beach wallpaper.",
            "Over-ear wireless headphones, black with silver accents. Found resting on a bench.",
            "A space gray iPad Mini with a folding keyboard case attached to it.",
            "A black SanDisk 32GB USB flash drive found plugged into a library computer.",
            "A white laptop charger with USB-C connector, possibly for a MacBook.",
            "A silver Apple Watch with a navy blue sport band. Found on a bathroom counter.",
            "A 10000mAh white power bank with a short charging cable attached.",
            // Documents
            "A PUP student ID card. The name and photo are partially visible.",
            "A national ID card found tucked between seat cushions in the lobby.",
            "A printed class schedule for the current semester, folded in half.",
            "A library borrowing card with several stamps. Found on the returns desk.",
            "A medical certificate from the university clinic, dated recently.",
            "An enrollment form with personal details filled out. Found near the registrar.",
            // Personal Items
            "A black leather wallet with a few cards and some cash inside.",
            "A brown bifold wallet with a Velcro closure. Contains a student ID.",
            "Prescription eyeglasses with black rectangular frames in a hard case.",
            "A set of house keys on a ring with a small plush bear keychain.",
            "A silver car key fob for a Toyota. Found on the ground near the parking area.",
            "A stainless steel insulated water bottle, matte black finish.",
            "A clear tumbler with assorted anime stickers on it. Still has liquid inside.",
            "A compact folding umbrella, dark navy blue. Left hanging on a chair.",
            // Other
            "A transparent zippered pencil case containing pens, highlighters, and an eraser.",
            "A Casio scientific calculator, model fx-991ES. Found on a classroom desk.",
            "A small pouch filled with colored pencils, markers, and a glue stick.",
            "A silver USB-C to HDMI dongle. Found near the projector setup area.",
            "A rectangular lunch box container with a blue lid, still sealed.",
            // Clothing
            "A blue denim jacket, size medium, with a small embroidered flower on the collar.",
            "A plain black hoodie, size large, left draped over a chair in the study hall.",
            "A white lab coat with a name stitched on the pocket. Found in the science building.",
            "A maroon PE uniform shirt with the university logo, size small.",
            "A gray knitted beanie hat found on the floor near the main staircase.",
            // Bags & Accessories
            "A black JanSport backpack with a water bottle pocket. Contains a notebook inside.",
            "A beige canvas tote bag with a 'Save the Earth' print. Found hanging on a hook.",
            "A dark green sling bag covered in travel patches. Found near the coffee shop.",
            "A padded 14-inch laptop sleeve, gray neoprene material.",
            "A red drawstring gym bag with a white Nike logo, containing gym clothes.",
            // Books & Notebooks
            "A hardcover Calculus textbook by Stewart, 8th edition. Has highlighting inside.",
            "A blue spiral-bound notebook with course notes written in neat handwriting.",
            "A 'Head First Java' programming book with sticky notes marking several chapters.",
            "A chemistry lab manual with the student name written on the cover.",
            "A brown leather-bound journal with lined pages, about half filled with writing.",
            // Sports Equipment
            "A Yonex badminton racket with a torn grip, stored in a cloth cover.",
            "An orange Molten basketball, slightly deflated. Found on the outdoor court.",
            "A rolled-up purple yoga mat with a carrying strap. Left in the multipurpose hall.",
            "A pair of Speedo swimming goggles with tinted lenses, found in the locker room.",
            "A sealed can of three Penn tennis balls, left on a bench near the tennis court."
        };
    }

    private static String[] getLocations() {
        return new String[] {
            "Main Library",
            "Cafeteria",
            "Student Center",
            "Gymnasium",
            "Parking Lot A",
            "Parking Lot B",
            "Science Building",
            "Engineering Building",
            "Admin Office",
            "Room 201",
            "Room 302",
            "Room 405",
            "Computer Lab 1",
            "Computer Lab 2",
            "Auditorium",
            "Main Gate",
            "Registrar Office",
            "PE Field",
            "Swimming Pool Area",
            "Outdoor Court"
        };
    }

    // ──────────────────────────────────────────────────────────────────
    // Schema migrations
    // ──────────────────────────────────────────────────────────────────

    private static void ensureUsersAvatarColumnExists(Connection conn) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(users)")) {

            boolean hasAvatar = false;
            while (rs.next()) {
                if ("avatar_path".equalsIgnoreCase(rs.getString("name"))) {
                    hasAvatar = true;
                    break;
                }
            }

            if (!hasAvatar) {
                LOGGER.info("Migrating database: Adding 'avatar_path' column to 'users' table.");
                stmt.execute("ALTER TABLE users ADD COLUMN avatar_path TEXT");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error migrating database schema (users)", e);
        }
    }

    private static void ensureItemsTitleColumnExists(Connection conn) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(items)")) {

            boolean hasTitle = false;
            while (rs.next()) {
                if ("title".equalsIgnoreCase(rs.getString("name"))) {
                    hasTitle = true;
                    break;
                }
            }

            if (!hasTitle) {
                LOGGER.info("Migrating database: Adding 'title' column to 'items' table.");
                stmt.execute("ALTER TABLE items ADD COLUMN title TEXT NOT NULL DEFAULT 'No Title'");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error migrating database schema", e);
        }
    }
}
