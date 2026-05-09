package com.alerts;

import java.util.ArrayList;
import java.util.List;

import com.data_management.AlertStorage;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

// mistake : the class javaDoc begins with "{...}" which is not allowed
/**
 * Monitors patient data and generates alerts when predefined health conditions are met.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private AlertStorage alertStorage;
    private List<AlertStrategy> strategies;
    private final AlertFactory bloodPressureFactory = new BloodPressureAlertFactory();


    // mistake : the second line is unnecessary, contents just repeats what the param explaining line already explains.
    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     *
     * @param dataStorage the data storage system that provides access to patient data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.alertStorage = new AlertStorage();
        initStrategies();
    }

    public AlertGenerator(DataStorage dataStorage, AlertStorage alertStorage) {
        this.dataStorage = dataStorage;
        this.alertStorage = alertStorage;
        initStrategies();
    }

    // mistake : the "will be triggered" somehow was wrapped in another line, even there is space in the line above it
    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        if (patient == null) {
            return;
        }

        List<PatientRecord> records = this.dataStorage.getRecords(patient.getPatientId(), 1700000000000L, 1800000000000L);
        if (records.isEmpty()) {
            return;
        }

        for (AlertStrategy strategy : strategies) {
            strategy.checkAlert(patient, records, alertStorage);
        }

        triggerHypotensiveHypoxemiaAlert(patient, records);
    }

    private void initStrategies() {
        this.strategies = new ArrayList<>();
        this.strategies.add(new BloodPressureSystolicStrategy());
        this.strategies.add(new BloodPressureDiastolicStrategy());
        this.strategies.add(new OxygenStrategySaturation());
        this.strategies.add(new HeartRateStrategy());
    }

    private void triggerHypotensiveHypoxemiaAlert(Patient patient, List<PatientRecord> records) {
        long lastLowSystolicTimestamp = 0;
        long lastLowSaturationTimestamp = 0;
        boolean lowSystolicFound = false;
        boolean lowSaturationFound = false;

        for (PatientRecord record : records) {
            if ("Systolic Pressure".equals(record.getRecordType()) && record.getMeasurementValue() < 90) {
                lowSystolicFound = true;
                lastLowSystolicTimestamp = record.getTimestamp();
            }
            if ("Saturation".equals(record.getRecordType()) && record.getMeasurementValue() < 92) {
                lowSaturationFound = true;
                lastLowSaturationTimestamp = record.getTimestamp();
            }
        }

        if (lowSystolicFound && lowSaturationFound) {
            Alert hha = bloodPressureFactory.createAlert(patient.getPatientId(),
                    "Hypotensive Hypoxemia Alert", Math.max(lastLowSystolicTimestamp, lastLowSaturationTimestamp));
            triggerAlert(hha);
        }
    }
    /**
     * Triggers an alert for the monitoring system. Prints alert out in the consle and 
     * stores alert in AlertStorage
     * 
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        // Implementation might involve logging the alert or notifying staff

        System.out.println("ALERT: Patient " + alert.getPatientId() +
        " | Condition: " + alert.getCondition() +
        " | Time: " + alert.getTimestamp());
        alertStorage.storeAlert(alert);

    }
}
