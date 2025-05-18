package application;

public class Purchase {
	private String title;
    private double price;
    private String purchaseDate;

    public Purchase(String title, double price, String purchaseDate) {
        this.title = title;
        this.price = price;
        this.purchaseDate = purchaseDate;
    }

    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public String getPurchaseDate() { return purchaseDate; }
}
