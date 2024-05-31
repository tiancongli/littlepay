package com.littlepay;

import java.time.Duration;
import java.time.LocalDateTime;

public class Trip {
    private LocalDateTime started;
    private LocalDateTime finished;
    private long durationSecs;
    private String fromStopId;
    private String toStopId;
    private double chargeAmount;
    private String companyId;
    private String busId;
    private String pan;
    private Status status;

    public Trip(LocalDateTime started, LocalDateTime finished, long durationSecs, String fromStopId, String toStopId,
                double chargeAmount, String companyId, String busId, String pan, Status status) {
        this.started = started;
        this.finished = finished;
        this.durationSecs = durationSecs;
        this.fromStopId = fromStopId;
        this.toStopId = toStopId;
        this.chargeAmount = chargeAmount;
        this.companyId = companyId;
        this.busId = busId;
        this.pan = pan;
        this.status = status;
    }

    public Trip(Tap onTap) {
        this.started = onTap.getDateTimeUTC();
        this.fromStopId = onTap.getStopId();
        this.companyId = onTap.getCompanyId();
        this.busId = onTap.getBusId();
        this.pan = onTap.getPan();
    }

    public enum Status {
        COMPLETED,
        INCOMPLETE,
        CANCELLED
    }

    public static String getUniqueKeyByTap(Tap tap) {
        return tap.getBusId() + "-" + tap.getPan() + "-" + tap.getDateTimeUTC().toLocalDate();
    }

    public void complete(Tap offTap) {
        this.finished = offTap.getDateTimeUTC();
        this.durationSecs = Duration.between(started, finished).getSeconds();
        this.toStopId = offTap.getStopId();
        this.chargeAmount = Payment.calculateCost(fromStopId, toStopId);
        this.status = fromStopId.equals(toStopId) ? Status.CANCELLED : Status.COMPLETED;
    }

    public void incomplete() {
        this.finished = null;
        this.durationSecs = 0;
        this.toStopId = null;
        this.chargeAmount = Payment.calculateCost(fromStopId);
        this.status = Status.INCOMPLETE;
    }

    public LocalDateTime getStarted() {
        return started;
    }

    public LocalDateTime getFinished() {
        return finished;
    }

    public long getDurationSecs() {
        return durationSecs;
    }

    public String getFromStopId() {
        return fromStopId;
    }

    public String getToStopId() {
        return toStopId;
    }

    public double getChargeAmount() {
        return chargeAmount;
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

    public Status getStatus() {
        return status;
    }
}