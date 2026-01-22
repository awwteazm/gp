package entity;

import java.time.LocalDateTime;

public class Car {
    private int id;
    private String brand;
    private String model;
    private String licensePlate;
    private int year;
    private double dailyPrice;
    private boolean isAvailable;
    private int mileage;
    private LocalDateTime createdAt;

    public Car() {}

    public Car(String brand, String model, String licensePlate, int year, double dailyPrice) {
        this.brand = brand;
        this.model = model;
        this.licensePlate = licensePlate;
        this.year = year;
        this.dailyPrice = dailyPrice;
        this.isAvailable = true;
        this.mileage = 0;
        this.createdAt = LocalDateTime.now();
    }

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

    public double getDailyPrice() { return dailyPrice; }
    public void setDailyPrice(double dailyPrice) { this.dailyPrice = dailyPrice; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public int getMileage() { return mileage; }
    public void setMileage(int mileage) { this.mileage = mileage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}