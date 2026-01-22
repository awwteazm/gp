package repository;

import database.DatabaseConnection;
import entity.Rental;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RentalRepository {

    public void createRental(Rental rental) {
        String sql = "INSERT INTO rentals (car_id, customer_name, customer_email, start_date, end_date, total_price, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, rental.getCarId());
            pstmt.setString(2, rental.getCustomerName());
            pstmt.setString(3, rental.getCustomerEmail());
            pstmt.setDate(4, Date.valueOf(rental.getStartDate()));
            pstmt.setDate(5, Date.valueOf(rental.getEndDate()));
            pstmt.setDouble(6, rental.getTotalPrice());
            pstmt.setString(7, rental.getStatus());

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

    public List<Rental> getAllRentals() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rentals ORDER BY start_date DESC";

        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Rental rental = new Rental();
                rental.setId(rs.getInt("id"));
                rental.setCarId(rs.getInt("car_id"));
                rental.setCustomerName(rs.getString("customer_name"));
                rental.setCustomerEmail(rs.getString("customer_email"));
                rental.setStartDate(rs.getDate("start_date").toLocalDate());
                rental.setEndDate(rs.getDate("end_date").toLocalDate());
                rental.setTotalPrice(rs.getDouble("total_price"));
                rental.setStatus(rs.getString("status"));
                rental.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                rentals.add(rental);
            }
        } catch (SQLException e) {
            System.out.println("Error getting rentals: " + e.getMessage());
        }
        return rentals;
    }

    public void updateRentalStatus(int rentalId, String status) {
        String sql = "UPDATE rentals SET status = ? WHERE id = ?";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, rentalId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating rental status: " + e.getMessage());
        }
    }

    public boolean isCarAvailableForDates(int carId, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT COUNT(*) FROM rentals WHERE car_id = ? AND status = 'ACTIVE' " +
                "AND ((start_date <= ? AND end_date >= ?) OR " +
                "(start_date <= ? AND end_date >= ?) OR " +
                "(start_date >= ? AND end_date <= ?))";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, carId);
            pstmt.setDate(2, Date.valueOf(endDate));
            pstmt.setDate(3, Date.valueOf(startDate));
            pstmt.setDate(4, Date.valueOf(endDate));
            pstmt.setDate(5, Date.valueOf(startDate));
            pstmt.setDate(6, Date.valueOf(startDate));
            pstmt.setDate(7, Date.valueOf(endDate));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking car availability: " + e.getMessage());
        }
        return false;
    }

    public double calculateRentalPrice(int carId, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT daily_price FROM cars WHERE id = ?";
        double dailyPrice = 0;

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, carId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                dailyPrice = rs.getDouble("daily_price");
            }
        } catch (SQLException e) {
            System.out.println("Error calculating rental price: " + e.getMessage());
        }

        long days = endDate.toEpochDay() - startDate.toEpochDay() + 1;
        return dailyPrice * days;
    }
}