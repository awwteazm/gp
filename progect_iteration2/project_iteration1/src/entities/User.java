package entities;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String role;

    public User() {}

    public User(String username, String password, String email, String fullName, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    //геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public boolean isManager() {
        return "MANAGER".equals(role);
    }

    public boolean isStaff() {
        return "STAFF".equals(role);
    }

    public boolean isCustomer() {
        return "CUSTOMER".equals(role);
    }

    public boolean hasPermission(String requiredRole) {
        String[] hierarchy = {"CUSTOMER", "STAFF", "MANAGER", "ADMIN"};
        int userLevel = -1;
        int requiredLevel = -1;

        for (int i = 0; i < hierarchy.length; i++) {
            if (hierarchy[i].equals(this.role)) {
                userLevel = i;
            }
            if (hierarchy[i].equals(requiredRole)) {
                requiredLevel = i;
            }
        }

        return userLevel >= requiredLevel;
    }
}