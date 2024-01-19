package com.example.cabBookingApp.driver;

import com.example.cabBookingApp.exceptionHandler.DriverNotFoundException;
import com.example.cabBookingApp.exceptionHandler.UserNotFoundException;
import com.example.cabBookingApp.service.RideService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class Driver {

    public static void main(String[] args) {
        RideService rideService = new RideService();

        rideService.addUser("Abhishek", "M", 23);
        rideService.addUser("Rahul", "M", 29);
        rideService.addUser("Nandini", "F", 22);

        rideService.addDriver("Driver1", "M", 22, "Swift, KA-01-12345", new int[]{10, 1});
        rideService.addDriver("Driver2", "M", 29, "Swift, KA-01-12345", new int[]{11, 10});
        rideService.addDriver("Driver3", "M", 24, "Swift, KA-01-12345", new int[]{5, 3});

        List<String> ridesForAbhishek = rideService.findRideSync("Abhishek", new int[]{0, 0}, new int[]{20, 1});
        System.out.println("Abhishek's ride options: " + ridesForAbhishek);

        List<String> ridesForRahul = rideService.findRideSync("Rahul", new int[]{10, 0}, new int[]{15, 3});
        System.out.println("Rahul's ride options: " + ridesForRahul);

        List<String> ridesForNandini = rideService.findRideSync("Nandini", new int[]{15, 6}, new int[]{20, 4});
        System.out.println("Nandini's ride options: " + ridesForNandini);

        rideService.chooseRide("Rahul", "Driver1");

        try {
            CompletableFuture<List<String>> rideFuture = rideService.findRide("Rahul", new int[]{10, 0}, new int[]{15, 3});
            rideFuture.thenAcceptAsync(rides -> {
                if (rides.isEmpty()) {
                    System.out.println("No ride found.");
                } else {
                    System.out.println("Available rides: " + String.join(", ", rides));
                }
            }).join();

            rideService.addUser("Abhishek", "M", 23);
            rideService.addUser("Rahul", "M", 29);
            rideService.addUser("Nandini", "F", 22);

            rideService.addDriver("Driver1", "M", 22, "Swift, KA-01-12345", new int[]{10, 1});
            rideService.addDriver("Driver2", "M", 29, "Swift, KA-01-12345", new int[]{11, 10});
            rideService.addDriver("Driver3", "M", 24, "Swift, KA-01-12345", new int[]{5, 3});

            CompletableFuture<Void> chooseRideFuture = rideService.chooseRideSync("Rahul", "Driver1");
            chooseRideFuture.thenRun(() -> System.out.println("Ride chosen successfully")).join(); // Wait for the asynchronous task to complete

            rideService.shutdownExecutorService();
        } catch (DriverNotFoundException | UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
