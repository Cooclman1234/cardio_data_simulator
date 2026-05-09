package com.alerts;

public class BloodPressureAlertFactory extends AlertFactory{

    @Override
    public Alert createAlert(int patientId, String condition, long timestamp) {
        return new BloodPressureAlert(patientId, condition, timestamp);
    }
    
}
