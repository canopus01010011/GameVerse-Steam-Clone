package application;

import javafx.scene.input.MouseEvent;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.*;
import java.util.ResourceBundle;

import javax.print.DocFlavor.URL;

public class ProfileController {
	private Stage stage;
	private Scene scene ;
	private Parent root ;
	

	 @FXML private Circle profileCircle;

	    @FXML private Label nameFielde;
	    @FXML private Label emailFielde;
	    @FXML private Label phonenumberFielde;
	    @FXML private Label birthdayFielde;
	    @FXML private Label dateFielde;

	    @FXML private Button Deleteb;
	    
	    @FXML
	    private ImageView houseIcon;
	    @FXML
	    private void goToStore(javafx.scene.input.MouseEvent event) {
	        try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/StoreScene.fxml"));
	            java.net.URL fxmlUrl = getClass().getResource("/StoreScene.fxml");
	            System.out.println("FXML URL = " + fxmlUrl);
	            Parent root = loader.load();
	            StoreController storeController = loader.getController();
	            storeController.setUserId(currentUserId);

	            Stage stage = (Stage) houseIcon.getScene().getWindow();
	            Scene scene = new Scene(root);
	            String css = this.getClass().getResource("application.css").toExternalForm();
	            scene.getStylesheets().add(css);
	            stage.setScene(scene);
	            stage.setResizable(false);
	            stage.centerOnScreen();
	            stage.show();

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }


	    private int currentUserId;

	    public void initialize() {
	        try {
	            java.net.URL imageUrl = getClass().getResource("/images/download.png");
	            if (imageUrl == null) {
	                System.out.println("Image not found! Check the path.");
	            } else {
	                Image img = new Image(imageUrl.toExternalForm());
	                profileCircle.setFill(new ImagePattern(img));
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    public void Edit(ActionEvent e) throws IOException {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Editscene.fxml"));
	        root = loader.load();
	        	        EditSceneController editController = loader.getController();
	        	        editController.initializeUserInfo(currentUserId);
	        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
	        scene = new Scene(root);
	        String css = this.getClass().getResource("application.css").toExternalForm();
	        scene.getStylesheets().add(css);
	        stage.setScene(scene);
	        stage.setResizable(false);
	        stage.centerOnScreen();
	        stage.show();
	    }
	    
	    @FXML
	    private void openPurchaseHistory(ActionEvent event) {
	        try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purchase_history.fxml"));
	            Parent root = loader.load();

	            PurchaseHistoryController controller = loader.getController();
	            controller.setUserId(currentUserId);
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    String css = this.getClass().getResource("application.css").toExternalForm();
    scene.getStylesheets().add(css);
    stage.setScene(scene);
    stage.getIcons().add(new Image(getClass().getResource("/images/icon.jpg").toExternalForm()));
    stage.setResizable(false);
    stage.centerOnScreen();
    stage.show();

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }


	    public void setUserId(int userId) {
	        this.currentUserId = userId;
	        System.out.println("UserID passed to profile: " + userId);
	        loadUserInfo();
	    }

	    private void loadUserInfo() {
	        String query = "SELECT Username, Firstname, Lastname, Email, phone_number, birthdate, CreatedAt FROM users WHERE UserID = ?";

	        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Games", "root", "souheil.2005");
	             PreparedStatement stmt = conn.prepareStatement(query)) {

	            stmt.setInt(1, currentUserId);
	            ResultSet rs = stmt.executeQuery();

	            if (rs.next()) {
	                String firstName = rs.getString("Firstname");
	                String lastName = rs.getString("Lastname");
	                String email = rs.getString("Email");
	                String phone = rs.getString("phone_number");
	                Date birthdate = rs.getDate("birthdate");
	                Timestamp createdAt = rs.getTimestamp("CreatedAt");

	                nameFielde.setText(firstName + " " + lastName);
	                emailFielde.setText(email);
	                phonenumberFielde.setText(phone);
	                birthdayFielde.setText(birthdate.toLocalDate().toString());
	                dateFielde.setText(createdAt.toLocalDateTime().toLocalDate().toString());
	            }

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    @FXML
	    private void logout(ActionEvent event) {
	        try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/f1.fxml"));
	        	root = loader.load();
			    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			    scene = new Scene(root);
			    String css = this.getClass().getResource("application.css").toExternalForm();
			    scene.getStylesheets().add(css);
			    stage.setScene(scene);
	            stage.getIcons().add(new Image(getClass().getResource("/images/icon.jpg").toExternalForm()));
	            stage.setResizable(false);
	            stage.centerOnScreen();
			    stage.show();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    @FXML
	    private void deleteAccount(ActionEvent event) {
	        System.out.println("Attempting to delete user with ID: " + currentUserId);

	        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
	        confirmDialog.setTitle("Delete Account");
	        confirmDialog.setHeaderText("Are you sure you want to delete your account?");
	        confirmDialog.setContentText("This action is irreversible!");

	        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
	            String query = "DELETE FROM users WHERE UserID = ?";

	            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Games", "root", "souheil.2005");
	                 PreparedStatement stmt = conn.prepareStatement(query)) {

	                stmt.setInt(1, currentUserId);
	                int rowsAffected = stmt.executeUpdate();

	                if (rowsAffected > 0) {
	                    System.out.println("User account deleted successfully.");

	                    Alert successDialog = new Alert(Alert.AlertType.INFORMATION);
	                    successDialog.setTitle("Account Deleted");
	                    successDialog.setHeaderText(null);
	                    successDialog.setContentText("Your account has been successfully deleted.");
	                    successDialog.showAndWait();

	                    logout(event);
	                } else {
	                    System.out.println("Account deletion failed. User not found.");
	                }

	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }    
	    }
}
