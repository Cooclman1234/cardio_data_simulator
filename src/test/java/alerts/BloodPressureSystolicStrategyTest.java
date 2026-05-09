package alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.BloodPressureSystolicStrategy;
import com.data_management.AlertStorage;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class BloodPressureSystolicStrategyTest {

    @BeforeEach
    void setUp() {
        DataStorage.resetInstance();
    }

    @Test
    void testHighSystolicPressureTriggersAlert() {
        // Arrange
        DataStorage db = DataStorage.getInstance();
        AlertStorage alertStorage = new AlertStorage();
        BloodPressureSystolicStrategy strategy = new BloodPressureSystolicStrategy();

        db.addPatientData(1, 185.0, "Systolic Pressure", 1714376789050L);
        Patient patient = db.getAllPatients().get(0);
        List<PatientRecord> records = db.getRecords(patient.getPatientId(), 0L, Long.MAX_VALUE);

        // Act
        strategy.checkAlert(patient, records, alertStorage);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(1, alerts.size());
        assertEquals("high Systolic Pressure", alerts.get(0).getCondition());
    }

    @Test
    void testLowSystolicPressureTriggersAlert() {
        // Arrange
        DataStorage db = DataStorage.getInstance();
        AlertStorage alertStorage = new AlertStorage();
        BloodPressureSystolicStrategy strategy = new BloodPressureSystolicStrategy();

        db.addPatientData(1, 85.0, "Systolic Pressure", 1714376789050L);
        Patient patient = db.getAllPatients().get(0);
        List<PatientRecord> records = db.getRecords(patient.getPatientId(), 0L, Long.MAX_VALUE);

        // Act
        strategy.checkAlert(patient, records, alertStorage);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(1, alerts.size());
        assertEquals("low Systolic Pressure", alerts.get(0).getCondition());
    }

    @Test
    void testHighSystolicPressureTriggersTrendAlert() {
        // Arrange
        DataStorage db = DataStorage.getInstance();
        AlertStorage alertStorage = new AlertStorage();
        BloodPressureSystolicStrategy strategy = new BloodPressureSystolicStrategy();

        db.addPatientData(1, 185.0, "Systolic Pressure", 1714376789050L);
        db.addPatientData(1, 197.0, "Systolic Pressure", 1714376789051L);
        db.addPatientData(1, 208.0, "Systolic Pressure", 1714376789052L);
        Patient patient = db.getAllPatients().get(0);
        List<PatientRecord> records = db.getRecords(patient.getPatientId(), 0L, Long.MAX_VALUE);

        // Act
        strategy.checkAlert(patient, records, alertStorage);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(4, alerts.size());
        assertEquals("Trend alert - increasing Systolic Pressure", alerts.get(3).getCondition());
    }

    @Test
    void testLowSystolicPressureTriggersTrendAlert() {
        // Arrange
        DataStorage db = DataStorage.getInstance();
        AlertStorage alertStorage = new AlertStorage();
        BloodPressureSystolicStrategy strategy = new BloodPressureSystolicStrategy();

        db.addPatientData(1, 89.0, "Systolic Pressure", 1714376789050L);
        db.addPatientData(1, 78.0, "Systolic Pressure", 1714376789051L);
        db.addPatientData(1, 66.0, "Systolic Pressure", 1714376789052L);
        Patient patient = db.getAllPatients().get(0);
        List<PatientRecord> records = db.getRecords(patient.getPatientId(), 0L, Long.MAX_VALUE);

        // Act
        strategy.checkAlert(patient, records, alertStorage);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(4, alerts.size());
        assertEquals("Trend alert - decreasing Systolic Pressure", alerts.get(3).getCondition());
    }
}
