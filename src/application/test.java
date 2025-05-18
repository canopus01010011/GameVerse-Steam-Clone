package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class test {
	  private static final String URL = "jdbc:mysql://localhost:3306/games?serverTimezone=UTC";
	    private static final String USER = "root"; 
	    private static final String PASSWORD = "souheil.2005";

	    public static Connection getConnection() {
	        try {
	            System.out.println("Trying to connect...");
	            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
	            System.out.println("Connection successful!");
	            return conn;
	        } catch (SQLException e) {
	            System.out.println("Connection failed!");
	            e.printStackTrace();
	            return null;
	        }
	    }

	    public static void main(String[] args) {
	        getConnection();
	    }
}
