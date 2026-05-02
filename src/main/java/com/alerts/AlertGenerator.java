package com.alerts;

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
    private static final long TEN_MINUTES_IN_MILLIS = 10 * 60 * 1000L;

    private DataStorage dataStorage;
    private AlertStorage alertStorage;


    // mistake : the second line is unnecessary, contents just repeats what the param explaining line already explains.
    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     *
     * @param dataStorage the data storage system that provides access to patient data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.alertStorage = new AlertStorage();
    }

    public AlertGenerator(DataStorage dataStorage, AlertStorage alertStorage) {
        this.dataStorage = dataStorage;
        this.alertStorage = alertStorage;
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
        if (patient == null || this.dataStorage.getRecords(patient.getPatientId(), 1700000000000L, 1800000000000L).isEmpty()) {
            return;
        }
        List<PatientRecord> list = this.dataStorage.getRecords(patient.getPatientId(), 1700000000000L, 1800000000000L);
        boolean lowSystolic = false;
        boolean lowSaturation = false;
        long lastLowSystolicTimestamp = 0;
        long lastLowSaturationTimestamp = 0;
        for (int i = 0; i < list.size(); i++) {
            PatientRecord record = list.get(i);
            String type = record.getRecordType();
            if (type.equals("Systolic Pressure")) {
                if (180 < record.getMeasurementValue()) {
                    Alert a = new Alert(String.valueOf(patient.getPatientId()), "high Systolic Pressure", record.getTimestamp());
                    triggerAlert(a);

                    if (hasIncreasingTrend(list, i, "Systolic Pressure")) {
                        Alert trend = new Alert(String.valueOf(patient.getPatientId()),
                                "Trend alert - increasing Systolic Pressure", record.getTimestamp(), "trend alert");
                        triggerAlert(trend);
                    }
                }

                if (90 > record.getMeasurementValue()) {
                    Alert a = new Alert(String.valueOf(patient.getPatientId()), "low Systolic Pressure", record.getTimestamp());
                    triggerAlert(a);
                    lowSystolic = true;
                    lastLowSystolicTimestamp = record.getTimestamp();

                    if (hasDecreasingTrend(list, i, "Systolic Pressure")) {
                        Alert trend = new Alert(String.valueOf(patient.getPatientId()),
                                "Trend alert - decreasing Systolic Pressure", record.getTimestamp(), "trend alert");
                        triggerAlert(trend);
                    }
                }
            } else if (type.equals("Diastolic Pressure")) {
                if (120 < record.getMeasurementValue()) {
                    Alert a = new Alert(String.valueOf(patient.getPatientId()), "high Diastolic Pressure", record.getTimestamp());
                    triggerAlert(a);

                    if (hasIncreasingTrend(list, i, "Diastolic Pressure")) {
                        Alert trend = new Alert(String.valueOf(patient.getPatientId()),
                                "Trend alert - increasing Diastolic Pressure", record.getTimestamp(), "trend alert");
                        triggerAlert(trend);
                    }
                }

                if (60 > record.getMeasurementValue()) {
                    Alert a = new Alert(String.valueOf(patient.getPatientId()), "low Diastolic Pressure", record.getTimestamp());
                    triggerAlert(a);

                    if (hasDecreasingTrend(list, i, "Diastolic Pressure")) {
                        Alert trend = new Alert(String.valueOf(patient.getPatientId()),
                                "Trend alert - decreasing Diastolic Pressure", record.getTimestamp(), "trend alert");
                        triggerAlert(trend);
                    }
                }
            } else if (type.equals("Saturation")) {
                if (92 > record.getMeasurementValue()) {
                    Alert a = new Alert(String.valueOf(patient.getPatientId()), "low Saturation", record.getTimestamp(),
                            "low saturation alert");
                    triggerAlert(a);
                    lowSaturation = true;
                    lastLowSaturationTimestamp = record.getTimestamp();
                }

                if (hasRapidDrop(list, i, "Saturation")) {
                    Alert rapidDrop = new Alert(String.valueOf(patient.getPatientId()), "Rapid drop in Saturation",
                            record.getTimestamp(), "rapid drop alert");
                    triggerAlert(rapidDrop);
                }
            }

            if (lowSystolic && lowSaturation) {
                Alert hha = new Alert(String.valueOf(patient.getPatientId()),
                        "Hypotensive Hypoxemia Alert", Math.max(lastLowSystolicTimestamp, lastLowSaturationTimestamp),
                        "Hypotensive Hypoxemia alert");
                triggerAlert(hha);
                lowSystolic = false;
                lowSaturation = false;
            }

            if (type.equals("ECG")) {
                if (isEcgPeakAlert(list, i)) {
                    Alert a = new Alert(String.valueOf(patient.getPatientId()), "Abnormal ECG peak", record.getTimestamp(),
                            "ECG data alert");
                    triggerAlert(a);
                }
            } 
        }
    }



    private boolean hasIncreasingTrend(List<PatientRecord> records, int currentIndex, String recordType) {
        PatientRecord previousRecord = findPreviousRecord(records, currentIndex - 1, recordType);
        if (previousRecord == null) {
            return false;
        }

        int previousIndex = findPreviousRecordIndex(records, currentIndex - 1, recordType);
        PatientRecord secondPreviousRecord = findPreviousRecord(records, previousIndex - 1, recordType);
        if (secondPreviousRecord == null) {
            return false;
        }

        double currentValue = records.get(currentIndex).getMeasurementValue();
        double previousValue = previousRecord.getMeasurementValue();
        double secondPreviousValue = secondPreviousRecord.getMeasurementValue();

        return currentValue > previousValue + 10 && previousValue > secondPreviousValue + 10;
    }

    private boolean hasDecreasingTrend(List<PatientRecord> records, int currentIndex, String recordType) {
        PatientRecord previousRecord = findPreviousRecord(records, currentIndex - 1, recordType);
        if (previousRecord == null) {
            return false;
        }

        int previousIndex = findPreviousRecordIndex(records, currentIndex - 1, recordType);
        PatientRecord secondPreviousRecord = findPreviousRecord(records, previousIndex - 1, recordType);
        if (secondPreviousRecord == null) {
            return false;
        }

        double currentValue = records.get(currentIndex).getMeasurementValue();
        double previousValue = previousRecord.getMeasurementValue();
        double secondPreviousValue = secondPreviousRecord.getMeasurementValue();

        return currentValue < previousValue - 10 && previousValue < secondPreviousValue - 10;
    }

    private boolean hasRapidDrop(List<PatientRecord> records, int currentIndex, String recordType) {
        PatientRecord previousRecord = findPreviousRecord(records, currentIndex - 1, recordType);
        if (previousRecord == null) {
            return false;
        }

        long timeDifference = records.get(currentIndex).getTimestamp() - previousRecord.getTimestamp();
        double valueDifference = previousRecord.getMeasurementValue() - records.get(currentIndex).getMeasurementValue();

        return timeDifference <= TEN_MINUTES_IN_MILLIS && valueDifference >= 5;
    }

    private boolean isEcgPeakAlert(List<PatientRecord> records, int currentIndex) {
        int windowSize = 10; 
        int count = 0;
        double sum = 0;

        for (int i = currentIndex - 1; i >= 0 && count < windowSize; i--) {
            if (records.get(i).getRecordType().equals("ECG")) {
                sum += records.get(i).getMeasurementValue();
                count++;
            }
        }

        if (count == 0) {
            return false; 
        }

        double average = sum / count;
        double currentValue = records.get(currentIndex).getMeasurementValue();

        return currentValue > average * 1.5;
    }

    private PatientRecord findPreviousRecord(List<PatientRecord> records, int startIndex, String recordType) {
        int index = findPreviousRecordIndex(records, startIndex, recordType);
        if (index < 0) {
            return null;
        }
        return records.get(index);
    }

    private int findPreviousRecordIndex(List<PatientRecord> records, int startIndex, String recordType) {
        for (int i = startIndex; i >= 0; i--) {
            if (records.get(i).getRecordType().equals(recordType)) {
                return i;
            }
        }
        return -1;
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
