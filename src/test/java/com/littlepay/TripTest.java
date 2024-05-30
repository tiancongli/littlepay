package com.littlepay;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TripTest {
    private Trip trip;

    @Before
    public void setUp() {
        Tap onTap = new Tap(1, LocalDateTime.of(2023, 1, 22, 13, 0), "ON", "Stop1", "Company1", "Bus37", "5500005555555559");
        trip = new Trip(onTap);
    }

    @Test
    public void testTripConstructorFromTap() {
        assertEquals(LocalDateTime.of(2023, 1, 22, 13, 0), trip.getStarted());
        assertEquals("Stop1", trip.getFromStopId());
        assertEquals("Company1", trip.getCompanyId());
        assertEquals("Bus37", trip.getBusId());
        assertEquals("5500005555555559", trip.getPan());
    }

    @Test
    public void testCancelTrip() {
        Tap offTap = new Tap(3, LocalDateTime.of(2023, 1, 22, 13, 10), "OFF", "Stop1", "Company1", "Bus37", "5500005555555559");
        trip.complete(offTap);

        assertEquals(LocalDateTime.of(2023, 1, 22, 13, 10), trip.getFinished());
        assertEquals("Stop1", trip.getToStopId());
        assertEquals(Trip.Status.CANCELLED, trip.getStatus());
    }

    @Test
    public void testCompleteTrip() {
        Tap offTap = new Tap(3, LocalDateTime.of(2023, 1, 22, 13, 10), "OFF", "Stop2", "Company1", "Bus37", "5500005555555559");
        trip.complete(offTap);

        assertEquals(LocalDateTime.of(2023, 1, 22, 13, 10), trip.getFinished());
        assertEquals("Stop2", trip.getToStopId());
        assertEquals(Trip.Status.COMPLETED, trip.getStatus());
    }

    @Test
    public void testIncompleteTrip() {
        trip.incomplete();
        assertNull(trip.getFinished());
        assertNull(trip.getToStopId());
        assertEquals(Trip.Status.INCOMPLETE, trip.getStatus());
    }
}
