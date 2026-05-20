package alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.BloodPressureDiastolicStrategy;
import com.data_management.AlertStorage;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class BloodPressureDiastolicStrategyTest {

    @BeforeEach
    void setUp() {
        DataStorage.resetInstance();
    }

    @Test
    void testHighDiastolicPressureTriggersAlert() {
        // Arrange
        DataStorage db = DataStorage.getInstance();
        AlertStorage alertStorage = new AlertStorage();
        BloodPressureDiastolicStrategy strategy = new BloodPressureDiastolicStrategy();

        db.addPatientData("1", 125.0, "Diastolic Pressure", 1714376789050L);
        Patient patient = db.getAllPatients().get(0);
        List<PatientRecord> records = db.getRecords(patient.getPatientId(), 0L, Long.MAX_VALUE);

        // Act
        strategy.checkAlert(patient, records, alertStorage);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(1, alerts.size());
        assertEquals("high Diastolic Pressure", alerts.get(0).getCondition());
    }

    @Test
    void testLowDiastolicPressureTriggersAlert() {
        // Arrange
        DataStorage db = DataStorage.getInstance();
        AlertStorage alertStorage = new AlertStorage();
        BloodPressureDiastolicStrategy strategy = new BloodPressureDiastolicStrategy();

        db.addPatientData("1", 55.0, "Diastolic Pressure", 1714376789050L);
        Patient patient = db.getAllPatients().get(0);
        List<PatientRecord> records = db.getRecords(patient.getPatientId(), 0L, Long.MAX_VALUE);

        // Act
        strategy.checkAlert(patient, records, alertStorage);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(1, alerts.size());
        assertEquals("low Diastolic Pressure", alerts.get(0).getCondition());
    }

    @Test
    void testHighDiastolicPressureTriggersTrendAlert() {
        // Arrange
        DataStorage db = DataStorage.getInstance();
        AlertStorage alertStorage = new AlertStorage();
        BloodPressureDiastolicStrategy strategy = new BloodPressureDiastolicStrategy();

        db.addPatientData("1", 125.0, "Diastolic Pressure", 1714376789050L);
        db.addPatientData("1", 137.0, "Diastolic Pressure", 1714376789051L);
        db.addPatientData("1", 148.0, "Diastolic Pressure", 1714376789052L);
        Patient patient = db.getAllPatients().get(0);
        List<PatientRecord> records = db.getRecords(patient.getPatientId(), 0L, Long.MAX_VALUE);

        // Act
        strategy.checkAlert(patient, records, alertStorage);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(4, alerts.size());
        assertTrue(alerts.get(3).getCondition().contains("Trend alert - increasing Diastolic Pressure"));
    }

    @Test
    void testLowDiastolicPressureTriggersTrendAlert() {
        // Arrange
        DataStorage db = DataStorage.getInstance();
        AlertStorage alertStorage = new AlertStorage();
        BloodPressureDiastolicStrategy strategy = new BloodPressureDiastolicStrategy();

        db.addPatientData("1", 55.0, "Diastolic Pressure", 1714376789050L);
        db.addPatientData("1", 44.0, "Diastolic Pressure", 1714376789051L);
        db.addPatientData("1", 32.0, "Diastolic Pressure", 1714376789052L);
        Patient patient = db.getAllPatients().get(0);
        List<PatientRecord> records = db.getRecords(patient.getPatientId(), 0L, Long.MAX_VALUE);

        // Act
        strategy.checkAlert(patient, records, alertStorage);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(4, alerts.size());
        assertTrue(alerts.get(3).getCondition().contains("Trend alert - decreasing Diastolic Pressure"));
    }

    @Test
    void testIncreasingTrendTriggersWhenOnlyOneValueIsOutOfRange() {
        // Arrange
        DataStorage db = DataStorage.getInstance();
        AlertStorage alertStorage = new AlertStorage();
        BloodPressureDiastolicStrategy strategy = new BloodPressureDiastolicStrategy();

        db.addPatientData("1", 55.0, "Diastolic Pressure", 1714376789050L);
        db.addPatientData("1", 67.0, "Diastolic Pressure", 1714376789051L);
        db.addPatientData("1", 79.0, "Diastolic Pressure", 1714376789052L);
        Patient patient = db.getAllPatients().get(0);
        List<PatientRecord> records = db.getRecords(patient.getPatientId(), 0L, Long.MAX_VALUE);

        // Act
        strategy.checkAlert(patient, records, alertStorage);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(2, alerts.size());
        assertTrue(alerts.get(1).getCondition().contains("Trend alert - increasing Diastolic Pressure"));
    }

}
