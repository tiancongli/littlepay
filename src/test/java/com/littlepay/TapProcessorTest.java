package com.littlepay;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author ltiancong@gmail.com
 * @date 2024/5/30 13:05
 */
public class TapProcessorTest {
    @Test
    public void testImportTapsFrom() {
        List<Tap> taps = TapProcessor.importTapsFrom("src/test/resources/taps_test.csv");
        assertNotNull(taps);
        assertEquals(3, taps.size());

        Tap tap1 = taps.get(0);
        assertEquals(1, tap1.getId());
        assertEquals(LocalDateTime.parse("2023-01-22T13:00:00"), tap1.getDateTimeUTC());
        assertEquals("ON", tap1.getTapType());
        assertEquals("Stop1", tap1.getStopId());
        assertEquals("Company1", tap1.getCompanyId());
        assertEquals("Bus37", tap1.getBusId());
        assertEquals("5500005555555559", tap1.getPan());

        Tap tap2 = taps.get(1);
        assertEquals(2, tap2.getId());
        assertEquals(LocalDateTime.parse("2023-01-22T13:05:00"), tap2.getDateTimeUTC());
        assertEquals("OFF", tap2.getTapType());
        assertEquals("Stop2", tap2.getStopId());
        assertEquals("Company1", tap2.getCompanyId());
        assertEquals("Bus37", tap2.getBusId());
        assertEquals("5500005555555559", tap2.getPan());

        Tap tap3 = taps.get(2);
        assertEquals(3, tap3.getId());
        assertEquals(LocalDateTime.parse("2023-01-22T09:20:00"), tap3.getDateTimeUTC());
        assertEquals("ON", tap3.getTapType());
        assertEquals("Stop3", tap3.getStopId());
        assertEquals("Company1", tap3.getCompanyId());
        assertEquals("Bus36", tap3.getBusId());
        assertEquals("4111111111111111", tap3.getPan());
    }

    @Test
    public void testExportTripsToWriter() throws IOException {
        Trip trip1 = new Trip(
                LocalDateTime.of(2023, 1, 22, 13, 0),
                LocalDateTime.of(2023, 1, 22, 13, 5),
                300,
                "Stop1",
                "Stop2",
                3.25,
                "Company1",
                "Bus37",
                "5500005555555559",
                Trip.Status.COMPLETED
        );

        Trip trip2 = new Trip(
                LocalDateTime.of(2023, 1, 22, 9, 20),
                LocalDateTime.of(2023, 1, 22, 9, 30),
                600,
                "Stop3",
                "Stop1",
                7.30,
                "Company1",
                "Bus36",
                "4111111111111111",
                Trip.Status.INCOMPLETE
        );

        List<Trip> trips = Arrays.asList(trip1, trip2);

        // Use a StringWriter to capture the output
        StringWriter stringWriter = new StringWriter();
        BufferedWriter bufferedWriter = new BufferedWriter(stringWriter);

        // Call the method to test
        TapProcessor.exportTripsToWriter(bufferedWriter, trips);

        // Close the writer to flush the content
        bufferedWriter.close();

        // Expected CSV output
        String expectedOutput = "Started,Finished,DurationSecs,FromStopId,ToStopId,ChargeAmount,CompanyId,BusID,PAN,Status\n" +
                "2023-01-22T13:00,2023-01-22T13:05,300,Stop1,Stop2,$3.25,Company1,Bus37,5500005555555559,COMPLETED\n" +
                "2023-01-22T09:20,2023-01-22T09:30,600,Stop3,Stop1,$7.30,Company1,Bus36,4111111111111111,INCOMPLETE\n";

        // Verify the output
        assertEquals(expectedOutput, stringWriter.toString());
    }

    @Test
    public void testGenerateTripsFrom_CompleteTrip() {
        Tap onTap = new Tap(1, LocalDateTime.of(2023, 1, 22, 13, 0), "ON", "Stop1", "Company1", "Bus37", "5500005555555559");
        Tap offTap = new Tap(3, LocalDateTime.of(2023, 1, 22, 13, 10), "OFF", "Stop2", "Company1", "Bus37", "5500005555555559");
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
        Tap onTap = new Tap(1, LocalDateTime.of(2023, 1, 22, 13, 0), "ON", "Stop1", "Company1", "Bus37", "5500005555555559");
        Tap offTap = new Tap(2, LocalDateTime.of(2023, 1, 22, 13, 5), "OFF", "Stop1", "Company1", "Bus37", "5500005555555559");
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
        Tap onTap = new Tap(1, LocalDateTime.of(2023, 1, 22, 13, 0), "ON", "Stop1", "Company1", "Bus37", "5500005555555559");
        List<Tap> taps = Collections.singletonList(onTap);
        List<Trip> trips = TapProcessor.generateTripsFrom(taps);

        assertEquals(1, trips.size());

        Trip trip = trips.get(0);
        assertEquals(LocalDateTime.of(2023, 1, 22, 13, 0), trip.getStarted());
        assertEquals(Trip.Status.INCOMPLETE, trip.getStatus());
    }

    @Test
    public void testGenerateTripsFrom_TripNotFound() {
        Tap onTap = new Tap(1, LocalDateTime.of(2023, 1, 22, 13, 0), "OFF", "Stop1", "Company1", "Bus37", "5500005555555559");
        List<Tap> taps = Collections.singletonList(onTap);
        List<Trip> trips = TapProcessor.generateTripsFrom(taps);

        assertEquals(0, trips.size());
    }

    @Test
    public void testGenerateTripsFrom_DuplicateTap() {
        Tap onTap = new Tap(1, LocalDateTime.of(2023, 1, 22, 13, 0), "ON", "Stop1", "Company1", "Bus37", "5500005555555559");
        Tap offTap = new Tap(1, LocalDateTime.of(2023, 1, 22, 13, 5), "ON", "Stop1", "Company1", "Bus37", "5500005555555559");
        List<Tap> taps = Arrays.asList(onTap, offTap);
        List<Trip> trips = TapProcessor.generateTripsFrom(taps);

        assertEquals(1, trips.size());
    }




}
