# Tap & Trip Project

## Overview

The Tap & Trip project simulates a transit system where passengers use credit cards to tap on and off buses. 
The system records these taps and processes them to generate trips, 
calculating charges based on the stops where passengers tap on and off. 
It handles complete, incomplete, and cancelled trips.

## Features
- **Taps Reading**: Read and parse tap events from a CSV file.
- **Trip Generation**: Create trips from tap events.
- **Charge Calculation**: Calculate the cost of each trip based on predefined fare rules.
- **Trip Status Handling**: Mark trips as completed, incomplete, or cancelled based on the tap events.
- **CSV Utility**: General CSV import and export functionality to handle different types of data.

## Assumptions
- The tap events recorded in the `taps.csv` file are in increasing order by `DateTimeUTC`.
- A new trip will be created when a new `ON` tap event occurs, and the trip is recognized by a unique key, which is the combination of the date, PAN, and busId.
- A trip will be completed when an `OFF` tap event with the same unique key occurs.
- All trips should be done in one natural day. If there is an `ON` tap but no `OFF` tap until midnight of the day, it is an incomplete trip.
- If another `ON` tap occurs with the same unique key of a trip before it completes, it is a duplicate tap and should be ignored.
- If an `OFF` tap occurs with a unique key that doesn't belong to any existing trips, it is an invalid tap and should be ignored.
- When calculating the payment, if the stop is not recorded in the price table, the trip is considered a non-existent route, and the user won't be charged.

## Technologies Used

- **Java**: Core programming language.
- **JUnit**: Testing framework.
- **Mockito**: Mocking framework for unit tests.
- **SLF4J**: Logging framework.
- **Maven**: Dependency management and build tool.

## Project Structure

```
littlepay/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── littlepay/
│   │   │           ├── CSVTool.java
│   │   │           ├── Main.java
│   │   │           ├── Payment.java
│   │   │           ├── Tap.java
│   │   │           ├── TapProcessor.java
│   │   │           └── Trip.java
│   │   ├── resources/
│   │   │   ├── taps.csv
│   │   │   └── trips.csv
│   │   │ 
│   ├── test/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── littlepay/
│   │   │           ├── CSVToolTest.java
│   │   │           ├── PaymentTest.java
│   │   │           ├── TapProcessorTest.java
│   │   │           └── TripTest.java
│   │   ├── resources/
│   │   │   └── taps_test.csv
├── pom.xml
└── README.md
```

## Usage

### Prerequisites

- Java 17 or higher
- Maven

### Running the Application

1. **Clone the Repository**:

    ```bash
    git clone https://github.com/tiancongli/littlepay.git
    cd littlepay
    ```

2. **Build the Project**:

    ```bash
    mvn clean install
    ```

3. **Run the Application**:
    - Replace `main/resources/taps.csv` with your own file of tap events.
    - Execute the `Main.main()` method to process the taps and generate trips. This can be done using Maven:
      ```bash
      mvn exec:java
      ```
    - Find your generated file at `main/resources/trips.csv`.

### Running Tests

To run the unit tests, use the following command:

```bash
mvn test
```

## Classes
### `Main`

The entry point of the program. It reads input from the resources folder and generates trips.

### `Tap`

Represents a tap event with attributes such as ID, dateTimeUTC, tapType, stopId, companyId, busId, and pan.

### `Trip`

Represents a trip generated from tap events. Contains logic for completing, cancelling, and marking trips as incomplete.

### `TapProcessor`

Contains the main logic for processing taps and generating trips.

### `Payment`

Contains fare rules and methods for calculating trip costs.

### `CSVTool`
Provides general CSV import and export functionality. Handles reading and writing of CSV files using functional interfaces for custom processing.
