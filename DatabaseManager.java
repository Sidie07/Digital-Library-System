package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:library.db";
    private static final DatabaseManager instance = new DatabaseManager();
    private Connection connection;

    private DatabaseManager() {
        connect();
        initialiseSchema();
    }

    public static DatabaseManager getInstance() {
        return instance;
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
            System.out.println("Connected to SQLite database.");
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            System.exit(1);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private void initialiseSchema() {
        String createBooks = """
                CREATE TABLE IF NOT EXISTS books (
                    book_id       INTEGER PRIMARY KEY AUTOINCREMENT,
                    title         TEXT    NOT NULL,
                    author        TEXT    NOT NULL,
                    category      TEXT    NOT NULL,
                    status        TEXT    NOT NULL DEFAULT 'Available'
                )""";

        String createMembers = """
                CREATE TABLE IF NOT EXISTS members (
                    member_id       INTEGER PRIMARY KEY AUTOINCREMENT,
                    name            TEXT    NOT NULL,
                    email           TEXT    NOT NULL UNIQUE,
                    membership_type TEXT    NOT NULL DEFAULT 'Standard'
                )""";

        String createBorrowing = """
                CREATE TABLE IF NOT EXISTS borrowing_records (
                    record_id   INTEGER PRIMARY KEY AUTOINCREMENT,
                    book_id     INTEGER NOT NULL,
                    member_id   INTEGER NOT NULL,
                    borrow_date TEXT    NOT NULL,
                    due_date    TEXT    NOT NULL,
                    status      TEXT    NOT NULL DEFAULT 'Borrowed',
                    FOREIGN KEY (book_id)   REFERENCES books(book_id),
                    FOREIGN KEY (member_id) REFERENCES members(member_id)
                )""";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createBooks);
            stmt.execute(createMembers);
            stmt.execute(createBorrowing);
        } catch (SQLException e) {
            System.err.println("Failed to initialise schema: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
