package application;
	
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;


public class Main extends Application {
	@Override 
	public void start(Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/f1.fxml"));
			Parent root = loader.load();
Scene scene = new Scene(root);
String css = this.getClass().getResource("application.css").toExternalForm();
scene.getStylesheets().add(css);
 stage.setScene(scene);
 stage.getIcons().add(new Image(getClass().getResource("/images/icon.jpg").toExternalForm()));
 stage.setResizable(false);
 stage.centerOnScreen();
			stage.show();
			stage.setOnCloseRequest(event -> {
				event.consume();
			logout(stage);  
			});

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void logout(Stage stage) {
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("LOG OUT");
		alert.setHeaderText("You're about to logout!!!");
		alert.setContentText("Do you want to save before exiting ? : ");
		if(alert.showAndWait().get()== ButtonType.OK) {
		    System.out.println("LOG out");
		    stage.close();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
