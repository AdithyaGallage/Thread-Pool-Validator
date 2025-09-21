package org.adithyagallage;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class DatabaseSeeder {
    private DataSource dataSource;
    public DatabaseSeeder(boolean useDbConnectionPool) {
        if(useDbConnectionPool) this.dataSource = DatabaseConfiguration.getHikariDataSource();
        else this.dataSource = DatabaseConfiguration.getDriverManagerDataSource();
    }

    public void seed() {
        try (Connection conn = dataSource.getConnection()) {
            // 1. Create table if not exists
            String createTableSQL = """
                    CREATE TABLE IF NOT EXISTS users (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(50) NOT NULL,
                        email VARCHAR(100) NOT NULL,
                        password VARCHAR(50) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """;

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
                System.out.println("Table 'users' is ready.");
            }

            // 2. Seed initial data
            String insertSql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                for (int i = 1; i <= 5; i++) {
                    pstmt.setString(1, "user" + i);
                    pstmt.setString(2, "user" + i + "@example.com");
                    pstmt.setString(3, "pass" + i);
                    pstmt.addBatch();
                }
                int[] results = pstmt.executeBatch();
                System.out.println("Inserted " + results.length + " users.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clean() {
        try (Connection conn = dataSource.getConnection()) {
            // Delete table if exists
            String createTableSQL = """
                    DROP TABLE IF EXISTS users
                    """;

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
                System.out.println("Table 'users' is deleted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
