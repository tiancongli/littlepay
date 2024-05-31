package com.littlepay;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CSVTool {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CSVTool.class);

    public static <T> List<T> importFrom(String filePath, Function<String, T> parseFunction) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(filePath))) {
            return bufferedReader.lines()
                    .skip(1) // Skip header
                    .map(parseFunction)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Failed to read from file: {}", filePath, e);
            throw new RuntimeException("Failed to import data. Please check the logs for more details.", e);
        }
    }

    public static <T> void exportTo(String filePath, List<T> data, Function<T, String> formatFunction, String header) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            exportToWriter(writer, data, formatFunction, header);
        } catch (IOException e) {
            logger.error("Failed to write to file: {}", filePath, e);
            throw new RuntimeException("Failed to export data. Please check the logs for more details.", e);
        }
    }

    public static <T> void exportToWriter(BufferedWriter writer, List<T> data, Function<T, String> formatFunction, String header) throws IOException {
        if (header != null && !header.isEmpty()) {
            writer.write(header);
            writer.newLine();
        }
        for (T item : data) {
            writer.write(formatFunction.apply(item));
            writer.newLine();
        }
    }
}
