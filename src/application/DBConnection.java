package application;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DBConnection {

    private static final Properties props = new Properties();
    private static boolean loaded = false;

    private static synchronized void loadProperties() {
        if (loaded) {
            return;
        }
        try (InputStream input = DBConnection.class.getResourceAsStream("/db.properties")) {
            if (input == null) {
                throw new IllegalStateException(
                    "db.properties not found on the classpath. " +
                    "Copy src/db.properties.example to src/db.properties " +
                    "and fill in your local database credentials before running the app."
                );
            }
            props.load(input);
            loaded = true;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read db.properties", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        loadProperties();
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        if (url == null || user == null || password == null) {
            throw new IllegalStateException(
                "db.properties is missing one of db.url / db.user / db.password."
            );
        }

        return DriverManager.getConnection(url, user, password);
    }
}
