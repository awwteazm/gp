package repositories;

import database.DatabaseConnection;
import entities.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {

    public void createUser(User user) {
        String sql = "INSERT INTO users (username, password, email, full_name, role) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty");
            }
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty");
            }
            if (user.getEmail() == null || !user.getEmail().contains("@")) {
                throw new IllegalArgumentException("Invalid email");
            }//агай если вы это смотрите то тут валидация

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getFullName());
            pstmt.setString(5, user.getRole());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setId(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }

    public Optional<User> authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            System.out.println("Error authenticating user: " + e.getMessage());
        }

        return Optional.empty();
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Error getting all users: " + e.getMessage());
        }

        return users;
    }

    public List<User> getUsersByRole(String role) {
        List<User> allUsers = getAllUsers();
        return allUsers.stream()
                .filter(user -> role.equals(user.getRole()))
                .toList();
    }
}