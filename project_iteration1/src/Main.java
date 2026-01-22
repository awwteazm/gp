

import controller.CarController;
import controller.RentalController;
import database.DatabaseConnection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("   CAR MANAGEMENT SYSTEM (CARSHARING)   ");
        System.out.println("=========================================\n");

        DatabaseConnection.getConnection();

        CarController carController = new CarController();
        RentalController rentalController = new RentalController();
        Scanner scanner = new Scanner(System.in);

        boolean running = true;

        while (running) {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("1. Car Management");
            System.out.println("2. Rental Management");
            System.out.println("3. Exit");
            System.out.print("Select option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    carManagementMenu(carController, scanner);
                    break;
                case 2:
                    rentalManagementMenu(rentalController, scanner);
                    break;
                case 3:
                    running = false;
                    System.out.println("Thank you for using Car Management System!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        DatabaseConnection.closeConnection();
        scanner.close();
    }

    private static void carManagementMenu(CarController carController, Scanner scanner) {
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("\n===== CAR MANAGEMENT =====");
            System.out.println("1. Add New Car");
            System.out.println("2. View All Cars");
            System.out.println("3. View Available Cars");
            System.out.println("4. Update Car Status");
            System.out.println("5. Back to Main Menu");
            System.out.print("Select option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    carController.addNewCar();
                    break;
                case 2:
                    carController.displayAllCars();
                    break;
                case 3:
                    carController.displayAvailableCars();
                    break;
                case 4:
                    carController.updateCarStatus();
                    break;
                case 5:
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void rentalManagementMenu(RentalController rentalController, Scanner scanner) {
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("\n===== RENTAL MANAGEMENT =====");
            System.out.println("1. Create New Rental");
            System.out.println("2. View All Rentals");
            System.out.println("3. Complete Rental");
            System.out.println("4. Calculate Rental Cost");
            System.out.println("5. Back to Main Menu");
            System.out.print("Select option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    rentalController.createNewRental();
                    break;
                case 2:
                    rentalController.displayAllRentals();
                    break;
                case 3:
                    rentalController.completeRental();
                    break;
                case 4:
                    rentalController.calculateRentalCost();
                    break;
                case 5:
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}