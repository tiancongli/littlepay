package com.littlepay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ltiancong@gmail.com
 * @date 2024/5/30 13:05
 */
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

    public static List<Trip> generateTripsFrom(List<Tap> taps) {
        return null;
    }

    public static void exportTripsTo(String s, List<Trip> trips) {
    }

    private static Tap parseTap(String line) {
        String[] fields = line.split(",");
        return new Tap(
                Integer.parseInt(fields[0].trim()),
                LocalDateTime.parse(fields[1].trim(), DATE_TIME_FORMATTER),
                fields[2].trim(),
                fields[3].trim(),
                fields[4].trim(),
                fields[5].trim(),
                fields[6].trim()
        );
    }
}
