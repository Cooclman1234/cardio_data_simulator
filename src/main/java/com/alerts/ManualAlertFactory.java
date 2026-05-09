package com.alerts;

public class ManualAlertFactory extends AlertFactory{

    @Override
    public Alert createAlert(int patientId, String condition, long timestamp) {
        return new ManualAlert(patientId, condition, timestamp);
    }
    
}
