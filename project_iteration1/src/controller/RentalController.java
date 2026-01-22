package controller;

import entity.Car;
import entity.Rental;
import repository.CarRepository;
import repository.RentalRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class RentalController {
    private RentalRepository rentalRepository;
    private CarRepository carRepository;
    private Scanner scanner;
    private DateTimeFormatter dateFormatter;

    public RentalController() {
        this.rentalRepository = new RentalRepository();
        this.carRepository = new CarRepository();
        this.scanner = new Scanner(System.in);
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public void createNewRental() {
        System.out.println("\n=== Create New Rental ===");

        List<Car> availableCars = carRepository.getAvailableCars();
        if (availableCars.isEmpty()) {
            System.out.println("No cars available for rent at the moment.");
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

        LocalDate startDate = null;
        LocalDate endDate = null;

        while (startDate == null) {
            System.out.print("Start Date (YYYY-MM-DD): ");
            try {
                startDate = LocalDate.parse(scanner.nextLine(), dateFormatter);
                if (startDate.isBefore(LocalDate.now())) {
                    System.out.println("Start date cannot be in the past.");
                    startDate = null;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }

        while (endDate == null) {
            System.out.print("End Date (YYYY-MM-DD): ");
            try {
                endDate = LocalDate.parse(scanner.nextLine(), dateFormatter);
                if (endDate.isBefore(startDate)) {
                    System.out.println("End date must be after start date.");
                    endDate = null;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }

        if (!rentalRepository.isCarAvailableForDates(carId, startDate, endDate)) {
            System.out.println("This car is not available for the selected dates.");
            return;
        }

        System.out.print("Customer Name: ");
        String customerName = scanner.nextLine();

        System.out.print("Customer Email: ");
        String customerEmail = scanner.nextLine();

        double totalPrice = rentalRepository.calculateRentalPrice(carId, startDate, endDate);

        System.out.printf("\nRental Summary:\n");
        System.out.printf("Car ID: %d\n", carId);
        System.out.printf("Period: %s to %s\n", startDate, endDate);
        System.out.printf("Total Price: $%.2f\n", totalPrice);

        System.out.print("\nConfirm rental? (yes/no): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("yes")) {
            Rental rental = new Rental(carId, customerName, customerEmail, startDate, endDate, totalPrice);
            rentalRepository.createRental(rental);
            carRepository.updateCarAvailability(carId, false);
            System.out.println("Rental created successfully! Rental ID: " + rental.getId());
        } else {
            System.out.println("Rental cancelled.");
        }
    }
    public void displayAllRentals() {
        List<Rental> rentals = rentalRepository.getAllRentals();
        System.out.println("\n=== All Rentals ===");
        System.out.printf("%-5s %-20s %-25s %-12s %-12s %-10s %-10s%n",
                "ID", "Customer", "Email", "Start Date", "End Date", "Price", "Status");
        System.out.println("---------------------------------------------------------------------------------------------");

        for (Rental rental : rentals) {
            System.out.printf("%-5d %-20s %-25s %-12s %-12s $%-9.2f %-10s%n",
                    rental.getId(),
                    rental.getCustomerName(),
                    rental.getCustomerEmail(),
                    rental.getStartDate(),
                    rental.getEndDate(),
                    rental.getTotalPrice(),
                    rental.getStatus());
        }
    }

    public void completeRental() {
        System.out.println("\n=== Complete Rental ===");
        displayActiveRentals();

        System.out.print("\nEnter Rental ID to complete: ");
        int rentalId = Integer.parseInt(scanner.nextLine());
        rentalRepository.updateRentalStatus(rentalId, "COMPLETED");
        List<Rental> rentals = rentalRepository.getAllRentals();
        for (Rental rental : rentals) {
            if (rental.getId() == rentalId) {
                // Make car available again
                carRepository.updateCarAvailability(rental.getCarId(), true);
                break;
            }
        }

        System.out.println("Rental completed successfully! Car is now available.");
    }

    private void displayActiveRentals() {
        List<Rental> rentals = rentalRepository.getAllRentals();
        System.out.println("\n=== Active Rentals ===");
        System.out.printf("%-5s %-20s %-15s %-12s %-12s %-10s%n",
                "ID", "Customer", "Car ID", "Start Date", "End Date", "Price");
        System.out.println("------------------------------------------------------------------");

        for (Rental rental : rentals) {
            if (rental.getStatus().equals("ACTIVE")) {
                System.out.printf("%-5d %-20s %-15d %-12s %-12s $%-9.2f%n",
                        rental.getId(),
                        rental.getCustomerName(),
                        rental.getCarId(),
                        rental.getStartDate(),
                        rental.getEndDate(),
                        rental.getTotalPrice());
            }
        }
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
        System.out.printf("\nRental Cost Estimate:\n");
        System.out.printf("Car: %s %s\n", car.getBrand(), car.getModel());
        System.out.printf("Daily Rate: $%.2f\n", car.getDailyPrice());
        System.out.printf("Number of Days: %d\n", days);
        System.out.printf("Total Cost: $%.2f\n", totalCost);
    }
}