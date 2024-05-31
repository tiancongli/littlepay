package com.littlepay;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class TapProcessorTest {
    @Test
    public void testImportTaps() {
        try (MockedStatic<CSVTool> mockedCSVTool = Mockito.mockStatic(CSVTool.class)) {
            Tap tap = new Tap(1, LocalDateTime.now(), Tap.Type.ON, "Stop1", "Company1", "Bus37", "5500005555555559");
            List<Tap> taps = List.of(tap);

            mockedCSVTool.when(() -> CSVTool.importFrom(anyString(), any())).thenReturn(taps);

            List<Tap> importedTaps = TapProcessor.importTapsFrom("src/test/resources/taps_test.csv");
            assertEquals(1, importedTaps.size());
            assertEquals(tap, importedTaps.get(0));
        }
    }

    @Test
    public void testExportTrips() {
        try (MockedStatic<CSVTool> mockedCSVTool = Mockito.mockStatic(CSVTool.class)) {
            List<Trip> trips = List.of(
                    new Trip(LocalDateTime.now(), LocalDateTime.now(), 600, "Stop1", "Stop2", 3.25, "Company1", "Bus37", "5500005555555559", Trip.Status.COMPLETED)
            );

            TapProcessor.exportTripsTo("src/test/resources/trips_test.csv", trips);
            String header = "Started,Finished,DurationSecs,FromStopId,ToStopId,ChargeAmount,CompanyId,BusID,PAN,Status";
            mockedCSVTool.verify(() -> CSVTool.exportTo(anyString(), anyList(), any(), eq(header)));
        }
    }

    @Test
    public void testGenerateTripsFrom_CompleteTrip() {
        List<Tap> taps = Arrays.asList(
                new Tap(1, LocalDateTime.of(2023, 1, 22, 13, 0), Tap.Type.ON, "Stop1", "Company1", "Bus37", "5500005555555559"),
                new Tap(2, LocalDateTime.of(2023, 1, 22, 13, 5), Tap.Type.OFF, "Stop2", "Company1", "Bus37", "5500005555555559")
        );
        List<Trip> trips = TapProcessor.generateTripsFrom(taps);

        assertEquals(1, trips.size());
        Trip trip = trips.get(0);
        assertEquals(Trip.Status.COMPLETED, trip.getStatus());
    }

    @Test
    public void testGenerateTripsFrom_CancelledTrip() {
        List<Tap> taps = Arrays.asList(
                new Tap(1, LocalDateTime.of(2023, 1, 23, 8, 0), Tap.Type.ON, "Stop1", "Company1", "Bus37", "4111111111111111"),
                new Tap(2, LocalDateTime.of(2023, 1, 23, 8, 2), Tap.Type.OFF, "Stop1", "Company1", "Bus37", "4111111111111111")
        );
        List<Trip> trips = TapProcessor.generateTripsFrom(taps);

        assertEquals(1, trips.size());
        Trip trip = trips.get(0);
        assertEquals(Trip.Status.CANCELLED, trip.getStatus());
    }

    @Test
    public void testGenerateTripsFrom_IncompleteTrip() {
        List<Tap> taps = List.of(
                new Tap(1, LocalDateTime.of(2023, 1, 22, 9, 20), Tap.Type.ON, "Stop3", "Company1", "Bus36", "4111111111111111")
        );
        List<Trip> trips = TapProcessor.generateTripsFrom(taps);

        assertEquals(1, trips.size());
        Trip trip = trips.get(0);
        assertEquals(Trip.Status.INCOMPLETE, trip.getStatus());
    }

    @Test
    public void testGenerateTripsFrom_TripNotFound_TapOffWithoutTapOn() {
        List<Tap> taps = List.of(
                new Tap(1, LocalDateTime.of(2023, 1, 24, 16, 30), Tap.Type.OFF, "Stop2", "Company1", "Bus37", "5500005555555559")
        );
        List<Trip> trips = TapProcessor.generateTripsFrom(taps);

        assertEquals(0, trips.size());
    }

    @Test
    public void testGenerateTripsFrom_DuplicateTap() {
        List<Tap> taps = Arrays.asList(
                new Tap(1, LocalDateTime.of(2023, 1, 23, 8, 0), Tap.Type.ON, "Stop1", "Company1", "Bus37", "4111111111111111"),
                new Tap(2, LocalDateTime.of(2023, 1, 23, 8, 2), Tap.Type.ON, "Stop1", "Company1", "Bus37", "4111111111111111")
        );
        List<Trip> trips = TapProcessor.generateTripsFrom(taps);

        assertEquals(1, trips.size());
        Trip trip = trips.get(0);
        assertEquals(Trip.Status.INCOMPLETE, trip.getStatus());
    }

    @Test
    public void testGenerateTripsFrom_MultipleTripsForSameCard() {
        List<Tap> taps = Arrays.asList(
                new Tap(1, LocalDateTime.of(2023, 1, 22, 13, 0), Tap.Type.ON, "Stop1", "Company1", "Bus37", "5500005555555559"),
                new Tap(2, LocalDateTime.of(2023, 1, 22, 13, 5), Tap.Type.OFF, "Stop2", "Company1", "Bus37", "5500005555555559"),
                new Tap(3, LocalDateTime.of(2023, 1, 22, 14, 0), Tap.Type.ON, "Stop2", "Company1", "Bus37", "5500005555555559"),
                new Tap(4, LocalDateTime.of(2023, 1, 22, 14, 10), Tap.Type.OFF, "Stop3", "Company1", "Bus37", "5500005555555559")
        );

        List<Trip> trips = TapProcessor.generateTripsFrom(taps);

        assertEquals(2, trips.size());

        Trip trip1 = trips.get(0);
        assertEquals(Trip.Status.COMPLETED, trip1.getStatus());
        assertEquals(3.25, trip1.getChargeAmount(), 0.01);

        Trip trip2 = trips.get(1);
        assertEquals(Trip.Status.COMPLETED, trip2.getStatus());
        assertEquals(5.50, trip2.getChargeAmount(), 0.01);
    }

    @Test
    public void testGenerateTripsFrom_MultipleCards() {
        List<Tap> taps = Arrays.asList(
                // Passenger 1: Tap on Bus37
                new Tap(1, LocalDateTime.of(2023, 1, 22, 13, 0), Tap.Type.ON, "Stop1", "Company1", "Bus37", "5500005555555559"),

                // Passenger 2: Tap on bus36
                new Tap(2, LocalDateTime.of(2023, 1, 22, 13, 2), Tap.Type.ON, "Stop3", "Company1", "Bus36", "4111111111111111"),

                // Passenger 3: Tap on bus37
                new Tap(3, LocalDateTime.of(2023, 1, 22, 13, 3), Tap.Type.ON, "Stop1", "Company1", "Bus37", "4111111111111112"),

                // Passenger 2: Tap off, completes the trip
                new Tap(4, LocalDateTime.of(2023, 1, 22, 13, 4), Tap.Type.OFF, "Stop2", "Company1", "Bus36", "4111111111111111"),

                // Passenger 1: Tap on bus37 again, duplicate tap
                new Tap(5, LocalDateTime.of(2023, 1, 22, 13, 5), Tap.Type.ON, "Stop2", "Company1", "Bus37", "5500005555555559"),

                // Passenger 1: Tap on bus36
                new Tap(6, LocalDateTime.of(2023, 1, 22, 14, 0), Tap.Type.ON, "Stop2", "Company1", "Bus36", "5500005555555559"),

                // Passenger 3: Tap off bus37, completes trip
                new Tap(7, LocalDateTime.of(2023, 1, 22, 14, 1), Tap.Type.OFF, "Stop2", "Company2", "Bus37", "4111111111111112"),

                // Passenger 2: Tap on bus38
                new Tap(8, LocalDateTime.of(2023, 1, 22, 14, 16), Tap.Type.ON, "Stop3", "Company2", "Bus38", "4111111111111111"),

                // Passenger 2: Tap off bus38 at the same stop, cancels trip
                new Tap(9, LocalDateTime.of(2023, 1, 22, 14, 17), Tap.Type.OFF, "Stop3", "Company2", "Bus38", "4111111111111111"),

                // Passenger 1: Tap off bus36, completes trip
                new Tap(10, LocalDateTime.of(2023, 1, 22, 14, 30), Tap.Type.OFF, "Stop1", "Company3", "Bus36", "5500005555555559")
        );

        List<Trip> trips = TapProcessor.generateTripsFrom(taps);

        assertEquals(5, trips.size());

        // Passenger 1: Incomplete trip
        Trip incompleteTrip1 = trips.stream().filter(trip -> trip.getPan().equals("5500005555555559") && trip.getFromStopId().equals("Stop1") && trip.getToStopId() == null).findFirst().orElse(null);
        assertNotNull(incompleteTrip1);
        assertEquals(Trip.Status.INCOMPLETE, incompleteTrip1.getStatus());

        // Passenger 1: Completed trip
        Trip completedTrip1 = trips.stream().filter(trip -> trip.getPan().equals("5500005555555559") && trip.getFromStopId().equals("Stop2") && trip.getToStopId().equals("Stop1")).findFirst().orElse(null);
        assertNotNull(completedTrip1);
        assertEquals(Trip.Status.COMPLETED, completedTrip1.getStatus());

        // Passenger 2: Completed trip
        Trip completedTrip2 = trips.stream().filter(trip -> trip.getPan().equals("4111111111111111") && trip.getFromStopId().equals("Stop3") && trip.getToStopId().equals("Stop2")).findFirst().orElse(null);
        assertNotNull(completedTrip2);
        assertEquals(Trip.Status.COMPLETED, completedTrip2.getStatus());

        // Passenger 2: Cancelled trip
        Trip cancelledTrip2 = trips.stream().filter(trip -> trip.getPan().equals("4111111111111111") && trip.getFromStopId().equals("Stop3") && trip.getToStopId().equals("Stop3")).findFirst().orElse(null);
        assertNotNull(cancelledTrip2);
        assertEquals(Trip.Status.CANCELLED, cancelledTrip2.getStatus());

        // Passenger 3: Completed trip
        Trip completedTrip3 = trips.stream().filter(trip -> trip.getPan().equals("4111111111111112") && trip.getFromStopId().equals("Stop1") && trip.getToStopId().equals("Stop2")).findFirst().orElse(null);
        assertNotNull(completedTrip3);
        assertEquals(Trip.Status.COMPLETED, completedTrip3.getStatus());
    }
}
