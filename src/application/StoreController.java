package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StoreController {
	 @FXML private ListView<VBox> gameListView;
	    @FXML private TextField searchField;
	    
	    private int currentUserId;
	    private Game id;
	    private Stage stage;
		private Scene scene ;
		private Parent root ;

		@FXML private javafx.scene.control.ComboBox<String> categoryFilter;

	    private ObservableList<VBox> gameItems = FXCollections.observableArrayList();

	    public void initialize() {
	        loadGames();
	        loadCategories();
	        categoryFilter.setOnAction(e -> handleSearch());
	    }

	    private void loadGames() {
	        gameItems.clear();

	        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Games", "root", "souheil.2005");
	             Statement stmt = conn.createStatement();
	             ResultSet rs = stmt.executeQuery("SELECT * FROM games")) {

	        	while (rs.next()) {
	        	    int gameId = rs.getInt("GameID");

	        	    Game game = new Game(
	        	        gameId,
	        	        rs.getString("Title"),
	        	        rs.getString("Description"),
	        	        rs.getDouble("Price"),
	        	        rs.getString("Category"),
	        	        rs.getDate("ReleaseDate").toString(),
	        	        rs.getDouble("Rating"),
	        	        rs.getString("SystemRequirements"),
	        	        rs.getString("ImagePath")
	        	    );

	        	    VBox card = new VBox(5);
	        	    card.setStyle("-fx-background-color: #2a475e; -fx-padding: 10; -fx-background-radius: 10;");
	        	  Image image = new Image(getClass().getResource("/" + game.getImagePath()).toExternalForm());
	        	    ImageView imageView = new ImageView(image);
	        	    imageView.setFitWidth(200);
	        	    imageView.setFitHeight(250);

	        	    Label title = new Label(game.getTitle());
	        	    title.setStyle("-fx-font-size: 18px; -fx-text-fill: #66c0f4;");

	        	    Label desc = new Label(game.getDescription());
	        	    desc.setWrapText(true);
	        	    desc.setStyle("-fx-text-fill: white;");

	        	    Label price = new Label("💵 " + game.getPrice() + " DA");
	        	    price.setStyle("-fx-text-fill: #66c0f4; -fx-font-weight: bold;");

	        	    Button buyBtn = new Button("Buy");
	        	    buyBtn.setStyle("-fx-background-color: #66c0f4; -fx-text-fill: #1b2838;");
	        	    buyBtn.setOnAction(e -> buyGame(game.getGameId()));

	        	    Button detailsBtn = new Button("Details");
	        	    detailsBtn.setStyle("-fx-background-color: #66c0f4; -fx-text-fill: #1b2838;");
	        	    detailsBtn.setOnAction(e -> DetailsScene(game));

	        	    card.getChildren().addAll(imageView, title, desc, price, buyBtn, detailsBtn);
	        	    gameItems.add(card);
		        	gameListView.setItems(gameItems);

	        	}

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    
	    private void loadCategories() {
	        ObservableList<String> categories = FXCollections.observableArrayList();
	        categories.add("All Categories");
	        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Games", "root", "souheil.2005");
	             Statement stmt = conn.createStatement();
	             ResultSet rs = stmt.executeQuery("SELECT DISTINCT Category FROM games")) {

	            while (rs.next()) {
	                categories.add(rs.getString("Category"));
	            }
	         
				categoryFilter.setItems(categories);
	            categoryFilter.getSelectionModel().selectFirst();

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }


	    private void buyGame(int gameId) {
	        System.out.println("Buying game ID: " + gameId);
	        if (currentUserId == 0) {
	            System.out.println("No logged-in user.");
	            return;
	        }

	        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Games", "root", "souheil.2005")) {

	            String checkQuery = "SELECT * FROM purchases WHERE UserID = ? AND GameID = ?";
	            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
	                checkStmt.setInt(1, currentUserId);
	                checkStmt.setInt(2, gameId);

	                ResultSet rs = checkStmt.executeQuery();
	                if (rs.next()) {
	                    showAlert("Purchase Failed!", "You already own this game.");
	                    return;
	                }
	            }
	            String insertQuery = "INSERT INTO purchases (UserID, GameID, PurchaseDate) VALUES (?, ?, NOW())";
	            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
	                insertStmt.setInt(1, currentUserId);
	                insertStmt.setInt(2, gameId);

	                int affectedRows = insertStmt.executeUpdate();

	                if (affectedRows > 0) {
	                    showAlert("Purchase Successful!", "The game has been added to your library.");
	                } else {
	                    showAlert("Purchase Failed!", "Could not add the game to your library.");
	                }
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	            showAlert("Error", "An error occurred while processing the purchase.");
	        }
	    }


	    @FXML
	    private void handleSearch() {
	    	 String keyword = searchField.getText().trim().toLowerCase();
	    	    String selectedCategory = categoryFilter.getValue();

	    	    gameItems.clear();

	    	    String sql = "SELECT * FROM games WHERE LOWER(Title) LIKE ?";
	    	    if (selectedCategory != null && !selectedCategory.equals("All Categories")) {
	    	        sql += " AND Category = ?";
	    	    }

	    	    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Games", "root", "souheil.2005");
	    	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	    	        stmt.setString(1, "%" + keyword + "%");
	    	        if (selectedCategory != null && !selectedCategory.equals("All Categories")) {
	    	            stmt.setString(2, selectedCategory);
	    	        }

	    	        ResultSet rs = stmt.executeQuery();

	    	        while (rs.next()) {
	    	            int gameId = rs.getInt("GameID");

	    	            Game game = new Game(
	    	                gameId,
	    	                rs.getString("Title"),
	    	                rs.getString("Description"),
	    	                rs.getDouble("Price"),
	    	                rs.getString("Category"),
	    	                rs.getDate("ReleaseDate").toString(),
	    	                rs.getDouble("Rating"),
	    	                rs.getString("SystemRequirements"),
	    	                rs.getString("ImagePath")
	    	            );

	    	            VBox card = new VBox(5);
	    	            card.setStyle("-fx-background-color: #2a475e; -fx-padding: 10; -fx-background-radius: 10;");

	    	            java.net.URL imageUrl = getClass().getResource("/" + game.getImagePath());
	    	            if (imageUrl == null) {
	    	                System.out.println("Image not found: " + game.getImagePath());
	    	                continue;
	    	            }
	    	            Image image = new Image(imageUrl.toExternalForm());
	    	            ImageView imageView = new ImageView(image);
	    	            imageView.setFitWidth(200);
	    	            imageView.setFitHeight(120);

	    	            Label title = new Label(game.getTitle());
	    	            title.setStyle("-fx-font-size: 18px; -fx-text-fill: #66c0f4;");
	    	            Label desc = new Label(game.getDescription());
	    	            desc.setWrapText(true);
	    	            desc.setStyle("-fx-text-fill: white;");
	    	            Label price = new Label("💵 " + game.getPrice() + " DA");
	    	            price.setStyle("-fx-text-fill: #66c0f4; -fx-font-weight: bold;");

	    	            Button buyBtn = new Button("Buy");
	    	            buyBtn.setStyle("-fx-background-color: #66c0f4; -fx-text-fill: #1b2838;");
	    	            buyBtn.setOnAction(e -> buyGame(game.getGameId()));

	    	            Button detailsBtn = new Button("Details");
	    	            detailsBtn.setStyle("-fx-background-color: #66c0f4; -fx-text-fill: #1b2838;");
	    	            detailsBtn.setOnAction(e -> DetailsScene(game));

	    	            card.getChildren().addAll(imageView, title, desc, price, buyBtn, detailsBtn);
	    	            gameItems.add(card);
	    	        }

	    	        gameListView.setItems(gameItems);

	    	    } catch (Exception e) {
	    	        e.printStackTrace();
	    	    }
	    }
	    
	    @FXML
	    private void goBack(ActionEvent event) {
	        try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfilScene.fxml"));
	            Parent root = loader.load();
	            
	            ProfileController profileController = loader.getController();
	            profileController.setUserId(currentUserId);

	
	            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	            Scene scene = new Scene(root);
	            stage.getIcons().add(new Image(getClass().getResource("/images/icon.jpg").toExternalForm()));

	            String css = this.getClass().getResource("application.css").toExternalForm();
	            scene.getStylesheets().add(css);
	            stage.setScene(scene);
	            stage.centerOnScreen();

	            stage.show();

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    public void setUserId(int userId) {
	        this.currentUserId = userId;
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
    stage.centerOnScreen();

    stage.show();

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    private void showAlert(String title, String message) {
	        Alert alert = new Alert(Alert.AlertType.INFORMATION);
	        alert.setTitle(title);
	        alert.setHeaderText(null);
	        alert.setContentText(message);
	        alert.showAndWait();
	    }
	    private void DetailsScene(Game game) {
	        try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsScene.fxml"));
	            Parent root = loader.load();
	            DetailsController detailsController = loader.getController();
	            detailsController.setGameDetails(game);
	            detailsController.setUserId(currentUserId);

	            Stage stage = (Stage) gameListView.getScene().getWindow();
	            Scene scene = new Scene(root);
	            stage.getIcons().add(new Image(getClass().getResource("/images/icon.jpg").toExternalForm()));

	            String css = this.getClass().getResource("application.css").toExternalForm();
	            scene.getStylesheets().add(css);
	            stage.setScene(scene);
	            stage.setResizable(false);
	            stage.centerOnScreen();
	            stage.show();
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }


}
