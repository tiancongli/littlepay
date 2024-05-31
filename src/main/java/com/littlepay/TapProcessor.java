package com.littlepay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TapProcessor {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static final Logger logger = LoggerFactory.getLogger(TapProcessor.class);

    public static List<Tap> importTapsFrom(String filePath) {
        return CSVTool.importFrom(filePath, TapProcessor::parseTap);
    }

    public static void exportTripsTo(String filePath, List<Trip> trips) {
        String header = "Started,Finished,DurationSecs,FromStopId,ToStopId,ChargeAmount,CompanyId,BusID,PAN,Status";
        CSVTool.exportTo(filePath, trips, TapProcessor::formatTrip, header);
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
