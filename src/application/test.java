package application;

import java.sql.Connection;

/**
 * Quick manual connectivity check — run this to confirm db.properties
 * is set up correctly before launching the full JavaFX app.
 */
public class test {

    public static void main(String[] args) {
        System.out.println("Trying to connect...");
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("Connection successful!");
        } catch (Exception e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
    }
}
