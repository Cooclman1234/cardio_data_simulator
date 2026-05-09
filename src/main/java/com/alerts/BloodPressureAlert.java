package com.alerts;

public class BloodPressureAlert implements Alert{
    private final int patientId;
    private final String condition;
    private final long timestamp;

    public BloodPressureAlert(int patientId, String condition, long timestamp) {
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
        return "Blood Pressure";
    }
    
}
