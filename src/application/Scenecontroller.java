package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Scenecontroller{
	private Stage stage;
	private Scene scene ;
	private Parent root ;
	private int currentUserId;

	@FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    
		public void signup(ActionEvent e) throws IOException {
		    FXMLLoader loader = new FXMLLoader(getClass().getResource("/signup.fxml"));
				root = loader.load();
		    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		    scene = new Scene(root);
		    String css = this.getClass().getResource("application.css").toExternalForm();
		    scene.getStylesheets().add(css);
		    stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResource("/images/icon.jpg").toExternalForm()));
            stage.setResizable(false);
            stage.centerOnScreen();
		    stage.show();
		}
		@FXML 
		private void handleLogin(ActionEvent event) throws IOException {
			  System.out.println("Login button clicked."); 
		    String username = usernameField.getText();
		    String password = passwordField.getText();
		    int userId = authenticateUser(username, password);

		    if (userId != -1) {
		        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfilScene.fxml"));
		        Parent root = loader.load();
		        ProfileController profileController = loader.getController();
		        profileController.setUserId(userId);

		        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		        scene = new Scene(root);
		        String css = this.getClass().getResource("application.css").toExternalForm();
		        scene.getStylesheets().add(css);
		        stage.setScene(scene);
	            stage.getIcons().add(new Image(getClass().getResource("/images/icon.jpg").toExternalForm()));
	            stage.setResizable(false);
	            stage.centerOnScreen();
		        stage.show();   
		    } else {
		        showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
		    }
		}
		   public void setCurrentUserId(int id) {
		        this.currentUserId = id;
		    }

		private int authenticateUser(String username, String password) {
		    int userId = -1;
		    try (Connection conn = DBConnection.getConnection()) {
		        String sql = "SELECT UserID FROM Users WHERE Username = ? AND PasswordHash = ?";
		        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		            stmt.setString(1, username);
		            stmt.setString(2, password);
		            ResultSet rs = stmt.executeQuery();

		            if (rs.next()) {
		                userId = rs.getInt("UserID");
		            }
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    }

		    return userId;
		}

		    private void showAlert(Alert.AlertType type, String title, String message) {
		        Alert alert = new Alert(type);
		        alert.setTitle(title);
		        alert.setHeaderText(null);
		        alert.setContentText(message);
		        alert.showAndWait();
		    }

	}
