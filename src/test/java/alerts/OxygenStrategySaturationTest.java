package alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.OxygenStrategySaturation;
import com.data_management.AlertStorage;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class OxygenStrategySaturationTest {

    @BeforeEach
    void setUp() {
        DataStorage.resetInstance();
    }

    @Test
    void testLowSaturation() {
        // Arrange
        DataStorage db = DataStorage.getInstance();
        AlertStorage alertStorage = new AlertStorage();
        OxygenStrategySaturation strategy = new OxygenStrategySaturation();

        db.addPatientData(1, 88.0, "Saturation", 1714376789050L);
        Patient patient = db.getAllPatients().get(0);
        List<PatientRecord> records = db.getRecords(patient.getPatientId(), 0L, Long.MAX_VALUE);

        // Act
        strategy.checkAlert(patient, records, alertStorage);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(1, alerts.size());
        assertEquals("low Saturation", alerts.get(0).getCondition());
    }

    @Test
    void testRapidDropSaturation() {
        // Arrange
        DataStorage db = DataStorage.getInstance();
        AlertStorage alertStorage = new AlertStorage();
        OxygenStrategySaturation strategy = new OxygenStrategySaturation();

        db.addPatientData(1, 90.0, "Saturation", 1714376789050L);
        db.addPatientData(1, 80.0, "Saturation", 1714376789051L);
        Patient patient = db.getAllPatients().get(0);
        List<PatientRecord> records = db.getRecords(patient.getPatientId(), 0L, Long.MAX_VALUE);

        // Act
        strategy.checkAlert(patient, records, alertStorage);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(3, alerts.size());
        assertEquals("Rapid drop in Saturation", alerts.get(2).getCondition());
    }
}
