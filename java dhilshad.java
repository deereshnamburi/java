import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class Vehicle {
    private String vehicleNumber;
    private String vehicleType;
    private int entryHour;
    private int entryMinute;
    private int exitHour;
    private int exitMinute;

    public Vehicle(String number, String type, int hour, int minute) {
        vehicleNumber = number;
        vehicleType = type;
        entryHour = hour;
        entryMinute = minute;
        exitHour = -1;
        exitMinute = -1;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void displayVehicle() {
        System.out.println("\nVehicle Number : " + vehicleNumber);
        System.out.println("Vehicle Type   : " + vehicleType);
        System.out.printf("Entry Time     : %02d:%02d%n", entryHour, entryMinute);
    }

    public void setExitTime(int hour, int minute) {
        exitHour = hour;
        exitMinute = minute;
    }

    public int calculateDuration() {
        int entryTotalMinutes = entryHour * 60 + entryMinute;
        int exitTotalMinutes = exitHour * 60 + exitMinute;
        return exitTotalMinutes - entryTotalMinutes;
    }

    public double calculateFee() {
        int duration = calculateDuration();
        if (duration < 0) {
            return 0.0;
        }

        int hours = (duration + 59) / 60;
        if (vehicleType.equalsIgnoreCase("Car")) {
            return hours * 30;
        }
        if (vehicleType.equalsIgnoreCase("Bike")) {
            return hours * 15;
        }
        return hours * 50;
    }

    public void displayBill() {
        int duration = calculateDuration();
        if (duration < 0) {
            System.out.println("\nInvalid exit time! Exit time cannot be before entry time.");
            return;
        }

        int hours = duration / 60;
        int minutes = duration % 60;

        System.out.println("\n========== PARKING BILL ==========");
        System.out.println("Vehicle Number : " + vehicleNumber);
        System.out.println("Vehicle Type   : " + vehicleType);
        System.out.printf("Entry Time     : %02d:%02d%n", entryHour, entryMinute);
        System.out.printf("Exit Time      : %02d:%02d%n", exitHour, exitMinute);
        System.out.println("Duration       : " + hours + " hour(s) " + minutes + " minute(s)");
        System.out.printf("Parking Fee    : Rs. %.2f%n", calculateFee());
        System.out.println("==================================");
    }

    public void saveToFile() {
        try (FileWriter file = new FileWriter("parking.txt", true)) {
            file.write(vehicleNumber + ","
                    + vehicleType + ","
                    + entryHour + ","
                    + entryMinute + ","
                    + exitHour + ","
                    + exitMinute + ","
                    + calculateFee() + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Error: Unable to write to file!");
        }
    }
}

class ParkingLot {
    private final ArrayList<Vehicle> vehicles = new ArrayList<>();
    private final Scanner sc;
    private static final int MAX_CAPACITY = 5;

    public ParkingLot(Scanner sc) {
        this.sc = sc;
    }

    public void addVehicle() {
        if (vehicles.size() >= MAX_CAPACITY) {
            System.out.println("\nParking Lot is Full! Maximum capacity is " + MAX_CAPACITY);
            return;
        }

        System.out.print("\nEnter Vehicle Number: ");
        String number = sc.next();

        System.out.print("Enter Vehicle Type (Car/Bike/Truck): ");
        String type = sc.next();
        if (!type.equalsIgnoreCase("Car")
                && !type.equalsIgnoreCase("Bike")
                && !type.equalsIgnoreCase("Truck")) {
            System.out.println("\nInvalid vehicle type! Please choose Car, Bike, or Truck.");
            return;
        }

        if (findVehicle(number) != -1) {
            System.out.println("\nVehicle with this number is already parked!");
            return;
        }

        System.out.print("Enter Entry Hour (0-23): ");
        int hour = readInteger();
        System.out.print("Enter Entry Minute (0-59): ");
        int minute = readInteger();

        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
            System.out.println("\nInvalid time format! Hours must be 0-23 and minutes 0-59.");
            return;
        }

        vehicles.add(new Vehicle(number, type, hour, minute));
        System.out.println("\nVehicle parked successfully! Slot assigned.");
    }

    public int findVehicle(String number) {
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).getVehicleNumber().equalsIgnoreCase(number)) {
                return i;
            }
        }
        return -1;
    }

    public void searchVehicle() {
        System.out.print("\nEnter Vehicle Number to find: ");
        String number = sc.next();
        int index = findVehicle(number);

        if (index == -1) {
            System.out.println("\nVehicle not found in the parking lot!");
        } else {
            System.out.println("\nVehicle found!");
            vehicles.get(index).displayVehicle();
        }
    }

    public void vehicleExit() {
        System.out.print("\nEnter Vehicle Number for Exit: ");
        String number = sc.next();
        int index = findVehicle(number);

        if (index == -1) {
            System.out.println("\nVehicle not found!");
            return;
        }

        System.out.print("Enter Exit Hour (0-23): ");
        int hour = readInteger();
        System.out.print("Enter Exit Minute (0-59): ");
        int minute = readInteger();

        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
            System.out.println("\nInvalid exit time format!");
            return;
        }

        Vehicle vehicle = vehicles.get(index);
        vehicle.setExitTime(hour, minute);
        if (vehicle.calculateDuration() < 0) {
            System.out.println("\nInvalid exit time! Exit time cannot precede entry time.");
            return;
        }

        vehicle.displayBill();
        vehicle.saveToFile();
        vehicles.remove(index);
        System.out.println("\nVehicle exit completed successfully!");
    }

    public void displayAllVehicles() {
        if (vehicles.isEmpty()) {
            System.out.println("\nNo vehicles are currently parked.");
            return;
        }

        System.out.println("\n======= PARKED VEHICLES (" + vehicles.size() + "/" + MAX_CAPACITY + ") =======");
        for (int i = 0; i < vehicles.size(); i++) {
            System.out.println("\nSlot " + (i + 1));
            vehicles.get(i).displayVehicle();
        }
        System.out.println("\n==============================================");
    }

    private int readInteger() {
        while (!sc.hasNextInt()) {
            System.out.print("Invalid input. Please enter a valid number: ");
            sc.next();
        }
        return sc.nextInt();
    }
}

class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ParkingLot parking = new ParkingLot(sc);
        int choice;

        do {
            System.out.println("\n\n========== VEHICLE PARKING SYSTEM ==========");
            System.out.println("1. Park Vehicle");
            System.out.println("2. Find Vehicle");
            System.out.println("3. Display All Vehicles");
            System.out.println("4. Vehicle Exit");
            System.out.println("5. Exit Program");
            System.out.println("============================================");
            System.out.print("Enter your choice: ");

            while (!sc.hasNextInt()) {
                System.out.print("Enter a valid menu choice (1-5): ");
                sc.next();
            }

            choice = sc.nextInt();
            switch (choice) {
                case 1:
                    parking.addVehicle();
                    break;
                case 2:
                    parking.searchVehicle();
                    break;
                case 3:
                    parking.displayAllVehicles();
                    break;
                case 4:
                    parking.vehicleExit();
                    break;
                case 5:
                    System.out.println("\nThank you for using Smart Parking Management System!");
                    break;
                default:
                    System.out.println("\nInvalid choice! Please select between 1 and 5.");
            }
        } while (choice != 5);

        sc.close();
    }
}
