package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

public class EditSceneController {
	private Stage stage;
	private Scene scene ;
	private Parent root ;
	
	@FXML private TextField firstnameF;
    @FXML private TextField lastnameF;
    @FXML private TextField usernameField;
    @FXML private TextField emailFielde;
    @FXML private TextField phonenumberFielde;
    @FXML private PasswordField passwordField;

    private String currentUsername;
    private int currentUserId;


    public void initializeUserInfo(int userID) {
        this.currentUserId = userID;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Games", "root", "souheil.2005");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE UserID = ?")) {

            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                currentUserId = rs.getInt("UserID");
                System.out.println("UserID passed to profile: " + currentUserId);
                firstnameF.setText(rs.getString("Firstname"));
                lastnameF.setText(rs.getString("Lastname"));
                usernameField.setText(rs.getString("Username"));
                emailFielde.setText(rs.getString("Email"));
                phonenumberFielde.setText(rs.getString("phone_number"));
                passwordField.setText(rs.getString("PasswordHash"));
            } 

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateProfile(ActionEvent event) throws IOException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Games", "root", "souheil.2005");
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE users SET Firstname=?, Lastname=?, Username=?, Email=?, phone_number=?, PasswordHash=? WHERE UserID=?")) {

            stmt.setString(1, firstnameF.getText());
            stmt.setString(2, lastnameF.getText());
            stmt.setString(3, usernameField.getText());
            stmt.setString(4, emailFielde.getText());
            stmt.setString(5, phonenumberFielde.getText());
            stmt.setString(6, passwordField.getText());
            stmt.setInt(7, currentUserId);

            System.out.println("Updating user with ID: " + currentUserId);

            int updated = stmt.executeUpdate();

            if (updated > 0) {
                showAlert("Profile updated successfully.");
                currentUsername = usernameField.getText();
            } else {
                showAlert("No changes were made.");
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfilScene.fxml"));
            root = loader.load();

            ProfileController profileController = loader.getController();
            profileController.setUserId(currentUserId);

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            String css = this.getClass().getResource("application.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResource("/images/icon.jpg").toExternalForm()));
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error updating profile.");
        }
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Update");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
