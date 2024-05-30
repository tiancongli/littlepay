package com.littlepay;

import java.util.List;

/**
 * @author ltiancong@gmail.com
 * @date 2024/5/30 13:05
 */
public class Main {
    public static void main(String[] args) {
        List<Tap> taps = TapProcessor.importTapsFrom("src/main/resources" +
                "/taps.csv");
        List<Trip> trips = TapProcessor.generateTripsFrom(taps);
        TapProcessor.exportTripsTo("src/main/resources/trips.csv", trips);
    }
}
