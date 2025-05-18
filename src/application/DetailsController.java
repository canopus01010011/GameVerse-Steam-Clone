package application;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;

public class DetailsController {
	 @FXML 
	 private ImageView gameImage;
	    @FXML 
	    private Label titleLabel, releaseLabel, priceLabel, categoryLabel, ratingLabel;
	    @FXML private Text descText, requirementsText;
	    private Game currentGame;
	    private int currentUserId;

	    public void setGameDetails(Game currentGame) {
	        titleLabel.setText(currentGame.getTitle());
	        descText.setText(currentGame.getDescription());
	        releaseLabel.setText(currentGame.getReleaseDate().toString());
	        priceLabel.setText(currentGame.getPrice() + " DA");
	        categoryLabel.setText(currentGame.getCategory());
	        ratingLabel.setText(String.valueOf(currentGame.getRating()));
	        requirementsText.setText(currentGame.getSystemRequirements());

	        URL imageUrl = getClass().getResource("/" + currentGame.getImagePath());
	        if (imageUrl != null) {
	            gameImage.setImage(new Image(imageUrl.toExternalForm()));
	        } else {
	            System.out.println("Image not found for: " + currentGame.getImagePath());
	        }
	    }

	    @FXML
	    private void goBack(ActionEvent event) {
	        try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/StoreScene.fxml"));
	            Parent root = loader.load();

	            StoreController storeController = loader.getController();
	            storeController.setUserId(currentUserId);
	            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
	            Scene scene = new Scene(root);
	            String css = this.getClass().getResource("application.css").toExternalForm();
	            scene.getStylesheets().add(css);
	            stage.getIcons().add(new Image(getClass().getResource("/images/icon.jpg").toExternalForm()));
	            stage.setScene(scene);
	            stage.setResizable(false);
	            stage.centerOnScreen();
	            stage.show();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    public void setUserId(int userId) {
	        this.currentUserId = userId;
	    }
	
	}

