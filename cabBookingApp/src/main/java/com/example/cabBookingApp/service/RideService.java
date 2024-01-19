package com.example.cabBookingApp.service;

import com.example.cabBookingApp.exceptionHandler.DriverNotFoundException;
import com.example.cabBookingApp.exceptionHandler.UserNotFoundException;
import com.example.cabBookingApp.model.DriverDetails;
import com.example.cabBookingApp.model.UserDetails;
import com.example.cabBookingApp.repository.DriverRepository;
import com.example.cabBookingApp.repository.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class RideService {

    private final UserRepository userRepository = new UserRepository();
    private final DriverRepository driverRepository = new DriverRepository();

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public void addUser(String username, String gender, int age) {
        userRepository.addUser(new UserDetails(username, gender, age));
    }

    public void addDriver(String driverName, String gender, int age, String vehicleDetails, int[] currentLocation) {
        driverRepository.addDriver(new DriverDetails(driverName, gender, age, vehicleDetails, currentLocation));
    }

//    public List<String> findRideSync(String username, int[] source, int[] destination) throws UserNotFoundException {
//        UserDetails user = userRepository.getUserByUsername(username);
//        List<String> availableRides = new ArrayList<>();
//        List<DriverDetails> drivers= driverRepository.getAvailableDrivers();
//        for (DriverDetails driver : drivers) {
//            if (calculateDistance(source,driver.getCurrentLocation())) {
//                availableRides.add(driver.getDriverName());
//            }
//        }
//
//        return availableRides;
//    }

    public List<String> findRideSync(String username, int[] source, int[] destination) throws UserNotFoundException {
        UserDetails user = userRepository.getUserByUsername(username);
        List<String> availableRides = new ArrayList<>();
        List<DriverDetails> drivers = driverRepository.getAvailableDrivers();
        int maxDistance = 5; // Set the maximum distance to 5 units

        for (DriverDetails driver : drivers) {
            if (calculateDistance(source, driver.getCurrentLocation()) <= maxDistance) {
                availableRides.add(driver.getDriverName());
            }
        }

        return availableRides;
    }

    @Async
    public CompletableFuture<List<String>> findRide(String username, int[] source, int[] destination) {
        return CompletableFuture.completedFuture(findRidesConcurrently(username, source, destination));
    }

    public void chooseRide(String username, String driverName) throws UserNotFoundException, DriverNotFoundException {
        UserDetails user = userRepository.getUserByUsername(username);
        DriverDetails chosenDriver = driverRepository.getDriverByName(driverName);

        if (chosenDriver == null) {
            throw new DriverNotFoundException("Driver with name " + driverName + " not found.");
        }

        else if(driverName.equals(chosenDriver.getDriverName())){
            // Perform other ride-related operations as needed

            // Mark the chosen driver as not available
            chosenDriver.setAvailable(false);
        } else {
            throw new DriverNotFoundException("Driver with name " + driverName + " is not available.");
        }
    }
    @Async
    public CompletableFuture<Void> chooseRideSync(String username, String driverName) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        try {
            UserDetails user = userRepository.getUserByUsername(username);
            DriverDetails chosenDriver = driverRepository.getDriverByName(driverName);

            if (chosenDriver == null) {
                future.completeExceptionally(new DriverNotFoundException("Driver with name " + driverName + " not found."));
                return future;
            }

            else if(driverName.equals(chosenDriver.getDriverName())){

                // Marking the chosen driver as not available
                chosenDriver.setAvailable(false);
                future.complete(null);
            } else {
                future.completeExceptionally(new DriverNotFoundException("Driver with name " + driverName + " is not available."));
            }
        } catch (UserNotFoundException e) {
            future.completeExceptionally(e);
        }

        return future;
    }

    public List<String> findRidesConcurrently(String username, int[] source, int[] destination) {
        List<String> availableRides = new ArrayList<>();
        List<DriverDetails> drivers = driverRepository.getAvailableDrivers();
        for (DriverDetails driver : drivers) {
            if (calculateDistance(source, driver.getCurrentLocation()) <= 5) {
                availableRides.add(driver.getDriverName());
            }
        }
        return availableRides;
    }

    public void chooseRideConcurrently(String username, String driverName) {
        DriverDetails selectedDriver = driverRepository.getDriverByName(driverName);

        if (!selectedDriver.isAvailable()) {
            throw new DriverNotFoundException(driverName + " is not available.");
        }

        System.out.println(username + " chose " + driverName + " for the ride.");
        selectedDriver.setAvailable(false);
    }

    public int calculateDistance(int[] point1, int[] point2) {
        int deltaX = point1[0] - point2[0];
        int deltaY = point1[1] - point2[1];

        return (int) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    public void shutdownExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.err.println("ExecutorService did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
