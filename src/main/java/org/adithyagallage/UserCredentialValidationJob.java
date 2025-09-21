package org.adithyagallage;

import javax.sql.DataSource;
import java.sql.*;

public class UserCredentialValidationJob {
    private String email;
    private String password;
    private DataSource dataSource;

    public UserCredentialValidationJob(String email, String password, boolean useDBConnectionPool) {
        this.email = email;
        this.password = password;
        if (useDBConnectionPool) this.dataSource = DatabaseConfiguration.getHikariDataSource();
        else this.dataSource = DatabaseConfiguration.getDriverManagerDataSource();

    }

    public User validate() {
        String selectSql = "SELECT id, username, email, password FROM users WHERE email = ? AND password = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // User found, map ResultSet to User object
                    User user = new User();
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    return user;
                } else {
                    User user = new User();
                    user.setEmail(this.email);
                    user.setGuestUser(true);
                    return user;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
