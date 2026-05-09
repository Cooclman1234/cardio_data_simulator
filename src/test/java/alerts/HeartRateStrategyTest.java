package alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.HeartRateStrategy;
import com.data_management.AlertStorage;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class HeartRateStrategyTest {

    @BeforeEach
    void setUp() {
        DataStorage.resetInstance();
    }

    @Test
    void testEcgAlertTriggersForAbnormalPeak() {
        // Arrange
        DataStorage db = DataStorage.getInstance();
        AlertStorage alertStorage = new AlertStorage();
        HeartRateStrategy strategy = new HeartRateStrategy();

        db.addPatientData(1, 100.0, "ECG", 1714376789050L);
        db.addPatientData(1, 200.0, "ECG", 1714376789051L);
        Patient patient = db.getAllPatients().get(0);
        List<PatientRecord> records = db.getRecords(patient.getPatientId(), 0L, Long.MAX_VALUE);

        // Act
        strategy.checkAlert(patient, records, alertStorage);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Abnormal ECG peak", alerts.get(0).getCondition());
    }

    @Test
    void testEcgAlertDoesNotTriggerForNormalValues() {
        // Arrange
        DataStorage db = DataStorage.getInstance();
        AlertStorage alertStorage = new AlertStorage();
        HeartRateStrategy strategy = new HeartRateStrategy();

        db.addPatientData(1, 100.0, "ECG", 1714376789050L);
        db.addPatientData(1, 120.0, "ECG", 1714376789051L);
        Patient patient = db.getAllPatients().get(0);
        List<PatientRecord> records = db.getRecords(patient.getPatientId(), 0L, Long.MAX_VALUE);

        // Act
        strategy.checkAlert(patient, records, alertStorage);

        // Assert
        assertEquals(0, alertStorage.getAlerts().size());
    }
}
