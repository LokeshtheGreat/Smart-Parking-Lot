import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final String CONFIG_FILE = "db.properties";

    public static Connection getConnection() throws SQLException {

        Properties properties = new Properties();

        Path configPath = Path.of(CONFIG_FILE);

        if (!Files.exists(configPath)) {
            throw new SQLException(
                "Database configuration file not found: " + CONFIG_FILE
            );
        }

        try (InputStream input = Files.newInputStream(configPath)) {
            properties.load(input);
        }
        catch (IOException e) {
            throw new SQLException(
                "Unable to read database configuration.", e
            );
        }

        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

        if (url == null || username == null || password == null) {
            throw new SQLException(
                "Database configuration is incomplete. " +
                "Check db.properties."
            );
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
            throw new SQLException(
                "MySQL JDBC Driver not found. Check the lib folder.", e
            );
        }

        return DriverManager.getConnection(url, username, password);
    }
}