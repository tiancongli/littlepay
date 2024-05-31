package com.littlepay;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Tap> taps = TapProcessor.importTapsFrom("src/main/resources" +
                "/taps.csv");
        List<Trip> trips = TapProcessor.generateTripsFrom(taps);
        TapProcessor.exportTripsTo("src/main/resources/trips.csv", trips);
    }
}
