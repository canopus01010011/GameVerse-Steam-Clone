package application;

import java.sql.Date;

public class Game {
	 private int gameId;
	    private String title;
	    private String description;
	    private double price;
	    private String category;
	    private String releaseDate;
	    private double rating;
	    private String systemRequirements;
	    private String imagePath;

	    public Game(int gameId, String title, String description, double price, String category, String releaseDate,
	                double rating, String systemRequirements, String imagePath) {
	        this.gameId = gameId;
	        this.title = title;
	        this.description = description;
	        this.price = price;
	        this.category = category;
	        this.releaseDate = releaseDate;
	        this.rating = rating;
	        this.systemRequirements = systemRequirements;
	        this.imagePath = imagePath;
	    }

	    public int getGameId() { return gameId; }
	    public String getTitle() { return title; }
	    public String getDescription() { return description; }
	    public double getPrice() { return price; }
	    public String getCategory() { return category; }
	    public String getReleaseDate() { return releaseDate; }
	    public double getRating() { return rating; }
	    public String getSystemRequirements() { return systemRequirements; }
	    public String getImagePath() { return imagePath; }
	}
