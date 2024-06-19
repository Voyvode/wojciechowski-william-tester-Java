package com.parkit.parkingsystem;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class ParkingServiceTest {
    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    private static ParkingService parkingService;

    private static Ticket ticket;

    @BeforeEach
    public void setUpPerTest() {
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    }

    /**
     * Test de l’appel de la méthode processIncomingVehicle() où tout se déroule comme attendu.
     */
    @Test
    public void testProcessIncomingVehicle() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("OUTATIME");
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

        parkingService.processIncomingVehicle();

        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
    }

    /**
     * Test de l’appel de la méthode getNextParkingNumberIfAvailable()
     * avec pour résultat l’obtention d’un spot dont l’ID est 1 et qui est disponible.
     */
    @Test
    public void testGetNextParkingNumberIfAvailable() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

        var spotNumber = parkingService.getNextParkingNumberIfAvailable();

        assertEquals(1, spotNumber.getId());
    }

    /**
     * Test de l’appel de la méthode getNextParkingNumberIfAvailable()
     * avec pour résultat aucun spot disponible (la méthode renvoie null).
     */
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);

        var parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        assertNull(parkingSpot);
    }

    /**
     *  Test de getNextParkingNumberIfAvailable() avec pour résultat aucun spot (null)
     *  à cause d’une erreur utilisateur sur le type de véhicule (il a saisi 3, par exemple).
     */
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
        when(inputReaderUtil.readSelection()).thenReturn(3);

        var parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        assertNull(parkingSpot);
    }

    /**
     * Test de l’appel de la méthode processIncomingVehicle() où tout se déroule comme attendu
     * avec un véhicule déjà venu (getNbTicket() > 0).
     */
    @Test
    public void testProcessExitingVehicle() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("OUTATIME");
        ticket.setInTime(Instant.now().minus(1, ChronoUnit.HOURS));

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("OUTATIME");
        when(ticketDAO.getTicket("OUTATIME")).thenReturn(ticket);
        when(ticketDAO.updateTicket(ticket)).thenReturn(true);
        when(ticketDAO.getNbTicket("OUTATIME")).thenReturn(1);

        parkingService.processExitingVehicle();

        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
    }

    /**
     * Test dans le cas où la méthode updateTicket() de ticketDAO renvoie false
     * lors de l’appel de processExitingVehicle().
     */
    @Test
    public void testProcessExitingVehicleIfUnableToUpdate() {
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outputStreamCaptor));

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("OUTATIME");
        ticket.setInTime(Instant.now().minus(1, ChronoUnit.HOURS));

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("OUTATIME");
        when(ticketDAO.getTicket("OUTATIME")).thenReturn(ticket);
        when(ticketDAO.updateTicket(ticket)).thenReturn(false);

        parkingService.processExitingVehicle();

        assertEquals("Unable to update ticket information. Error occurred", outputStreamCaptor.toString().trim());
    }

}
