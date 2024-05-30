package com.littlepay;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author ltiancong@gmail.com
 * @date 2024/5/30 12:26
 */
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
    private TripStatus status;

    public Trip(Tap onTap, Tap offTap, double chargeAmount, TripStatus status) {
        this.started = onTap.getDateTimeUTC();
        this.finished = offTap.getDateTimeUTC();
        this.durationSecs = Duration.between(started, finished).getSeconds();
        this.fromStopId = onTap.getStopId();
        this.toStopId = offTap.getStopId();
        this.chargeAmount = chargeAmount;
        this.companyId = onTap.getCompanyId();
        this.busId = onTap.getBusId();
        this.pan = onTap.getPan();
        this.status = status;
    }

    public Trip(Tap onTap, double chargeAmount, TripStatus status) {
        this.started = onTap.getDateTimeUTC();
        this.finished = null;
        this.durationSecs = 0;
        this.fromStopId = onTap.getStopId();
        this.toStopId = null;
        this.chargeAmount = chargeAmount;
        this.companyId = onTap.getCompanyId();
        this.busId = onTap.getBusId();
        this.pan = onTap.getPan();
        this.status = status;
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

    public TripStatus getStatus() {
        return status;
    }
}