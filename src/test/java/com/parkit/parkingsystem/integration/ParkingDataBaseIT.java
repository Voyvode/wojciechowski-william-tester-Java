package com.parkit.parkingsystem.integration;

import java.time.Duration;
import java.time.Instant;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.parkit.parkingsystem.constants.ParkingType.CAR;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    public static void setUp() {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    public void setUpPerTest() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    public void testParkingACar() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        assertNotNull(ticketDAO.getTicket("ABCDEF")); // check that a ticket is in database
        assertNotEquals(1, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)); // check first spot is taken
    }

    @Test
    public void testParkingLotExit() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        var oneHourLater = Instant.now().plus(1, HOURS); // exit

        try (var mockedInstant = mockStatic(Instant.class, CALLS_REAL_METHODS)) {
            mockedInstant.when(Instant::now).thenReturn(oneHourLater);
            parkingService.processExitingVehicle();
        }

        var ticket = ticketDAO.getTicket("ABCDEF");
        assertEquals(1 * CAR.getRatePerHour(), ticket.getPrice()); // check one hour car fare in database
        assertEquals(Duration.of(1, HOURS), Duration.between(ticket.getInTime(), ticket.getOutTime())); // check one hour later out time in database
    }

    @Test
    public void testParkingLotExitRecurringUser() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        var oneHourLater = Instant.now().plus(1, HOURS); // exit
        var tomorrow = Instant.now().plus(1, DAYS); // back again
        var tomorrowOneHourLater = Instant.now().plus(1, DAYS).plus(1, HOURS); // exit again

        try (var mockedInstant = mockStatic(Instant.class, CALLS_REAL_METHODS)) {
            mockedInstant.when(Instant::now).thenReturn(oneHourLater);
            parkingService.processExitingVehicle();

            mockedInstant.when(Instant::now).thenReturn(tomorrow);
            parkingService.processIncomingVehicle();

            mockedInstant.when(Instant::now).thenReturn(tomorrowOneHourLater);
            parkingService.processExitingVehicle();
        }

        var ticket = ticketDAO.getTicket("ABCDEF");
        assertEquals(1 * CAR.getRatePerHour() * 0.95, ticket.getPrice()); // check discounted one hour car fare in database
    }

}
