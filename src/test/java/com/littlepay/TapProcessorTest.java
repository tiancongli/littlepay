package com.littlepay;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author ltiancong@gmail.com
 * @date 2024/5/30 13:05
 */
public class TapProcessorTest {
    @Test
    public void ImportTapsFrom() {
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
}
