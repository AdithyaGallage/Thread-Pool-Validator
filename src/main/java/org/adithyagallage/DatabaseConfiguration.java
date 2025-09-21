package org.adithyagallage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

public class DatabaseConfiguration {

    // Singleton instance of DataSource
    private static HikariDataSource hikariDataSource;
    private static DriverManagerDataSource driverManagerDataSource;

    // Private constructor to prevent instantiation
    private DatabaseConfiguration() { }

    // Public methods to get the singleton DataSource
    public static DataSource getHikariDataSource() {
        if (hikariDataSource == null) {
            synchronized (DatabaseConfiguration.class) {
                if (hikariDataSource == null) { // double-checked locking
                    HikariConfig config = new HikariConfig();
                    config.setJdbcUrl("jdbc:mysql://localhost:3306/validator");
                    config.setUsername("root");
                    config.setPassword("root");
                    config.setDriverClassName("com.mysql.cj.jdbc.Driver");

                    // Pool settings
                    config.setMaximumPoolSize(15);
                    config.setMinimumIdle(2);
                    config.setIdleTimeout(30000);
                    config.setMaxLifetime(1800000);
                    config.setConnectionTimeout(20000);

                    hikariDataSource = new HikariDataSource(config);
                }
            }
        }
        return hikariDataSource;
    }

    public static DataSource getDriverManagerDataSource() {
        if (driverManagerDataSource == null) {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl("jdbc:mysql://localhost:3306/validator");
            dataSource.setUsername("root");
            dataSource.setPassword("root");

            driverManagerDataSource = dataSource;
        }
        return driverManagerDataSource;
    }
}
