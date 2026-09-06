package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SignupController {
	private Stage stage;
	private Scene scene ;
	private Parent root ;
	@FXML private TextField firstNameField;
	@FXML private TextField lastNameField;
	@FXML private TextField usernameField;
	@FXML private TextField emailField;
	@FXML private TextField phoneField;
	@FXML private PasswordField passwordField;
	@FXML private DatePicker birthDatePicker;
	@FXML private RadioButton maleuser;
	@FXML private RadioButton femaleuser;
	
	@FXML private Label nameFielde;
    @FXML private Label emailFielde;
    @FXML private Label phonenumberFielde;
    @FXML private Label birthdayFielde;
    @FXML private Label dateFielde;
    
    @FXML private ToggleGroup genderGroup;
    
    @FXML private CheckBox termsCheckBox;


    private int currentUserId;

	private int calculateAgeFromBirthDate(LocalDate birthDate) {
	    if (birthDate == null) return 0;
	    return Period.between(birthDate, LocalDate.now()).getYears();
	}
	private void showAlert(Alert.AlertType type, String title, String message) {
	    Alert alert = new Alert(type);
	    alert.setTitle(title);
	    alert.setHeaderText(null);
	    alert.setContentText(message);
	    alert.showAndWait();
	}
	@FXML
	private void handleSignUp(ActionEvent event) throws IOException {
	    String firstName = firstNameField.getText();
	    String lastName = lastNameField.getText();
	    String username = usernameField.getText();
	    String email = emailField.getText();
	    String phone = phoneField.getText();
	    String password = passwordField.getText();
	    LocalDate birthDate = birthDatePicker.getValue();
	    Toggle selectedToggle = genderGroup.getSelectedToggle();
	    if (selectedToggle == null) {
	        showAlert(Alert.AlertType.ERROR, "Missing Info", "Please select a gender.");
	        return;
	    }
	    String gender = ((RadioButton) selectedToggle).getText();

	    if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() ||
	            email.isEmpty() || phone.isEmpty() || password.isEmpty() ||
	            birthDate == null || gender == null) {
	        showAlert(Alert.AlertType.ERROR, "Missing Info", "Please fill in all the fields.");
	        return;
	    }

	    int age = Period.between(birthDate, LocalDate.now()).getYears();
	    if (age < 18) {
	        showAlert(Alert.AlertType.WARNING, "Too Young", "You must be at least 18 years old to sign up.");
	        return;
	    }
	    
	    if (!firstName.matches("[a-zA-Z]+") || !lastName.matches("[a-zA-Z]+")) {
	        showAlert(Alert.AlertType.ERROR, "Invalid Name", "First and Last names should contain only letters.");
	        return;
	    }
	    
	    if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
	        showAlert(Alert.AlertType.ERROR, "Invalid Email", "Please enter a valid email address.");
	        return;
	    }
	    if (!phone.matches("^(05|06|07)[0-9]{8}$")) {
	        showAlert(Alert.AlertType.ERROR, "Invalid Phone Number", "Phone number must be 10 digits starting with 05, 06, or 07.");
	        return;
	    }
	    if (password.length() < 8) {
	        showAlert(Alert.AlertType.ERROR, "Weak Password", "Password must be at least 8 characters long.");
	        return;
	    }
	    
	    if (!termsCheckBox.isSelected()) {
	        showAlert(Alert.AlertType.ERROR, "Terms Not Accepted", "You must accept all rules and conditions to sign up.");
	        return;
	    }


	    String sql = "INSERT INTO users (Username, Firstname, Lastname, Email, PasswordHash, birthdate, phone_number) " +
	                 "VALUES (?, ?, ?, ?, ?, ?, ?)";

	    try (Connection conn = DBConnection.getConnection();
	    		PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        stmt.setString(1, username);
	        stmt.setString(2, firstName);
	        stmt.setString(3, lastName);
	        stmt.setString(4, email);
	        stmt.setString(5, password);
	        stmt.setDate(6, Date.valueOf(birthDate));
	        stmt.setInt(7, Integer.parseInt(phone));

	        int rows = stmt.executeUpdate();
	        if (rows > 0) {
	        	ResultSet generatedKeys = stmt.getGeneratedKeys();
	        	if (generatedKeys.next()) {
	        	    int newUserId = generatedKeys.getInt(1);
	        	    System.out.println("New User ID: " + newUserId);
	        	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfilScene.fxml"));
	        	    root = loader.load();
	        	    ProfileController profileController = loader.getController();
	        	    profileController.setUserId(newUserId);
	        	    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	        	    scene = new Scene(root);
	        	    String css = this.getClass().getResource("application.css").toExternalForm();
	        	    scene.getStylesheets().add(css);
	        	    stage.setScene(scene);
		            stage.getIcons().add(new Image(getClass().getResource("/images/icon.jpg").toExternalForm()));
		            stage.setResizable(false);
		            stage.centerOnScreen();
	        	    stage.show();
	        	}
	        	
	            System.out.println("User registered successfully!");
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        showAlert(Alert.AlertType.ERROR, "Database Error", "Could not create your account. Please try again.");
	    }
	}
	
	 public void setUserId(int userId) {
	        this.currentUserId = userId;
	        System.out.println("UserID passed to profile: " + userId);
	        loadUserInfo();
	    }

	    private void loadUserInfo() {
	        String query = "SELECT Username, Firstname, Lastname, Email, phone_number, birthdate, CreatedAt FROM users WHERE UserID = ?";

	        try (Connection conn = DBConnection.getConnection();
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
	}
