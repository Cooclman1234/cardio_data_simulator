package com.alerts;

import java.util.List;

import com.data_management.AlertStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class BloodPressureDiastolicStrategy implements AlertStrategy {
    private static final String RECORD_TYPE = "Diastolic Pressure";
    private static final double HIGH_THRESHOLD = 120;
    private static final double LOW_THRESHOLD = 60;

    private final AlertFactory bloodPressureFactory = new BloodPressureAlertFactory();

    @Override
    public void checkAlert(Patient patient, List<PatientRecord> records, AlertStorage alertStorage) {
        if (patient == null || records == null || alertStorage == null) {
            return;
        }

        for (int i = 0; i < records.size(); i++) {
            PatientRecord record = records.get(i);
            if (!RECORD_TYPE.equals(record.getRecordType())) {
                continue;
            }

            if (record.getMeasurementValue() > HIGH_THRESHOLD) {
                Alert high = bloodPressureFactory.createAlert(
                        patient.getPatientId(),
                        "high Diastolic Pressure",
                        record.getTimestamp());
                Alert decoratedHigh = new PriorityAlertDecorator(high, "HIGH");
                storeAlert(decoratedHigh, alertStorage);
            }

            if (record.getMeasurementValue() < LOW_THRESHOLD) {
                Alert low = bloodPressureFactory.createAlert(
                        patient.getPatientId(),
                        "low Diastolic Pressure",
                        record.getTimestamp());
                Alert decoratedLow = new PriorityAlertDecorator(low, "HIGH");
                storeAlert(decoratedLow, alertStorage);
            }

            if (hasIncreasingTrend(records, i, RECORD_TYPE, LOW_THRESHOLD, HIGH_THRESHOLD)) {
                Alert trend = bloodPressureFactory.createAlert(
                        patient.getPatientId(),
                        "Trend alert - increasing Diastolic Pressure",
                        record.getTimestamp());
                Alert decoratedTrend = new RepeatedAlertDecorator(trend, 3);
                storeAlert(decoratedTrend, alertStorage);
            }

            if (hasDecreasingTrend(records, i, RECORD_TYPE, LOW_THRESHOLD, HIGH_THRESHOLD)) {
                Alert trend = bloodPressureFactory.createAlert(
                        patient.getPatientId(),
                        "Trend alert - decreasing Diastolic Pressure",
                        record.getTimestamp());
                Alert decoratedTrend = new RepeatedAlertDecorator(trend, 3);
                storeAlert(decoratedTrend, alertStorage);
            }
        }
    }

        private boolean hasIncreasingTrend(List<PatientRecord> records, int currentIndex, String recordType,
            double lowThreshold, double highThreshold) {
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

        boolean strongIncrease = currentValue > previousValue + 10 && previousValue > secondPreviousValue + 10;
        boolean hasOutOfRangeValue = isOutsideNormalRange(currentValue, lowThreshold, highThreshold)
            || isOutsideNormalRange(previousValue, lowThreshold, highThreshold)
            || isOutsideNormalRange(secondPreviousValue, lowThreshold, highThreshold);

        return strongIncrease && hasOutOfRangeValue;
    }

        private boolean hasDecreasingTrend(List<PatientRecord> records, int currentIndex, String recordType,
            double lowThreshold, double highThreshold) {
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

        boolean strongDecrease = currentValue < previousValue - 10 && previousValue < secondPreviousValue - 10;
        boolean hasOutOfRangeValue = isOutsideNormalRange(currentValue, lowThreshold, highThreshold)
                || isOutsideNormalRange(previousValue, lowThreshold, highThreshold)
                || isOutsideNormalRange(secondPreviousValue, lowThreshold, highThreshold);

        return strongDecrease && hasOutOfRangeValue;
    }

    private boolean isOutsideNormalRange(double value, double lowThreshold, double highThreshold) {
        return value < lowThreshold || value > highThreshold;
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
