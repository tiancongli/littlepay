package com.littlepay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TapProcessor {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static final Logger logger = LoggerFactory.getLogger(TapProcessor.class);

    public static List<Tap> importTapsFrom(String filePath) {
        List<Tap> taps = new ArrayList<>();
        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(filePath))) {
            bufferedReader.readLine(); // Skip header
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                taps.add(parseTap(line));
            }
        } catch (IOException e) {
            logger.error("Failed to read taps from file: {}", filePath, e);
            throw new RuntimeException("Failed to import taps. Please check the logs for more details.", e);
        }
        return taps;
    }

    public static void exportTripsTo(String filePath, List<Trip> trips) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            exportTripsToWriter(writer, trips);
        } catch (IOException e) {
            logger.error("Failed to write trips to file: {}", filePath, e);
            throw new RuntimeException("Failed to export trips. Please check the logs for more details.", e);
        }
    }

    public static List<Trip> generateTripsFrom(List<Tap> taps) {
        List<Trip> generatedTrips = new ArrayList<>();
        Map<String, Trip> activeTrips = new HashMap<>();

        taps.forEach(tap -> processTap(tap, activeTrips, generatedTrips));

        processRemainingTrips(activeTrips, generatedTrips);

        return generatedTrips;
    }

    private static Tap parseTap(String line) {
        String[] fields = line.split(",");
        return new Tap(
                Integer.parseInt(fields[0].trim()),
                LocalDateTime.parse(fields[1].trim(), DATE_TIME_FORMATTER),
                Tap.Type.valueOf(fields[2].trim().toUpperCase()),
                fields[3].trim(),
                fields[4].trim(),
                fields[5].trim(),
                fields[6].trim()
        );
    }

    public static void exportTripsToWriter(BufferedWriter writer, List<Trip> trips) throws IOException {
        writer.write("Started,Finished,DurationSecs,FromStopId,ToStopId,ChargeAmount,CompanyId,BusID,PAN,Status");
        writer.newLine();
        for (Trip trip : trips) {
            writer.write(formatTrip(trip));
            writer.newLine();
        }
    }

    private static String formatTrip(Trip trip) {
        return String.format("%s,%s,%d,%s,%s,$%.2f,%s,%s,%s,%s",
                trip.getStarted(),
                trip.getFinished(),
                trip.getDurationSecs(),
                trip.getFromStopId(),
                trip.getToStopId(),
                trip.getChargeAmount(),
                trip.getCompanyId(),
                trip.getBusId(),
                trip.getPan(),
                trip.getStatus());
    }

    private static void processTap(Tap tap, Map<String, Trip> activeTrips, List<Trip> trips) {
        String uniqueTripKey = Trip.getUniqueKeyByTap(tap);

        if (tap.getTapType() == Tap.Type.ON) {
            processTapOn(tap, uniqueTripKey, activeTrips);
        } else {
            processTapOff(tap, uniqueTripKey, activeTrips, trips);
        }
    }

    private static void processTapOn(Tap tap, String uniqueTripKey, Map<String, Trip> activeTrips) {
        if (activeTrips.containsKey(uniqueTripKey)) {
            logger.warn("Duplicate tap: {}", uniqueTripKey);
        } else {
            activeTrips.put(uniqueTripKey, new Trip(tap));
        }
    }

    private static void processTapOff(Tap tap, String uniqueTripKey, Map<String, Trip> activeTrips, List<Trip> trips) {
        Trip trip = activeTrips.remove(uniqueTripKey);
        if (trip == null) {
            logger.warn("Trip not found: {}", uniqueTripKey);
        } else {
            trip.complete(tap);
            trips.add(trip);
        }
    }

    private static void processRemainingTrips(Map<String, Trip> activeTrips, List<Trip> trips) {
        activeTrips.values().forEach(trip -> {
            trip.incomplete();
            trips.add(trip);
        });
    }
}
