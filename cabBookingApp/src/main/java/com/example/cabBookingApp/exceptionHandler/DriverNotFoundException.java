package com.example.cabBookingApp.exceptionHandler;

public class DriverNotFoundException extends RuntimeException{

    public DriverNotFoundException(String driverName) {
        super("Driver with name " + driverName + " not found.");
    }
}
