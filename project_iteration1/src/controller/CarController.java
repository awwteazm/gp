package  controller;

import entity.Car;
import repository.CarRepository;
import java.util.List;
import java.util.Scanner;

public class CarController {
    private CarRepository carRepository;
    private Scanner scanner;

    public CarController() {
        this.carRepository = new CarRepository();
        this.scanner = new Scanner(System.in);
    }

    public void addNewCar() {
        System.out.println("\n=== Add New Car ===");
        System.out.print("Brand: ");
        String brand = scanner.nextLine();

        System.out.print("Model: ");
        String model = scanner.nextLine();

        System.out.print("License Plate: ");
        String licensePlate = scanner.nextLine();

        System.out.print("Year: ");
        int year = Integer.parseInt(scanner.nextLine());

        System.out.print("Daily Price: ");
        double dailyPrice = Double.parseDouble(scanner.nextLine());

        Car car = new Car(brand, model, licensePlate, year, dailyPrice);
        carRepository.createCar(car);

        System.out.println("Car added successfully! ID: " + car.getId());
    }

    public void displayAllCars() {
        List<Car> cars = carRepository.getAllCars();
        System.out.println("\n=== All Cars ===");
        System.out.printf("%-5s %-15s %-15s %-12s %-6s %-10s %-10s%n",
                "ID", "Brand", "Model", "License", "Year", "Price/Day", "Available");
        System.out.println("--------------------------------------------------------------------");

        for (Car car : cars) {
            System.out.printf("%-5d %-15s %-15s %-12s %-6d $%-9.2f %-10s%n",
                    car.getId(),
                    car.getBrand(),
                    car.getModel(),
                    car.getLicensePlate(),
                    car.getYear(),
                    car.getDailyPrice(),
                    car.isAvailable() ? "Yes" : "No");
        }
    }

    public void displayAvailableCars() {
        List<Car> cars = carRepository.getAvailableCars();
        System.out.println("\n=== Available Cars for Rent ===");
        System.out.printf("%-5s %-15s %-15s %-12s %-6s %-10s%n",
                "ID", "Brand", "Model", "License", "Year", "Price/Day");
        System.out.println("------------------------------------------------------");

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

    public void updateCarStatus() {
        System.out.println("\n=== Update Car Status ===");
        displayAllCars();

        System.out.print("\nEnter Car ID to update: ");
        int carId = Integer.parseInt(scanner.nextLine());

        System.out.print("Set availability (true/false): ");
        boolean isAvailable = Boolean.parseBoolean(scanner.nextLine());

        carRepository.updateCarAvailability(carId, isAvailable);
        System.out.println("Car status updated successfully!");
    }
}