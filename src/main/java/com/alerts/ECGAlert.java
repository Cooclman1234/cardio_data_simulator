package com.alerts;

public class ECGAlert implements Alert {
    private final int patientId;
    private final String condition;
    private final long timestamp;

    public ECGAlert(int patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    @Override
    public int getPatientId() {
        return patientId;
    }

    @Override
    public String getCondition() {
        return condition;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String getType() {
        return "ECG Alert";
    }
    
}
