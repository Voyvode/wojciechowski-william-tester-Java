package com.parkit.parkingsystem;

import java.time.Instant;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.parkit.parkingsystem.constants.ParkingType.BIKE;
import static com.parkit.parkingsystem.constants.ParkingType.CAR;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    public static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    public void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar() {
        var inTime = Instant.now().minus(1, HOURS);
        var outTime = Instant.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), CAR.getRatePerHour());
    }

    @Test
    public void calculateFareBike() {
        var inTime = Instant.now().minus(1, HOURS);
        var outTime = Instant.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), BIKE.getRatePerHour());
    }

    @Test
    public void calculateFareUnknownType() {
        var inTime = Instant.now().minus(1, HOURS);
        var outTime = Instant.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime() {
        var inTime = Instant.now().plus(1, HOURS);
        var outTime = Instant.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
        var inTime = Instant.now().minus(45, MINUTES); // 45 minutes parking time should give 3/4th parking fare
        var outTime = Instant.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * BIKE.getRatePerHour()), ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime() {
        var inTime = Instant.now().minus(45, MINUTES); // 45 minutes parking time should give 3/4th parking fare
        var outTime = Instant.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * CAR.getRatePerHour()), ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime() {
        var inTime = Instant.now().minus(24, HOURS); // 24 hours parking time should give 24 * parking fare per hour
        var outTime = Instant.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((24 * CAR.getRatePerHour()), ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithLessThan30minutesParkingTime() {
        var inTime = Instant.now().minus(29, MINUTES);
        var outTime = Instant.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0, ticket.getPrice());
    }

    @Test
    public void calculateFareBikeWithLessThan30minutesParkingTime() {
        var inTime = Instant.now().minus(29, MINUTES);
        var outTime = Instant.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0, ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithDiscount() {
        var inTime = Instant.now().minus(1, HOURS);
        var outTime = Instant.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, true);
        assertEquals(1 * CAR.getRatePerHour() * 0.95, ticket.getPrice());
    }

    @Test
    public void calculateFareBikeWithDiscount() {
        var inTime = Instant.now().minus(1, HOURS);
        var outTime = Instant.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, true);
        assertEquals(1 * BIKE.getRatePerHour() * 0.95, ticket.getPrice());
    }

}
