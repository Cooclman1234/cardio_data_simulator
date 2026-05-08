package com.alerts;

import java.util.List;

import com.data_management.AlertStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public interface AlertStrategy {
    void checkAlert(Patient patient, List<PatientRecord> records, AlertStorage alertStorage);
}
