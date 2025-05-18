package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Sceneprofilcontroller {

	@FXML
	private Button logoutb;
	
	@FXML
	private AnchorPane scenePane;
	
	Stage stage ;
	
	
	public void logout(ActionEvent e) {
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("LOG OUT");
		alert.setHeaderText("You're about to logout!!!");
		alert.setContentText("Do you want to save before exiting ? : ");
		if(alert.showAndWait().get()== ButtonType.OK) {
		    stage = (Stage) scenePane.getScene().getWindow();
		    System.out.println("LOG out");
		    stage.close();
		}
		
	}
	
	
}
