package services;

import entities.User;
import repositories.UserRepository;
import java.util.Optional;

public class AuthService {
    private UserRepository userRepository;
    private User currentUser;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean login(String username, String password) {
        Optional<User> userOpt = userRepository.authenticateUser(username, password);

        if (userOpt.isPresent()) {
            this.currentUser = userOpt.get();
            return true;
        }

        return false;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean hasPermission(String requiredRole) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.hasPermission(requiredRole);
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public boolean isStaffOrHigher() {
        return hasPermission("STAFF");
    }

    public boolean canViewAllRentals() {
        return hasPermission("STAFF");
    }

    public boolean canManageCars() {
        return hasPermission("STAFF");
    }
}