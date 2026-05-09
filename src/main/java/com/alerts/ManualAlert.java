package com.alerts;

public class ManualAlert implements Alert {
    private final int patientId;
    private final String condition;
    private final long timestamp;
    
    public ManualAlert(int patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }
    
    @Override
    public String getType() { 
        return "Manual alert"; 
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
}