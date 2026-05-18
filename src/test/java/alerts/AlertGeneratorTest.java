package alerts;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.AlertStorage;
import com.data_management.DataStorage;
import com.data_management.Patient;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AlertGeneratorTest {

    // for learning: beause of the BeforeEach tag, before evey test, setUp() is automatically run
    // for learning: tests run in a seperate JVM production, so the in production JVM has its own global DataStorage instance, whihc wont be touched
    @BeforeEach
    void setUp() {
        DataStorage.resetInstance();
    }

    @Test
    void testEvaluateDataWithNullPatientDoesNothing() {
        // Arrange
        DataStorage db = DataStorage.getInstance();
        AlertStorage alertStorage = new AlertStorage();
        AlertGenerator alertGenerator = new AlertGenerator(db, alertStorage);

        // Act
        alertGenerator.evaluateData(null);

        // Assert
        assertEquals(0, alertStorage.getAlerts().size());
    }

    @Test
    void testHypotensiveHypoxemiaAlert() {
        // Arrange
        DataStorage db = DataStorage.getInstance();
        AlertStorage alertStorage = new AlertStorage();
        AlertGenerator alertGenerator = new AlertGenerator(db, alertStorage);

        db.addPatientData("1", 89.0, "Systolic Pressure", 1714376789050L);
        db.addPatientData("1", 88, "Saturation", 1714376789050L); 
        Patient patient = db.getAllPatients().get(0);

        // Act
        alertGenerator.evaluateData(patient);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(3, alerts.size());
        assertEquals("Hypotensive Hypoxemia Alert", alerts.get(2).getCondition());
    }

    @Test
    void testECGAlertTriggersForAbnormalPeak() {
        // Arrange
        DataStorage db = DataStorage.getInstance();
        AlertStorage alertStorage = new AlertStorage();
        AlertGenerator alertGenerator = new AlertGenerator(db, alertStorage);

        db.addPatientData("1", 100.0, "ECG", 1714376789050L);
        db.addPatientData("1", 200.0, "ECG", 1714376789051L);
        Patient patient = db.getAllPatients().get(0);

        // Act
        alertGenerator.evaluateData(patient);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Abnormal ECG peak", alerts.get(0).getCondition());
    }
    
}
