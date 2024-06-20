package com.parkit.parkingsystem.constants;

public enum ParkingType {

    CAR(1.5),
    BIKE(1.0);

    private final double ratePerHour;

    ParkingType(double ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    public double getRatePerHour() {
        return ratePerHour;
    }

}