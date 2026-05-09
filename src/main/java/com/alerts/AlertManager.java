package com.alerts;

import com.data_management.AlertStorage;
import com.data_management.DataStorage;
import com.data_management.Patient;

/**
 * Manages Alerting process, which contains an AlertStorage, and two alert generators
 */
public class AlertManager {
    private AlertStorage alertStorage;
    private AlertGenerator alertGenerator;
    private HealthDataGenerator healthDataGenerator;

    /**
     * Creates an AlertManager. 
     * 
     * @param dataStorage teh required dataStorage for alertGenerator
     */
    public AlertManager(DataStorage dataStorage) {
        this.alertStorage = new AlertStorage();
        this.alertGenerator = new AlertGenerator(dataStorage, alertStorage);
        this.healthDataGenerator = new HealthDataGenerator(alertStorage);
    }

    /**
     * evaluates patientData whihc is done by alertGenerator. 
     * 
     * @param patient the patient for which data is being evaluated for
     */
    public void evaluatePatient(Patient patient) {
        alertGenerator.evaluateData(patient); 
    }

    /**
     * Triggers an alert, whihc simulates a manual alert signal.
     * @param patientId the patient from whom the alert is triggered 
     */
    public void manualTrigger(int patientId) {
        healthDataGenerator.triggerAlert(patientId); 
    }

    /**
     * Untriggers a triggered alert. Gives patients the option to untrigger alerts.
     * 
     * @param patientId the patient from whom the alert is triggered 
     */
    public void manualUntrigger(int patientId) {
        Alert alert = healthDataGenerator.getActiveAlert(patientId);
        if (alert != null) {
            healthDataGenerator.untriggerAlert(patientId, alert);
        }
    }
}
