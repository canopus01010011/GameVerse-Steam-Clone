package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PurchaseHistoryController {
	@FXML private TableView<Purchase> purchaseTable;
    @FXML private TableColumn<Purchase, String> titleCol;
    @FXML private TableColumn<Purchase, Double> priceCol;
    @FXML private TableColumn<Purchase, String> dateCol;

    private int userId;
    private Stage stage;
	private Scene scene ;
	private Parent root ;
    public void setUserId(int userId) {
        this.userId = userId;
        loadPurchaseHistory();
    }

    private void loadPurchaseHistory() {
        ObservableList<Purchase> purchases = FXCollections.observableArrayList();

        String query = """
            SELECT g.Title, g.Price, p.PurchaseDate 
            FROM purchases p
            JOIN games g ON p.GameID = g.GameID
            WHERE p.UserID = ?
            """;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Games", "root", "souheil.2005");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                purchases.add(new Purchase(
                    rs.getString("Title"),
                    rs.getDouble("Price"),
                    rs.getString("PurchaseDate")
                ));
            }

            titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
            priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
            dateCol.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));

            purchaseTable.setItems(purchases);
           /* purchaseTable.setRowFactory(tv -> {
                TableRow<Purchase> row = new TableRow<>();
                row.setStyle("-fx-opacity: 0;");
                Timeline fade = new Timeline(
                    new KeyFrame(Duration.seconds(0.1), new KeyValue(row.opacityProperty(), 1))
                );
                row.itemProperty().addListener((obs, oldItem, newItem) -> {
                    if (newItem != null) fade.playFromStart();
                });
                return row;
            });
*/

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void goBack(ActionEvent e) throws IOException {
    	 FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfilScene.fxml"));
			root = loader.load();
			 ProfileController profileController = loader.getController();
		        profileController.setUserId(userId);
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
}
