package entities;

public class Car {
    private int id;
    private String brand;
    private String model;
    private String licensePlate;
    private int year;
    private int categoryId;
    private double dailyPrice;
    private boolean isAvailable;

    private String categoryName;

    public Car() {}

    public Car(String brand, String model, String licensePlate, int year,
               int categoryId, double dailyPrice) {
        this.brand = brand;
        this.model = model;
        this.licensePlate = licensePlate;
        this.year = year;
        this.categoryId = categoryId;
        this.dailyPrice = dailyPrice;
        this.isAvailable = true;
    }

    // геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public double getDailyPrice() { return dailyPrice; }
    public void setDailyPrice(double dailyPrice) { this.dailyPrice = dailyPrice; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getFullName() {
        return brand + " " + model + " (" + year + ")";
    }
}