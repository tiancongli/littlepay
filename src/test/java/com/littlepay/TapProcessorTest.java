package com.littlepay;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
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
            Trip trip = new Trip(LocalDateTime.now(), LocalDateTime.now(), 600, "Stop1", "Stop2", 3.25, "Company1", "Bus37", "5500005555555559", Trip.Status.COMPLETED);
            List<Trip> trips = List.of(trip);

            TapProcessor.exportTripsTo("src/test/resources/trips_test.csv", trips);
            String header = "Started,Finished,DurationSecs,FromStopId,ToStopId,ChargeAmount,CompanyId,BusID,PAN,Status";
            mockedCSVTool.verify(() -> CSVTool.exportTo(anyString(), anyList(), any(), eq(header)));
        }
    }

    @Test
    public void testGenerateTripsFrom_CompleteTrip() {
        Tap onTap = new Tap(1, LocalDateTime.of(2023, 1, 22, 13, 0), Tap.Type.ON, "Stop1", "Company1", "Bus37", "5500005555555559");
        Tap offTap = new Tap(3, LocalDateTime.of(2023, 1, 22, 13, 10), Tap.Type.OFF, "Stop2", "Company1", "Bus37", "5500005555555559");
        List<Tap> taps = Arrays.asList(onTap, offTap);
        List<Trip> trips = TapProcessor.generateTripsFrom(taps);

        assertEquals(1, trips.size());

        Trip trip = trips.get(0);
        assertEquals(LocalDateTime.of(2023, 1, 22, 13, 0), trip.getStarted());
        assertEquals(LocalDateTime.of(2023, 1, 22, 13, 10), trip.getFinished());
        assertEquals(Trip.Status.COMPLETED, trip.getStatus());
    }

    @Test
    public void testGenerateTripsFrom_CancelledTrip() {
        Tap onTap = new Tap(1, LocalDateTime.of(2023, 1, 22, 13, 0), Tap.Type.ON, "Stop1", "Company1", "Bus37", "5500005555555559");
        Tap offTap = new Tap(2, LocalDateTime.of(2023, 1, 22, 13, 5), Tap.Type.OFF, "Stop1", "Company1", "Bus37", "5500005555555559");
        List<Tap> taps = Arrays.asList(onTap, offTap);
        List<Trip> trips = TapProcessor.generateTripsFrom(taps);

        assertEquals(1, trips.size());

        Trip trip = trips.get(0);
        assertEquals(LocalDateTime.of(2023, 1, 22, 13, 0), trip.getStarted());
        assertEquals(LocalDateTime.of(2023, 1, 22, 13, 5), trip.getFinished());
        assertEquals(Trip.Status.CANCELLED, trip.getStatus());
    }

    @Test
    public void testGenerateTripsFrom_IncompleteTrip() {
        Tap onTap = new Tap(1, LocalDateTime.of(2023, 1, 22, 13, 0), Tap.Type.ON, "Stop1", "Company1", "Bus37", "5500005555555559");
        List<Tap> taps = Collections.singletonList(onTap);
        List<Trip> trips = TapProcessor.generateTripsFrom(taps);

        assertEquals(1, trips.size());

        Trip trip = trips.get(0);
        assertEquals(LocalDateTime.of(2023, 1, 22, 13, 0), trip.getStarted());
        assertEquals(Trip.Status.INCOMPLETE, trip.getStatus());
    }

    @Test
    public void testGenerateTripsFrom_TripNotFound() {
        Tap onTap = new Tap(1, LocalDateTime.of(2023, 1, 22, 13, 0), Tap.Type.OFF, "Stop1", "Company1", "Bus37", "5500005555555559");
        List<Tap> taps = Collections.singletonList(onTap);
        List<Trip> trips = TapProcessor.generateTripsFrom(taps);

        assertEquals(0, trips.size());
    }

    @Test
    public void testGenerateTripsFrom_DuplicateTap() {
        Tap onTap = new Tap(1, LocalDateTime.of(2023, 1, 22, 13, 0), Tap.Type.ON, "Stop1", "Company1", "Bus37", "5500005555555559");
        Tap offTap = new Tap(1, LocalDateTime.of(2023, 1, 22, 13, 5), Tap.Type.ON, "Stop1", "Company1", "Bus37", "5500005555555559");
        List<Tap> taps = Arrays.asList(onTap, offTap);
        List<Trip> trips = TapProcessor.generateTripsFrom(taps);

        assertEquals(1, trips.size());
    }




}
