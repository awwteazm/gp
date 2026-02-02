package repositories;

import database.DatabaseConnection;
import entities.Rental;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RentalRepository {

    public List<Rental> getAllRentalsWithDetails() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT r.*, " +
                "c.brand as car_brand, c.model as car_model, c.license_plate as car_license, " +
                "c.daily_price as car_price, c.category_id as car_category_id, " +
                "u.username, u.email as user_email, u.full_name as user_name " +
                "FROM rentals r " +
                "JOIN cars c ON r.car_id = c.id " +
                "JOIN users u ON r.user_id = u.id " +
                "ORDER BY r.created_at DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rentals.add(mapResultSetToRental(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting rentals with details: " + e.getMessage());
        }
        return rentals;
    }

    public List<Rental> getRentalsByUser(int userId) {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT r.*, " +
                "c.brand as car_brand, c.model as car_model, c.license_plate as car_license " +
                "FROM rentals r " +
                "JOIN cars c ON r.car_id = c.id " +
                "WHERE r.user_id = ? " +
                "ORDER BY r.start_date DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                rentals.add(mapResultSetToRental(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting rentals by user: " + e.getMessage());
        }
        return rentals;
    }

    public List<Rental> searchRentals(String searchTerm) {
        List<Rental> allRentals = getAllRentalsWithDetails();

        return allRentals.stream()
                .filter(rental ->
                        rental.getCustomerName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                rental.getCustomerEmail().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                rental.getCarLicensePlate().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                rental.getUsername().toLowerCase().contains(searchTerm.toLowerCase())
                )
                .sorted(Comparator.comparing(Rental::getStartDate).reversed())
                .toList();
    }

    public List<Rental> getActiveRentals() {
        List<Rental> allRentals = getAllRentalsWithDetails();
        LocalDate today = LocalDate.now();

        return allRentals.stream()
                .filter(rental -> "ACTIVE".equals(rental.getStatus()))
                .filter(rental ->
                        !rental.getStartDate().isAfter(today) &&
                                !rental.getEndDate().isBefore(today)
                )
                .sorted(Comparator.comparing(Rental::getEndDate))
                .toList();
    }

    public List<Rental> getOverdueRentals() {
        List<Rental> allRentals = getAllRentalsWithDetails();
        LocalDate today = LocalDate.now();

        return allRentals.stream()
                .filter(rental -> "ACTIVE".equals(rental.getStatus()))
                .filter(rental -> rental.getEndDate().isBefore(today))
                .sorted(Comparator.comparing(Rental::getEndDate))
                .toList();
    }

    public void printRentalStatistics() {
        List<Rental> allRentals = getAllRentalsWithDetails();

        if (allRentals.isEmpty()) {
            System.out.println("No rentals in database.");
            return;
        }

        long totalRentals = allRentals.size();
        long activeRentals = allRentals.stream().filter(r -> "ACTIVE".equals(r.getStatus())).count();
        long completedRentals = allRentals.stream().filter(r -> "COMPLETED".equals(r.getStatus())).count();
        double totalRevenue = allRentals.stream().mapToDouble(Rental::getTotalPrice).sum();
        double avgRentalPrice = allRentals.stream().mapToDouble(Rental::getTotalPrice).average().orElse(0);

        System.out.println("\n=== Rental Statistics ===");
        System.out.println("Total Rentals: " + totalRentals);
        System.out.println("Active Rentals: " + activeRentals);
        System.out.println("Completed Rentals: " + completedRentals);
        System.out.printf("Total Revenue: $%.2f\n", totalRevenue);
        System.out.printf("Average Rental Price: $%.2f\n", avgRentalPrice);

        System.out.println("\n=== Monthly Statistics ===");
        allRentals.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        rental -> rental.getStartDate().getMonth().toString(),
                        java.util.stream.Collectors.summarizingDouble(Rental::getTotalPrice)
                ))
                .forEach((month, stats) ->
                        System.out.printf("%-15s: %d rentals, $%.2f revenue\n",
                                month, stats.getCount(), stats.getSum())
                );
    }

    public boolean isCarAvailableForDates(int carId, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT COUNT(*) FROM rentals " +
                "WHERE car_id = ? AND status IN ('PENDING', 'ACTIVE') " +
                "AND ((start_date <= ? AND end_date >= ?) OR " +
                "(start_date <= ? AND end_date >= ?))";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, carId);
            pstmt.setDate(2, Date.valueOf(endDate));
            pstmt.setDate(3, Date.valueOf(startDate));
            pstmt.setDate(4, Date.valueOf(endDate));
            pstmt.setDate(5, Date.valueOf(startDate));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking car availability: " + e.getMessage());
        }
        return false;
    }

    public void createRental(Rental rental) {
        String sql = "INSERT INTO rentals (car_id, user_id, customer_name, customer_email, " +
                "start_date, end_date, total_price, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, rental.getCarId());
            pstmt.setInt(2, rental.getUserId());
            pstmt.setString(3, rental.getCustomerName());
            pstmt.setString(4, rental.getCustomerEmail());
            pstmt.setDate(5, Date.valueOf(rental.getStartDate()));
            pstmt.setDate(6, Date.valueOf(rental.getEndDate()));
            pstmt.setDouble(7, rental.getTotalPrice());
            pstmt.setString(8, rental.getStatus());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        rental.setId(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error creating rental: " + e.getMessage());
        }
    }

    public void updateRentalStatus(int rentalId, String status) {
        String sql = "UPDATE rentals SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, rentalId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating rental status: " + e.getMessage());
        }
    }

    private Rental mapResultSetToRental(ResultSet rs) throws SQLException {
        Rental rental = new Rental();
        rental.setId(rs.getInt("id"));
        rental.setCarId(rs.getInt("car_id"));
        rental.setUserId(rs.getInt("user_id"));
        rental.setCustomerName(rs.getString("customer_name"));
        rental.setCustomerEmail(rs.getString("customer_email"));
        rental.setStartDate(rs.getDate("start_date").toLocalDate());
        rental.setEndDate(rs.getDate("end_date").toLocalDate());
        rental.setTotalPrice(rs.getDouble("total_price"));
        rental.setStatus(rs.getString("status"));

        rental.setCarBrand(rs.getString("car_brand"));
        rental.setCarModel(rs.getString("car_model"));
        rental.setCarLicensePlate(rs.getString("car_license"));
        rental.setUsername(rs.getString("username"));

        return rental;
    }
}