package controllers;

import entities.Car;
import repositories.CarRepository;
import services.AuthService;
import java.util.List;
import java.util.Scanner;

public class CarController {
    private CarRepository carRepository;
    private AuthService authService;
    private Scanner scanner;

    public CarController(CarRepository carRepository, AuthService authService, Scanner scanner) {
        this.carRepository = carRepository;
        this.authService = authService;
        this.scanner = scanner;
    }

    public void displayAvailableCars() {
        List<Car> cars = carRepository.getAvailableCars();

        System.out.println("\n=== Available Cars for Rent ===");
        System.out.printf("%-5s %-15s %-15s %-12s %-6s %-15s %-10s%n",
                "ID", "Brand", "Model", "License", "Year", "Category", "Price/Day");
        System.out.println("-----------------------------------------------------------------------------------");

        for (Car car : cars) {
            System.out.printf("%-5d %-15s %-15s %-12s %-6d %-15s $%-9.2f%n",
                    car.getId(),
                    car.getBrand(),
                    car.getModel(),
                    car.getLicensePlate(),
                    car.getYear(),
                    car.getCategoryName() != null ? car.getCategoryName() : "N/A",
                    car.getDailyPrice());
        }
    }

    public void displayAllCars() {
        if (!authService.canManageCars()) {
            System.out.println("Access denied. Staff role required.");
            return;
        }

        List<Car> cars = carRepository.getAllCarsWithCategory();

        System.out.println("\n=== All Cars (With Categories) ===");
        System.out.printf("%-5s %-15s %-15s %-12s %-6s %-15s %-10s %-10s%n",
                "ID", "Brand", "Model", "License", "Year", "Category", "Price/Day", "Available");
        System.out.println("-------------------------------------------------------------------------------------------");

        for (Car car : cars) {
            System.out.printf("%-5d %-15s %-15s %-12s %-6d %-15s $%-9.2f %-10s%n",
                    car.getId(),
                    car.getBrand(),
                    car.getModel(),
                    car.getLicensePlate(),
                    car.getYear(),
                    car.getCategoryName() != null ? car.getCategoryName() : "N/A",
                    car.getDailyPrice(),
                    car.isAvailable() ? "Yes" : "No");
        }
    }

    public void searchCars() {
        System.out.print("\nSearch cars (brand, model, license, or category): ");
        String searchTerm = scanner.nextLine();

        List<Car> results = carRepository.searchCars(searchTerm);

        if (results.isEmpty()) {
            System.out.println("No cars found matching: '" + searchTerm + "'");
            return;
        }

        System.out.println("\n=== Search Results ===");
        System.out.printf("%-5s %-15s %-15s %-12s %-6s %-15s %-10s%n",
                "ID", "Brand", "Model", "License", "Year", "Category", "Price/Day");
        System.out.println("-----------------------------------------------------------------------------------");

        for (Car car : results) {
            System.out.printf("%-5d %-15s %-15s %-12s %-6d %-15s $%-9.2f%n",
                    car.getId(),
                    car.getBrand(),
                    car.getModel(),
                    car.getLicensePlate(),
                    car.getYear(),
                    car.getCategoryName() != null ? car.getCategoryName() : "N/A",
                    car.getDailyPrice());
        }

        System.out.println("\nFound " + results.size() + " car(s) matching '" + searchTerm + "'");
    }

    public void listCarsByCategory() {
        System.out.println("\n=== Car Categories ===");
        System.out.println("1. Economy");
        System.out.println("2. SUV");
        System.out.println("3. Luxury");
        System.out.println("4. Sports");
        System.out.print("Select category (1-4) or enter category name: ");

        String input = scanner.nextLine();
        String categoryName;

        switch (input) {
            case "1": categoryName = "Economy"; break;
            case "2": categoryName = "SUV"; break;
            case "3": categoryName = "Luxury"; break;
            case "4": categoryName = "Sports"; break;
            default: categoryName = input;
        }

        List<Car> cars = carRepository.getCarsByCategory(categoryName);

        if (cars.isEmpty()) {
            System.out.println("No cars found in category: " + categoryName);
            return;
        }

        System.out.println("\n=== Cars in Category: " + categoryName + " ===");
        System.out.printf("%-5s %-15s %-15s %-12s %-6s %-10s%n",
                "ID", "Brand", "Model", "License", "Year", "Price/Day");
        System.out.println("------------------------------------------------------------");

        for (Car car : cars) {
            System.out.printf("%-5d %-15s %-15s %-12s %-6d $%-9.2f%n",
                    car.getId(),
                    car.getBrand(),
                    car.getModel(),
                    car.getLicensePlate(),
                    car.getYear(),
                    car.getDailyPrice());
        }
    }

    // Показать машины по ценовому диапазону
    public void listCarsByPriceRange() {
        System.out.print("\nEnter minimum daily price: $");
        double minPrice = Double.parseDouble(scanner.nextLine());

        System.out.print("Enter maximum daily price: $");
        double maxPrice = Double.parseDouble(scanner.nextLine());

        List<Car> cars = carRepository.getCarsByPriceRange(minPrice, maxPrice);

        if (cars.isEmpty()) {
            System.out.printf("No cars found in price range: $%.2f - $%.2f\n", minPrice, maxPrice);
            return;
        }

        System.out.printf("\n=== Cars in Price Range: $%.2f - $%.2f ===\n", minPrice, maxPrice);
        System.out.printf("%-5s %-15s %-15s %-12s %-6s %-15s %-10s%n",
                "ID", "Brand", "Model", "License", "Year", "Category", "Price/Day");
        System.out.println("-----------------------------------------------------------------------------------");

        for (Car car : cars) {
            System.out.printf("%-5d %-15s %-15s %-12s %-6d %-15s $%-9.2f%n",
                    car.getId(),
                    car.getBrand(),
                    car.getModel(),
                    car.getLicensePlate(),
                    car.getYear(),
                    car.getCategoryName() != null ? car.getCategoryName() : "N/A",
                    car.getDailyPrice());
        }
    }

    // Показать статистику по машинам
    public void showCarStatistics() {
        if (!authService.canManageCars()) {
            System.out.println("Access denied. Staff role required.");
            return;
        }

        carRepository.printCarStatistics();
    }

    // Обновить статус машины
    public void updateCarStatus() {
        if (!authService.canManageCars()) {
            System.out.println("Access denied. Staff role required.");
            return;
        }

        displayAllCars();

        System.out.print("\nEnter Car ID to update: ");
        int carId = Integer.parseInt(scanner.nextLine());

        System.out.print("Set availability (true/false): ");
        boolean isAvailable = Boolean.parseBoolean(scanner.nextLine());

        carRepository.updateCarAvailability(carId, isAvailable);
        System.out.println("Car status updated successfully!");
    }

    public void showCarDetails() {
        System.out.print("\nEnter Car ID to view details: ");
        int carId = Integer.parseInt(scanner.nextLine());

        Car car = carRepository.getCarById(carId);

        if (car == null) {
            System.out.println("Car not found!");
            return;
        }

        System.out.println("\n=== Car Details ===");
        System.out.println("ID: " + car.getId());
        System.out.println("Brand: " + car.getBrand());
        System.out.println("Model: " + car.getModel());
        System.out.println("License Plate: " + car.getLicensePlate());
        System.out.println("Year: " + car.getYear());
        System.out.println("Category: " + (car.getCategoryName() != null ? car.getCategoryName() : "N/A"));
        System.out.printf("Daily Price: $%.2f\n", car.getDailyPrice());
        System.out.println("Available: " + (car.isAvailable() ? "Yes" : "No"));
    }
}