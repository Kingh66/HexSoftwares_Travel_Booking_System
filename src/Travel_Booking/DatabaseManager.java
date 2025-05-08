package Travel_Booking;

import java.sql.*;
import java.util.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseManager {
    private static final ThreadLocal<Connection> threadConnection = new ThreadLocal<>();

    private static HikariDataSource dataSource;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/travel_booking";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Sizwe22";
    private static final int MAX_RETRIES = 3;
    private static final int MAX_POOL_SIZE = 10;
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    
    // Initialize connection pool
    static {
        try {
            Class.forName(DRIVER_CLASS);
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DB_URL);
            config.setUsername(DB_USER);
            config.setPassword(DB_PASSWORD);
            config.setMaximumPoolSize(MAX_POOL_SIZE);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            
            dataSource = new HikariDataSource(config);
            
            // Test connection
            try (Connection conn = dataSource.getConnection()) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("SELECT 1");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError("Database initialization failed");
        }
    }

    public static Connection getConnection() throws SQLException {
    Connection conn = threadConnection.get();
    if (conn != null) return conn;

    SQLException lastException = null;

    for (int i = 0; i < MAX_RETRIES; i++) {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            lastException = e;
            System.err.println("Connection attempt " + (i + 1) + " failed. Retrying...");
            try {
                Thread.sleep(1000 * (i + 1));
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }
    throw new SQLException("Failed to get database connection after " + MAX_RETRIES + " attempts", lastException);
}


    public static int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setParameters(stmt, params);
            return stmt.executeUpdate();
        }
    }

    public static List<Map<String, Object>> executeQuery(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setParameters(stmt, params);
            try (ResultSet rs = stmt.executeQuery()) {
                return resultSetToList(rs);
            }
        }
    }

    private static void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }

    private static List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(metaData.getColumnLabel(i), rs.getObject(i));
            }
            results.add(row);
        }
        return results;
    }

    public static void beginTransaction() throws SQLException {
    Connection conn = dataSource.getConnection();
    conn.setAutoCommit(false);
    threadConnection.set(conn);
}

    public static void commitTransaction() throws SQLException {
    Connection conn = threadConnection.get();
    if (conn != null) {
        conn.commit();
        conn.setAutoCommit(true);
        conn.close();
        threadConnection.remove();
    }
}

    public static void rollbackTransaction() {
    Connection conn = threadConnection.get();
    if (conn != null) {
        try {
            conn.rollback();
            conn.setAutoCommit(true);
            conn.close();
        } catch (SQLException e) {
            System.err.println("Rollback failed: " + e.getMessage());
        } finally {
            threadConnection.remove();
        }
    }
    }
    
    public static int updateUser(User user) throws SQLException {
    String sql = "UPDATE users SET "
            + "email = ?, "
            + "full_name = ?, "
            + "phone = ?, "
            + "address = ?, "
            + "profile_picture_path = ?, "
            + "last_login = ? "
            + "WHERE user_id = ?";
    
    return executeUpdate(sql,
            user.getEmail(),
            user.getFullName(),
            user.getPhone(),
            user.getAddress(),
            user.getProfilePicturePath(),
            user.getLastLogin(),
            user.getUserId()
    );
} 
    
    public static void createAdminUser() {
        String username = "admin";
        String password = "Sizwe@21";
        String email = "admin@travel.com";
        String fullName = "System Administrator";

        try {
            // Check if admin already exists
            List<Map<String, Object>> existing = executeQuery(
                "SELECT user_id FROM users WHERE username = ?", 
                username
            );

            if (!existing.isEmpty()) {
                System.out.println("Admin user already exists!");
                return;
            }

            // Hash password
            String hashedPassword = PasswordUtils.hashPassword(password);

            // Insert admin
            int result = executeUpdate(
                "INSERT INTO users (username, password, email, full_name, role) " +
                "VALUES (?, ?, ?, ?, ?)",
                username, hashedPassword, email, fullName, "admin"
            );

            if (result > 0) {
                System.out.println("Admin user created successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error creating admin: " + e.getMessage());
        }
    }
    
    public static void closeConnection(Connection conn) {
    if (conn != null) {
        try {
            conn.close();
        } catch (SQLException e) {
            System.err.println("Failed to close connection: " + e.getMessage());
        }
    }
}


    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}