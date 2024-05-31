package com.littlepay;

import java.time.LocalDateTime;

public class Tap {
    private final int id;
    private LocalDateTime dateTimeUTC;
    private Type tapType;
    private String stopId;
    private String companyId;
    private String busId;
    private String pan;

    public Tap(int id, LocalDateTime dateTimeUTC, Type tapType, String stopId, String companyId, String busId, String pan) {
        this.id = id;
        this.dateTimeUTC = dateTimeUTC;
        this.tapType = tapType;
        this.stopId = stopId;
        this.companyId = companyId;
        this.busId = busId;
        this.pan = pan;
    }

    public enum Type {
        ON,
        OFF
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getDateTimeUTC() {
        return dateTimeUTC;
    }

    public Type getTapType() {
        return tapType;
    }

    public String getStopId() {
        return stopId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getBusId() {
        return busId;
    }

    public String getPan() {
        return pan;
    }
}
