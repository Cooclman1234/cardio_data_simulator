package com.alerts;

import java.util.List;

import com.data_management.AlertStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class BloodPressureSystolicStrategy implements AlertStrategy {
    private final AlertFactory bloodPressureFactory = new BloodPressureAlertFactory();

    @Override
    public void checkAlert(Patient patient, List<PatientRecord> records, AlertStorage alertStorage) {
        if (patient == null || records == null || alertStorage == null) {
            return;
        }

        for (int i = 0; i < records.size(); i++) {
            PatientRecord record = records.get(i);
            if (!"Systolic Pressure".equals(record.getRecordType())) {
                continue;
            }

            if (record.getMeasurementValue() > 180) {
                Alert high = bloodPressureFactory.createAlert(
                        patient.getPatientId(),
                        "high Systolic Pressure",
                        record.getTimestamp());
                Alert decoratedHigh = new PriorityAlertDecorator(high, "HIGH");
                storeAlert(decoratedHigh, alertStorage);

                if (hasIncreasingTrend(records, i, "Systolic Pressure")) {
                    Alert trend = bloodPressureFactory.createAlert(
                            patient.getPatientId(),
                            "Trend alert - increasing Systolic Pressure",
                            record.getTimestamp());
                    Alert decoratedTrend = new RepeatedAlertDecorator(trend, 3);
                    storeAlert(decoratedTrend, alertStorage);
                }
            }

            if (record.getMeasurementValue() < 90) {
                Alert low = bloodPressureFactory.createAlert(
                        patient.getPatientId(),
                        "low Systolic Pressure",
                        record.getTimestamp());
                Alert decoratedLow = new PriorityAlertDecorator(low, "HIGH");
                storeAlert(decoratedLow, alertStorage);

                if (hasDecreasingTrend(records, i, "Systolic Pressure")) {
                    Alert trend = bloodPressureFactory.createAlert(
                            patient.getPatientId(),
                            "Trend alert - decreasing Systolic Pressure",
                            record.getTimestamp());
                    Alert decoratedTrend = new RepeatedAlertDecorator(trend, 3);
                    storeAlert(decoratedTrend, alertStorage);
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

    private PatientRecord findPreviousRecord(List<PatientRecord> records, int startIndex, String recordType) {
        int index = findPreviousRecordIndex(records, startIndex, recordType);
        if (index < 0) {
            return null;
        }
        return records.get(index);
    }

    private int findPreviousRecordIndex(List<PatientRecord> records, int startIndex, String recordType) {
        for (int i = startIndex; i >= 0; i--) {
            if (recordType.equals(records.get(i).getRecordType())) {
                return i;
            }
        }
        return -1;
    }

    private void storeAlert(Alert alert, AlertStorage alertStorage) {
        System.out.println("ALERT: Patient " + alert.getPatientId()
                + " | Condition: " + alert.getCondition()
                + " | Time: " + alert.getTimestamp());
        alertStorage.storeAlert(alert);
    }
}
