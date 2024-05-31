package com.littlepay;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CSVToolTest {

    @Test
    public void testImportFrom() throws IOException {
        List<Tap> taps = CSVTool.importFrom("src/test/resources/taps_test.csv", line -> {
            String[] fields = line.split(",");
            return new Tap(
                    Integer.parseInt(fields[0].trim()),
                    LocalDateTime.parse(fields[1].trim(), TapProcessor.DATE_TIME_FORMATTER),
                    Tap.Type.valueOf(fields[2].trim().toUpperCase()),
                    fields[3].trim(),
                    fields[4].trim(),
                    fields[5].trim(),
                    fields[6].trim()
            );
        });

        assertNotNull(taps);
        assertEquals(3, taps.size());

        Tap tap1 = taps.get(0);
        assertEquals(1, tap1.getId());
        assertEquals(LocalDateTime.parse("2023-01-22T13:00:00"), tap1.getDateTimeUTC());
        assertEquals(Tap.Type.ON, tap1.getTapType());
        assertEquals("Stop1", tap1.getStopId());
        assertEquals("Company1", tap1.getCompanyId());
        assertEquals("Bus37", tap1.getBusId());
        assertEquals("5500005555555559", tap1.getPan());

        Tap tap2 = taps.get(1);
        assertEquals(2, tap2.getId());
        assertEquals(LocalDateTime.parse("2023-01-22T13:05:00"), tap2.getDateTimeUTC());
        assertEquals(Tap.Type.OFF, tap2.getTapType());
        assertEquals("Stop2", tap2.getStopId());
        assertEquals("Company1", tap2.getCompanyId());
        assertEquals("Bus37", tap2.getBusId());
        assertEquals("5500005555555559", tap2.getPan());

        Tap tap3 = taps.get(2);
        assertEquals(3, tap3.getId());
        assertEquals(LocalDateTime.parse("2023-01-22T09:20:00"), tap3.getDateTimeUTC());
        assertEquals(Tap.Type.ON, tap3.getTapType());
        assertEquals("Stop3", tap3.getStopId());
        assertEquals("Company1", tap3.getCompanyId());
        assertEquals("Bus36", tap3.getBusId());
        assertEquals("4111111111111111", tap3.getPan());
    }

    @Test
    public void testExportToWriter() throws IOException {
        List<Trip> trips = getTrips();

        StringWriter stringWriter = new StringWriter();

        try (BufferedWriter bufferedWriter = new BufferedWriter(stringWriter)) {
            CSVTool.exportToWriter(bufferedWriter, trips, trip -> String.format("%s,%s,%d,%s,%s,$%.2f,%s,%s,%s,%s",
                            trip.getStarted(),
                            trip.getFinished(),
                            trip.getDurationSecs(),
                            trip.getFromStopId(),
                            trip.getToStopId(),
                            trip.getChargeAmount(),
                            trip.getCompanyId(),
                            trip.getBusId(),
                            trip.getPan(),
                            trip.getStatus()),
                    "Started,Finished,DurationSecs,FromStopId,ToStopId,ChargeAmount,CompanyId,BusID,PAN,Status");
        }

        String expectedOutput = """
                Started,Finished,DurationSecs,FromStopId,ToStopId,ChargeAmount,CompanyId,BusID,PAN,Status
                2023-01-22T13:00,2023-01-22T13:05,300,Stop1,Stop2,$3.25,Company1,Bus37,5500005555555559,COMPLETED
                2023-01-22T09:20,2023-01-22T09:30,600,Stop3,Stop1,$7.30,Company1,Bus36,4111111111111111,INCOMPLETE
                """;

        assertEquals(expectedOutput, stringWriter.toString());
    }

    private static List<Trip> getTrips() {
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

        return Arrays.asList(trip1, trip2);
    }
}
