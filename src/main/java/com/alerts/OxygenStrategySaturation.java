package com.alerts;

import java.util.List;

import com.data_management.AlertStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class OxygenStrategySaturation implements AlertStrategy {
	private static final long TEN_MINUTES_IN_MILLIS = 10 * 60 * 1000L;
	private final AlertFactory bloodOxygenFactory = new BloodOxygenAlertFactory();

	@Override
	public void checkAlert(Patient patient, List<PatientRecord> records, AlertStorage alertStorage) {
		if (patient == null || records == null || alertStorage == null) {
			return;
		}

		for (int i = 0; i < records.size(); i++) {
			PatientRecord record = records.get(i);
			if (!"Saturation".equals(record.getRecordType())) {
				continue;
			}

			if (record.getMeasurementValue() < 92) {
				Alert low = bloodOxygenFactory.createAlert(
						patient.getPatientId(),
						"low Saturation",
						record.getTimestamp());
				storeAlert(low, alertStorage);
			}

			if (hasRapidDrop(records, i, "Saturation")) {
				Alert rapidDrop = bloodOxygenFactory.createAlert(
						patient.getPatientId(),
						"Rapid drop in Saturation",
						record.getTimestamp());
				storeAlert(rapidDrop, alertStorage);
			}
		}
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

	private PatientRecord findPreviousRecord(List<PatientRecord> records, int startIndex, String recordType) {
		for (int i = startIndex; i >= 0; i--) {
			if (recordType.equals(records.get(i).getRecordType())) {
				return records.get(i);
			}
		}
		return null;
	}

	private void storeAlert(Alert alert, AlertStorage alertStorage) {
		System.out.println("ALERT: Patient " + alert.getPatientId()
				+ " | Condition: " + alert.getCondition()
				+ " | Time: " + alert.getTimestamp());
		alertStorage.storeAlert(alert);
	}
}
