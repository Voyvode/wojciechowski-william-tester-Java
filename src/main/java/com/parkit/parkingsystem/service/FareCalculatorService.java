package com.parkit.parkingsystem.service;

import java.time.Duration;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        var inHour = ticket.getInTime();
        var outHour = ticket.getOutTime();

        double duration = Duration.between(inHour, outHour).toMinutes() / 60d; // to decimal hours

        if (duration < 0.5) { // less than half an hour
            ticket.setPrice(0);
        } else switch (ticket.getParkingSpot().getParkingType()) {
            case CAR -> ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
            case BIKE -> ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
        }
    }

}
