import controllers.CarController;
import controllers.RentalController;
import database.DatabaseConnection;
import entities.User;
import repositories.*;
import services.AuthService;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("   CAR MANAGEMENT SYSTEM (CARSHARING)   ");
        System.out.println("=========================================\n");

        DatabaseConnection dbConnection = DatabaseConnection.getInstance();

        UserRepository userRepository = new UserRepository();
        CarRepository carRepository = new CarRepository();
        CategoryRepository categoryRepository = new CategoryRepository();
        RentalRepository rentalRepository = new RentalRepository();

        AuthService authService = new AuthService(userRepository);

        Scanner scanner = new Scanner(System.in);

        CarController carController = new CarController(carRepository, authService, scanner);
        RentalController rentalController = new RentalController(rentalRepository, carRepository, authService, scanner);

        boolean running = true;

        while (running) {
            displayMainMenu(authService);
            System.out.print("Select option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        login(userRepository, authService, scanner);
                        break;
                    case 2:
                        registerUser(userRepository, scanner);
                        break;
                    case 3:
                        logout(authService);
                        break;
                    case 4:
                        carController.displayAvailableCars();
                        break;
                    case 5:
                        carController.searchCars();
                        break;
                    case 6:
                        carController.listCarsByCategory();
                        break;
                    case 7:
                        carController.listCarsByPriceRange();
                        break;
                    case 8:
                        carController.showCarDetails();
                        break;
                    case 9:
                        rentalController.createNewRental();
                        break;
                    case 10:
                        rentalController.displayUserRentals();
                        break;
                    case 11:
                        rentalController.cancelRental();
                        break;
                    case 12:
                        rentalController.calculateRentalCost();
                        break;
                    case 13:
                        if (authService.isStaffOrHigher()) {
                            carManagementMenu(carController, scanner);
                        } else {
                            System.out.println("Access denied. Staff role required.");
                        }
                        break;
                    case 14:
                        if (authService.isStaffOrHigher()) {
                            rentalManagementMenu(rentalController, scanner);
                        } else {
                            System.out.println("Access denied. Staff role required.");
                        }
                        break;
                    case 15:
                        running = false;
                        System.out.println("Thank you for using Car Management System!");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println();
        }

        scanner.close();
        dbConnection.closeConnection();
    }

    private static void displayMainMenu(AuthService authService) {
        System.out.println("\n===== MAIN MENU =====");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Logout");
        System.out.println("4. View Available Cars");
        System.out.println("5. Search Cars");
        System.out.println("6. View Cars by Category");
        System.out.println("7. View Cars by Price Range");
        System.out.println("8. View Car Details");
        System.out.println("9. Create New Rental");
        System.out.println("10. View My Rentals");
        System.out.println("11. Cancel Rental");
        System.out.println("12. Calculate Rental Cost");

        if (authService.isStaffOrHigher()) {
            System.out.println("13. Car Management (Staff)");
            System.out.println("14. Rental Management (Staff)");
        }

        System.out.println("15. Exit");

        if (authService.isLoggedIn()) {
            System.out.println("\nLogged in as: " + authService.getCurrentUser().getUsername() +
                    " (" + authService.getCurrentUser().getRole() + ")");
        }
    }

    private static void carManagementMenu(CarController carController, Scanner scanner) {
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("\n===== CAR MANAGEMENT =====");
            System.out.println("1. View All Cars");
            System.out.println("2. Update Car Status");
            System.out.println("3. Show Car Statistics");
            System.out.println("4. Back to Main Menu");
            System.out.print("Select option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    carController.displayAllCars();
                    break;
                case 2:
                    carController.updateCarStatus();
                    break;
                case 3:
                    carController.showCarStatistics();
                    break;
                case 4:
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void rentalManagementMenu(RentalController rentalController, Scanner scanner) {
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("\n===== RENTAL MANAGEMENT =====");
            System.out.println("1. View All Rentals (With Details)");
            System.out.println("2. Search Rentals");
            System.out.println("3. View Active Rentals");
            System.out.println("4. View Overdue Rentals");
            System.out.println("5. Complete Rental");
            System.out.println("6. Show Rental Statistics");
            System.out.println("7. Back to Main Menu");
            System.out.print("Select option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    rentalController.displayAllRentals();
                    break;
                case 2:
                    rentalController.searchRentals();
                    break;
                case 3:
                    rentalController.showActiveRentals();
                    break;
                case 4:
                    rentalController.showOverdueRentals();
                    break;
                case 5:
                    rentalController.completeRental();
                    break;
                case 6:
                    rentalController.showRentalStatistics();
                    break;
                case 7:
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void login(UserRepository userRepository, AuthService authService, Scanner scanner) {
        System.out.println("\n=== Login ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        boolean success = authService.login(username, password);

        if (success) {
            System.out.println("Login successful! Welcome, " + authService.getCurrentUser().getFullName());
        } else {
            System.out.println("Login failed. Invalid username or password.");
        }
    }

    private static void registerUser(UserRepository userRepository, Scanner scanner) {
        System.out.println("\n=== Register ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Full Name: ");
        String fullName = scanner.nextLine();

        String role = "CUSTOMER";

        try {
            if (username.trim().isEmpty() || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Username and password cannot be empty");
            }
            if (!email.contains("@")) {
                throw new IllegalArgumentException("Invalid email format");
            }

            User user = new User(username, password, email, fullName, role);
            userRepository.createUser(user);

            System.out.println("Registration successful! You can now login.");
        } catch (IllegalArgumentException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private static void logout(AuthService authService) {
        if (authService.isLoggedIn()) {
            String username = authService.getCurrentUser().getUsername();
            authService.logout();
            System.out.println("Logged out successfully. Goodbye, " + username + "!");
        } else {
            System.out.println("No user is currently logged in.");
        }
    }
}