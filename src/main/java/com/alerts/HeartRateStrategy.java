package com.alerts;

import java.util.List;

import com.data_management.AlertStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class HeartRateStrategy implements AlertStrategy {
	private final AlertFactory ecgFactory = new ECGAlertFactory();

	@Override
	public void checkAlert(Patient patient, List<PatientRecord> records, AlertStorage alertStorage) {
		if (patient == null || records == null || alertStorage == null) {
			return;
		}

		for (int i = 0; i < records.size(); i++) {
			PatientRecord record = records.get(i);
			if (!"ECG".equals(record.getRecordType())) {
				continue;
			}

			if (isEcgPeakAlert(records, i)) {
				Alert alert = ecgFactory.createAlert(
						patient.getPatientId(),
						"Abnormal ECG peak",
						record.getTimestamp());
				storeAlert(alert, alertStorage);
			}
		}
	}

	private boolean isEcgPeakAlert(List<PatientRecord> records, int currentIndex) {
		int windowSize = 10;
		int count = 0;
		double sum = 0;

		for (int i = currentIndex - 1; i >= 0 && count < windowSize; i--) {
			if ("ECG".equals(records.get(i).getRecordType())) {
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

	private void storeAlert(Alert alert, AlertStorage alertStorage) {
		System.out.println("ALERT: Patient " + alert.getPatientId()
				+ " | Condition: " + alert.getCondition()
				+ " | Time: " + alert.getTimestamp());
		alertStorage.storeAlert(alert);
	}
}
