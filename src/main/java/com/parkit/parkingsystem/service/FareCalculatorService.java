package com.parkit.parkingsystem.service;

import java.time.Duration;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
        calculateFare(ticket, false);
    }

    public void calculateFare(Ticket ticket, boolean discount) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        var inHour = ticket.getInTime();
        var outHour = ticket.getOutTime();

        double duration = Duration.between(inHour, outHour).toMinutes() / 60.0; // to decimal hours

        if (duration < 0.5) { // if less than half an hour
            ticket.setPrice(0);
        } else {
            double rate = switch (ticket.getParkingSpot().getParkingType()) {
                case CAR -> Fare.CAR_RATE_PER_HOUR;
                case BIKE -> Fare.BIKE_RATE_PER_HOUR;
            };
            if (discount) {
                System.out.println("5% loyalty discount!");
                rate *= 0.95;
            }
            ticket.setPrice(duration * rate);
        }
    }

}
