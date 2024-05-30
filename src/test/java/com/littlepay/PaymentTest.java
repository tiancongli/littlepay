package com.littlepay;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PaymentTest {

    @Test
    public void testCalculateCostBetweenStops() {
        // Test known route costs
        assertEquals(3.25, Payment.calculateCost("Stop1", "Stop2"), 0.01);
        assertEquals(5.50, Payment.calculateCost("Stop2", "Stop3"), 0.01);
        assertEquals(7.30, Payment.calculateCost("Stop1", "Stop3"), 0.01);

        // Test reversed route costs
        assertEquals(3.25, Payment.calculateCost("Stop2", "Stop1"), 0.01);
        assertEquals(5.50, Payment.calculateCost("Stop3", "Stop2"), 0.01);
        assertEquals(7.30, Payment.calculateCost("Stop3", "Stop1"), 0.01);

        // Test non-existent route
        assertEquals(0.0, Payment.calculateCost("Stop1", "Stop4"), 0.01);

        // Test same stop
        assertEquals(0.0, Payment.calculateCost("Stop1", "Stop1"), 0.01);
    }

    @Test
    public void testCalculateMaxCostFromStop() {
        // Test maximum cost from a stop with multiple routes
        assertEquals(7.30, Payment.calculateCost("Stop1"), 0.01);
        assertEquals(5.50, Payment.calculateCost("Stop2"), 0.01);
        assertEquals(7.30, Payment.calculateCost("Stop3"), 0.01);

        // Test maximum cost from a stop with no routes
        assertEquals(0.0, Payment.calculateCost("Stop4"), 0.01);
    }
}
