package repositories;

import database.DatabaseConnection;
import entities.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY name";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));
                categories.add(category);
            }
        } catch (SQLException e) {
            System.out.println("Error getting categories: " + e.getMessage());
        }

        return categories;
    }
}//ну тут ничего интересного