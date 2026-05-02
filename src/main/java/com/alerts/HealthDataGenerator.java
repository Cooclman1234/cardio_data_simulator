package com.alerts;

import java.util.HashMap;
import java.util.Map;

import com.data_management.AlertStorage;

/**
 * Simulates manually triggered alerts, e.g. when a nurse or patient
 * presses the alert button near a bed.
 * Alerts can be triggered and untriggered per patient.
 */
public class HealthDataGenerator {
    private AlertStorage alertStorage;
    private Map<String, Alert> activeAlerts = new HashMap<>();

    public HealthDataGenerator(AlertStorage alertStorage) {
        this.alertStorage = alertStorage;
    }

    /**
     * Triggers a manual alert for the given patient.
     * Simulates a nurse or patient pressing the alert button.
     *
     * @param patientId the ID of the patient triggering the alert
     * @return the generated Alert object
     */
    public Alert triggerAlert(String patientId) {
        Alert alert = new Alert(patientId, "Manual alert triggered", System.currentTimeMillis(), "triggered alert");
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
    public void untriggerAlert(String patientId, Alert alert) {
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
    public boolean isTriggered(String patientId) {
        return activeAlerts.containsKey(patientId);
    }

    /**
     * Returns the active alert for the given patient, or null if none exists.
     *
     * @param patientId the ID of the patient
     * @return the active Alert, or null
     */
    public Alert getActiveAlert(String patientId) {
        return activeAlerts.get(patientId);
    }
}
