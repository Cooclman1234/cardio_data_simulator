package com.alerts;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.data_management.AlertStorage;

/**
 * Simulates manually triggered alerts, e.g. when a nurse or patient
 * presses the alert button near a bed.
 * Alerts can be triggered and untriggered per patient.
 */
public class HealthDataGenerator {
    private final AlertStorage alertStorage;
    private final Map<Integer, Alert> activeAlerts = new HashMap<>();
    private final ManualAlertFactory factory;

    public HealthDataGenerator(AlertStorage alertStorage) {
        this(alertStorage, new ManualAlertFactory());
    }

    public HealthDataGenerator(AlertStorage alertStorage, ManualAlertFactory factory) {
        this.alertStorage = Objects.requireNonNull(alertStorage, "alertStorage must not be null");
        this.factory = factory != null ? factory : new ManualAlertFactory();
    }

    /**
     * Triggers a manual alert for the given patient.
     * Simulates a nurse or patient pressing the alert button.
     *
     * @param patientId the ID of the patient triggering the alert
     * @return the generated Alert object
     */
    public Alert triggerAlert(int patientId) {
        Alert alert = this.factory.createAlert(patientId, "Manual alert triggered", System.currentTimeMillis());
        activeAlerts.put(patientId, alert);
        this.alertStorage.storeAlert(alert);
        System.out.println("ALERT TRIGGERED: Patient " + patientId + " | Time: " + alert.getTimestamp());
        return alert;
    }

    /**
     * Untriggers the active manual alert for the given patient.
     * Called when the alert condition is resolved.
     *
     * @param patientId the ID of the patient whose alert is being resolved
     */
    public void untriggerAlert(int patientId, Alert alert) {
        if (activeAlerts.containsKey(patientId)) {
            activeAlerts.remove(patientId);
            alertStorage.removeAlert(alert);
            System.out.println("ALERT RESOLVED: Patient " + patientId);
        }
    }

    /**
     * Returns whether the given patient currently has an active manual alert.
     *
     * @param patientId the ID of the patient to check
     * @return true if the patient has an active alert, false otherwise
     */
    public boolean isTriggered(int patientId) {
        return activeAlerts.containsKey(patientId);
    }

    /**
     * Returns the active alert for the given patient, or null if none exists.
     *
     * @param patientId the ID of the patient
     * @return the active Alert, or null
     */
    public Alert getActiveAlert(int patientId) {
        return activeAlerts.get(patientId);
    }
}
