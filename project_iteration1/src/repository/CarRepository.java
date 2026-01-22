package repository;

import database.DatabaseConnection;
import entity.Car;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarRepository {

    public void createCar(Car car) {
        String sql = "INSERT INTO cars (brand, model, license_plate, year, daily_price, is_available, mileage) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, car.getBrand());
            pstmt.setString(2, car.getModel());
            pstmt.setString(3, car.getLicensePlate());
            pstmt.setInt(4, car.getYear());
            pstmt.setDouble(5, car.getDailyPrice());
            pstmt.setBoolean(6, car.isAvailable());
            pstmt.setInt(7, car.getMileage());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        car.setId(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error creating car: " + e.getMessage());
        }
    }

    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars ORDER BY id";

        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Car car = new Car();
                car.setId(rs.getInt("id"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setLicensePlate(rs.getString("license_plate"));
                car.setYear(rs.getInt("year"));
                car.setDailyPrice(rs.getDouble("daily_price"));
                car.setAvailable(rs.getBoolean("is_available"));
                car.setMileage(rs.getInt("mileage"));
                car.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                cars.add(car);
            }
        } catch (SQLException e) {
            System.out.println("Error getting cars: " + e.getMessage());
        }
        return cars;
    }

    public List<Car> getAvailableCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars WHERE is_available = true ORDER BY daily_price";

        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Car car = new Car();
                car.setId(rs.getInt("id"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setLicensePlate(rs.getString("license_plate"));
                car.setYear(rs.getInt("year"));
                car.setDailyPrice(rs.getDouble("daily_price"));
                car.setAvailable(rs.getBoolean("is_available"));
                car.setMileage(rs.getInt("mileage"));
                cars.add(car);
            }
        } catch (SQLException e) {
            System.out.println("Error getting available cars: " + e.getMessage());
        }
        return cars;
    }

    public void updateCarAvailability(int carId, boolean isAvailable) {
        String sql = "UPDATE cars SET is_available = ? WHERE id = ?";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, isAvailable);
            pstmt.setInt(2, carId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating car availability: " + e.getMessage());
        }
    }

    public Car getCarById(int id) {
        String sql = "SELECT * FROM cars WHERE id = ?";
        Car car = null;

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                car = new Car();
                car.setId(rs.getInt("id"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setLicensePlate(rs.getString("license_plate"));
                car.setYear(rs.getInt("year"));
                car.setDailyPrice(rs.getDouble("daily_price"));
                car.setAvailable(rs.getBoolean("is_available"));
                car.setMileage(rs.getInt("mileage"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting car by ID: " + e.getMessage());
        }
        return car;
    }
}