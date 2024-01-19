package com.example.cabBookingApp.repository;

import com.example.cabBookingApp.exceptionHandler.DriverNotFoundException;
import com.example.cabBookingApp.model.DriverDetails;
import com.example.cabBookingApp.model.UserDetails;

import java.util.ArrayList;
import java.util.List;

public class DriverRepository {

    private final List<DriverDetails> drivers = new ArrayList<>();

    public void addDriver(DriverDetails driver) {
        drivers.add(driver);
    }

    public List<DriverDetails> getAvailableDrivers() {
        return drivers.stream()
                .filter(DriverDetails::isAvailable)
                .toList();
    }

    public DriverDetails getDriverByName(String driverName) {
        return drivers.stream()
                .filter(driver -> driver.getDriverName().equals(driver.getDriverName()))
                .findFirst()
                .orElseThrow(() -> new DriverNotFoundException("so driver with this name is not found"));
    }
}
