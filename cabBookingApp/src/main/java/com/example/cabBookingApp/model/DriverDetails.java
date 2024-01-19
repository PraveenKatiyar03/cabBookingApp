package com.example.cabBookingApp.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverDetails {

    private String driverName;
    private String gender;
    private int age;
    private String vehicleDetails;
    private boolean isAvailable=true;
    private int[] currentLocation;

    public DriverDetails(String driverName, String gender, int age, String vehicleDetails, int[] currentLocation) {
        this.driverName=driverName;
        this.gender=gender;
        this.age=age;
        this.vehicleDetails=vehicleDetails;
        this.currentLocation=currentLocation;
    }
}
