package repositories;

import database.DatabaseConnection;
import entities.Car;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CarRepository {
    public List<Car> getAllCarsWithCategory() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT c.*, cat.name as category_name, cat.description as category_description " +
                "FROM cars c " +
                "LEFT JOIN categories cat ON c.category_id = cat.id " +
                "ORDER BY c.brand, c.model";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cars.add(mapResultSetToCar(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting cars with category: " + e.getMessage());
        }
        return cars;
    }

    public List<Car> getAvailableCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT c.*, cat.name as category_name, cat.description as category_description " +
                "FROM cars c " +
                "LEFT JOIN categories cat ON c.category_id = cat.id " +
                "WHERE c.is_available = true " +
                "ORDER BY c.daily_price";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cars.add(mapResultSetToCar(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting available cars: " + e.getMessage());
        }
        return cars;
    }

    public List<Car> searchCars(String searchTerm) {
        List<Car> allCars = getAllCarsWithCategory();

        return allCars.stream()
                .filter(car ->
                        car.getBrand().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                car.getModel().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                car.getLicensePlate().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                (car.getCategoryName() != null &&
                                        car.getCategoryName().toLowerCase().contains(searchTerm.toLowerCase()))
                )
                .sorted(Comparator.comparing(Car::getDailyPrice))
                .toList();
    }

    public List<Car> getCarsByCategory(String categoryName) {
        List<Car> allCars = getAllCarsWithCategory();

        return allCars.stream()
                .filter(car ->
                        car.getCategoryName() != null &&
                                car.getCategoryName().equalsIgnoreCase(categoryName)
                )
                .sorted((c1, c2) -> Double.compare(c1.getDailyPrice(), c2.getDailyPrice()))  // Сортировка
                .toList();
    }

    public List<Car> getCarsByPriceRange(double minPrice, double maxPrice) {
        List<Car> allCars = getAllCarsWithCategory();

        return allCars.stream()
                .filter(car -> car.getDailyPrice() >= minPrice && car.getDailyPrice() <= maxPrice)
                .sorted(Comparator.comparing(Car::getDailyPrice))
                .toList();
    }

    public void printCarStatistics() {
        List<Car> allCars = getAllCarsWithCategory();

        if (allCars.isEmpty()) {
            System.out.println("No cars in database.");
            return;
        }

        long totalCars = allCars.size();
        long availableCars = allCars.stream().filter(Car::isAvailable).count();
        double avgPrice = allCars.stream().mapToDouble(Car::getDailyPrice).average().orElse(0);
        double maxPrice = allCars.stream().mapToDouble(Car::getDailyPrice).max().orElse(0);
        double minPrice = allCars.stream().mapToDouble(Car::getDailyPrice).min().orElse(0);

        System.out.println("\n=== Car Statistics ===");
        System.out.println("Total Cars: " + totalCars);
        System.out.println("Available Cars: " + availableCars);
        System.out.printf("Average Daily Price: $%.2f\n", avgPrice);
        System.out.printf("Most Expensive: $%.2f\n", maxPrice);
        System.out.printf("Cheapest: $%.2f\n", minPrice);

        System.out.println("\n=== Cars by Category ===");
        allCars.stream()
                .filter(car -> car.getCategoryName() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        Car::getCategoryName,
                        java.util.stream.Collectors.counting()
                ))
                .forEach((category, count) ->
                        System.out.printf("%-15s: %d cars\n", category, count)
                );
    }

    public void updateCarAvailability(int carId, boolean isAvailable) {
        String sql = "UPDATE cars SET is_available = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, isAvailable);
            pstmt.setInt(2, carId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating car availability: " + e.getMessage());
        }
    }

    // Получить машину по ID
    public Car getCarById(int id) {
        String sql = "SELECT c.*, cat.name as category_name, cat.description as category_description " +
                "FROM cars c " +
                "LEFT JOIN categories cat ON c.category_id = cat.id " +
                "WHERE c.id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCar(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error getting car by ID: " + e.getMessage());
        }
        return null;
    }

    private Car mapResultSetToCar(ResultSet rs) throws SQLException {
        Car car = new Car();
        car.setId(rs.getInt("id"));
        car.setBrand(rs.getString("brand"));
        car.setModel(rs.getString("model"));
        car.setLicensePlate(rs.getString("license_plate"));
        car.setYear(rs.getInt("year"));
        car.setCategoryId(rs.getInt("category_id"));
        car.setDailyPrice(rs.getDouble("daily_price"));
        car.setAvailable(rs.getBoolean("is_available"));

        if (rs.getString("category_name") != null) {
            car.setCategoryName(rs.getString("category_name"));
        }

        return car;
    }
}