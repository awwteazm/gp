package controllers;

import entities.Car;
import entities.Rental;
import entities.User;
import repositories.CarRepository;
import repositories.RentalRepository;
import services.AuthService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class RentalController {
    private RentalRepository rentalRepository;
    private CarRepository carRepository;
    private AuthService authService;
    private Scanner scanner;

    public RentalController(RentalRepository rentalRepository, CarRepository carRepository,
                            AuthService authService, Scanner scanner) {
        this.rentalRepository = rentalRepository;
        this.carRepository = carRepository;
        this.authService = authService;
        this.scanner = scanner;
    }

    public void createNewRental() {
        System.out.println("\n=== Create New Rental ===");

        if (!authService.isLoggedIn()) {
            System.out.println("Please login first.");
            return;
        }

        List<Car> availableCars = carRepository.getAvailableCars();
        if (availableCars.isEmpty()) {
            System.out.println("No cars available for rent.");
            return;
        }

        System.out.println("\nAvailable Cars:");
        System.out.printf("%-5s %-15s %-15s %-10s%n", "ID", "Brand", "Model", "Price/Day");
        System.out.println("------------------------------------------------");

        for (Car car : availableCars) {
            System.out.printf("%-5d %-15s %-15s $%-9.2f%n",
                    car.getId(), car.getBrand(), car.getModel(), car.getDailyPrice());
        }

        System.out.print("\nSelect Car ID: ");
        int carId = Integer.parseInt(scanner.nextLine());

        Car selectedCar = carRepository.getCarById(carId);
        if (selectedCar == null || !selectedCar.isAvailable()) {
            System.out.println("Car not available.");
            return;
        }

        LocalDate startDate = getDate("Start Date (YYYY-MM-DD): ", false);
        LocalDate endDate = getDate("End Date (YYYY-MM-DD): ", true);

        if (endDate == null || !endDate.isAfter(startDate)) {
            System.out.println("End date must be after start date.");
            return;
        }

        if (!rentalRepository.isCarAvailableForDates(carId, startDate, endDate)) {
            System.out.println("Car is not available for the selected dates.");
            return;
        }

        long days = endDate.toEpochDay() - startDate.toEpochDay() + 1;
        double totalPrice = selectedCar.getDailyPrice() * days;

        User currentUser = authService.getCurrentUser();
        String customerName, customerEmail;

        System.out.println("\nUsing your account information:");
        customerName = currentUser.getFullName() != null ? currentUser.getFullName() : currentUser.getUsername();
        customerEmail = currentUser.getEmail();

        System.out.println("Name: " + customerName);
        System.out.println("Email: " + customerEmail);

        System.out.print("Use this information? (yes/no): ");
        if (!scanner.nextLine().equalsIgnoreCase("yes")) {
            System.out.print("Customer Name: ");
            customerName = scanner.nextLine();

            System.out.print("Customer Email: ");
            customerEmail = scanner.nextLine();
        }

        System.out.println("\n=== Rental Summary ===");
        System.out.println("Car: " + selectedCar.getFullName());
        System.out.println("Period: " + startDate + " to " + endDate + " (" + days + " days)");
        System.out.println("Daily Price: $" + selectedCar.getDailyPrice());
        System.out.println("Total Price: $" + totalPrice);
        System.out.println("Customer: " + customerName + " (" + customerEmail + ")");

        System.out.print("\nConfirm rental? (yes/no): ");
        if (!scanner.nextLine().equalsIgnoreCase("yes")) {
            System.out.println("Rental cancelled.");
            return;
        }

        Rental rental = new Rental(carId, currentUser.getId(), customerName, customerEmail,
                startDate, endDate, totalPrice);
        rentalRepository.createRental(rental);

        carRepository.updateCarAvailability(carId, false);

        System.out.println("Rental created successfully! Rental ID: " + rental.getId());
    }

    public void displayAllRentals() {
        if (!authService.canViewAllRentals()) {
            displayUserRentals();
            return;
        }

        List<Rental> rentals = rentalRepository.getAllRentalsWithDetails();

        System.out.println("\n=== All Rentals (With Details) ===");
        System.out.printf("%-5s %-20s %-25s %-20s %-12s %-12s %-10s %-10s%n",
                "ID", "Customer", "Email", "Car", "Start Date", "End Date", "Price", "Status");
        System.out.println("-------------------------------------------------------------------------------------------------------------------");

        for (Rental rental : rentals) {
            String carInfo = rental.getCarBrand() + " " + rental.getCarModel() +
                    " (" + rental.getCarLicensePlate() + ")";

            System.out.printf("%-5d %-20s %-25s %-20s %-12s %-12s $%-9.2f %-10s%n",
                    rental.getId(),
                    rental.getCustomerName(),
                    rental.getCustomerEmail(),
                    carInfo,
                    rental.getStartDate(),
                    rental.getEndDate(),
                    rental.getTotalPrice(),
                    rental.getStatus());
        }
    }

    public void displayUserRentals() {
        if (!authService.isLoggedIn()) {
            System.out.println("Please login to view your rentals.");
            return;
        }

        User currentUser = authService.getCurrentUser();
        List<Rental> rentals = rentalRepository.getRentalsByUser(currentUser.getId());

        System.out.println("\n=== Your Rentals ===");
        System.out.printf("%-5s %-20s %-12s %-12s %-10s %-10s%n",
                "ID", "Car", "Start Date", "End Date", "Price", "Status");
        System.out.println("---------------------------------------------------------------------");

        for (Rental rental : rentals) {
            String carInfo = rental.getCarBrand() + " " + rental.getCarModel();

            System.out.printf("%-5d %-20s %-12s %-12s $%-9.2f %-10s%n",
                    rental.getId(),
                    carInfo,
                    rental.getStartDate(),
                    rental.getEndDate(),
                    rental.getTotalPrice(),
                    rental.getStatus());
        }
    }

    public void searchRentals() {
        if (!authService.canViewAllRentals()) {
            System.out.println("Access denied. Staff role required.");
            return;
        }

        System.out.print("\nSearch rentals (customer name, email, license plate, or username): ");
        String searchTerm = scanner.nextLine();

        List<Rental> results = rentalRepository.searchRentals(searchTerm);

        if (results.isEmpty()) {
            System.out.println("No rentals found matching: '" + searchTerm + "'");
            return;
        }

        System.out.println("\n=== Search Results ===");
        System.out.printf("%-5s %-20s %-25s %-20s %-12s %-12s %-10s%n",
                "ID", "Customer", "Email", "Car", "Start Date", "End Date", "Price");
        System.out.println("-------------------------------------------------------------------------------------------------------");

        for (Rental rental : results) {
            String carInfo = rental.getCarBrand() + " " + rental.getCarModel();

            System.out.printf("%-5d %-20s %-25s %-20s %-12s %-12s $%-9.2f%n",
                    rental.getId(),
                    rental.getCustomerName(),
                    rental.getCustomerEmail(),
                    carInfo,
                    rental.getStartDate(),
                    rental.getEndDate(),
                    rental.getTotalPrice());
        }

        System.out.println("\nFound " + results.size() + " rental(s) matching '" + searchTerm + "'");
    }

    public void showActiveRentals() {
        if (!authService.canViewAllRentals()) {
            System.out.println("Access denied. Staff role required.");
            return;
        }

        List<Rental> activeRentals = rentalRepository.getActiveRentals();

        if (activeRentals.isEmpty()) {
            System.out.println("No active rentals.");
            return;
        }

        System.out.println("\n=== Active Rentals ===");
        System.out.printf("%-5s %-20s %-20s %-12s %-12s %-10s%n",
                "ID", "Customer", "Car", "Start Date", "End Date", "Price");
        System.out.println("-------------------------------------------------------------------------");

        for (Rental rental : activeRentals) {
            String carInfo = rental.getCarBrand() + " " + rental.getCarModel();

            System.out.printf("%-5d %-20s %-20s %-12s %-12s $%-9.2f%n",
                    rental.getId(),
                    rental.getCustomerName(),
                    carInfo,
                    rental.getStartDate(),
                    rental.getEndDate(),
                    rental.getTotalPrice());
        }
    }

    public void showOverdueRentals() {
        if (!authService.canViewAllRentals()) {
            System.out.println("Access denied. Staff role required.");
            return;
        }

        List<Rental> overdueRentals = rentalRepository.getOverdueRentals();

        if (overdueRentals.isEmpty()) {
            System.out.println("No overdue rentals.");
            return;
        }

        System.out.println("\n=== Overdue Rentals ===");
        System.out.printf("%-5s %-20s %-20s %-12s %-12s %-10s %-10s%n",
                "ID", "Customer", "Car", "End Date", "Days Overdue", "Price", "Contact");
        System.out.println("-----------------------------------------------------------------------------------------");

        LocalDate today = LocalDate.now();

        for (Rental rental : overdueRentals) {
            String carInfo = rental.getCarBrand() + " " + rental.getCarModel();
            long daysOverdue = today.toEpochDay() - rental.getEndDate().toEpochDay();

            System.out.printf("%-5d %-20s %-20s %-12s %-12d $%-9.2f %-10s%n",
                    rental.getId(),
                    rental.getCustomerName(),
                    carInfo,
                    rental.getEndDate(),
                    daysOverdue,
                    rental.getTotalPrice(),
                    rental.getCustomerEmail());
        }

        System.out.println("\nTotal overdue rentals: " + overdueRentals.size());
    }

    public void showRentalStatistics() {
        if (!authService.canViewAllRentals()) {
            System.out.println("Access denied. Staff role required.");
            return;
        }

        rentalRepository.printRentalStatistics();
    }

    public void completeRental() {
        if (!authService.canViewAllRentals()) {
            System.out.println("Access denied. Staff role required.");
            return;
        }

        displayAllRentals();

        System.out.print("\nEnter Rental ID to complete: ");
        int rentalId = Integer.parseInt(scanner.nextLine());

        rentalRepository.updateRentalStatus(rentalId, "COMPLETED");

        System.out.println("Rental completed successfully!");
    }

    public void cancelRental() {
        if (!authService.isLoggedIn()) {
            System.out.println("Please login to cancel a rental.");
            return;
        }

        displayUserRentals();

        System.out.print("\nEnter Rental ID to cancel: ");
        int rentalId = Integer.parseInt(scanner.nextLine());

        rentalRepository.updateRentalStatus(rentalId, "CANCELLED");
        System.out.println("Rental cancelled successfully!");
    }

    public void calculateRentalCost() {
        System.out.println("\n=== Calculate Rental Cost ===");

        System.out.print("Car ID: ");
        int carId = Integer.parseInt(scanner.nextLine());

        Car car = carRepository.getCarById(carId);
        if (car == null) {
            System.out.println("Car not found!");
            return;
        }

        System.out.print("Number of days: ");
        int days = Integer.parseInt(scanner.nextLine());

        if (days <= 0) {
            System.out.println("Number of days must be positive.");
            return;
        }

        double totalCost = car.getDailyPrice() * days;
        System.out.println("\n=== Rental Cost Estimate ===");
        System.out.println("Car: " + car.getFullName());
        System.out.println("Daily Rate: $" + car.getDailyPrice());
        System.out.println("Number of Days: " + days);
        System.out.println("Total Cost: $" + totalCost);
        System.out.println("Daily Average: $" + (totalCost / days));
    }

    private LocalDate getDate(String prompt, boolean allowPast) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        while (true) {
            System.out.print(prompt);
            String dateStr = scanner.nextLine();

            try {
                LocalDate date = LocalDate.parse(dateStr, formatter);

                if (!allowPast && date.isBefore(LocalDate.now())) {
                    System.out.println("Date cannot be in the past.");
                    continue;
                }

                return date;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }
}