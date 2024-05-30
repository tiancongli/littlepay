package com.littlepay;

import java.util.HashMap;
import java.util.Map;

public class Payment {
    private static final Map<String, Double> PRICE_TABLE = new HashMap<>();

    static {
        addPrice("Stop1", "Stop2", 3.25);
        addPrice("Stop2", "Stop3", 5.50);
        addPrice("Stop1", "Stop3", 7.30);
    }

    private static void addPrice(String fromStop, String toStop, double price) {
        PRICE_TABLE.put(fromStop + "-" + toStop, price);
        PRICE_TABLE.put(toStop + "-" + fromStop, price);
    }

    public static double calculateCost(String fromStop, String toStop) {
        return PRICE_TABLE.getOrDefault(fromStop + "-" + toStop, 0.0);
    }

    public static double calculateCost(String fromStop) {
        return PRICE_TABLE.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(fromStop))
                .map(Map.Entry::getValue)
                .max(Double::compare)
                .orElse(0.0);
    }
}
