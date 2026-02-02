package entities;

import java.time.LocalDate;

public class Rental {
    private int id;
    private int carId;
    private int userId;
    private String customerName;
    private String customerEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalPrice;
    private String status;

    private String carBrand;
    private String carModel;
    private String carLicensePlate;
    private String username;

    public Rental() {}

    public Rental(int carId, int userId, String customerName, String customerEmail,
                  LocalDate startDate, LocalDate endDate, double totalPrice) {
        this.carId = carId;
        this.userId = userId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.status = "PENDING";
    }

    // геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCarId() { return carId; }
    public void setCarId(int carId) { this.carId = carId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCarBrand() { return carBrand; }
    public void setCarBrand(String carBrand) { this.carBrand = carBrand; }

    public String getCarModel() { return carModel; }
    public void setCarModel(String carModel) { this.carModel = carModel; }

    public String getCarLicensePlate() { return carLicensePlate; }
    public void setCarLicensePlate(String carLicensePlate) { this.carLicensePlate = carLicensePlate; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}