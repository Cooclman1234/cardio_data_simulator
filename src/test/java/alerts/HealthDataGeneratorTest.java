package alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.HealthDataGenerator;
import com.data_management.AlertStorage;
import com.data_management.DataStorage;
import com.data_management.Patient;

public class HealthDataGeneratorTest {
    @Test
    void testTriggerAlert() {
        // Arrange
        DataStorage db = new DataStorage();
        AlertStorage alertStorage = new AlertStorage();
        HealthDataGenerator alertGenerator = new HealthDataGenerator(alertStorage);

        db.addPatientData(1, 175.0, "Systolic Pressure", 1714376789050L); // normal
        Patient patient = db.getAllPatients().get(0);

        // Act
        alertGenerator.triggerAlert(String.valueOf(patient.getPatientId()));

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Manual alert triggered", alerts.get(0).getCondition());
    }

    @Test
    void testUntriggerAlert() {
        // Arrange
        DataStorage db = new DataStorage();
        AlertStorage alertStorage = new AlertStorage();
        HealthDataGenerator alertGenerator = new HealthDataGenerator(alertStorage);

        db.addPatientData(1, 175.0, "Systolic Pressure", 1714376789050L); // normal
        Patient patient = db.getAllPatients().get(0);

        // Act
        alertGenerator.triggerAlert(String.valueOf(patient.getPatientId()));
        List<Alert> list = alertStorage.getAlerts();
        alertGenerator.untriggerAlert(String.valueOf(patient.getPatientId()), list.get(0));

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(0, alerts.size());
        assertEquals(false, alertGenerator.isTriggered(String.valueOf(patient.getPatientId())));
        assertEquals(null, alertGenerator.getActiveAlert(String.valueOf(patient.getPatientId())));
    }

    @Test
    void testIsTriggeredAndGetActiveAlert() {
        // Arrange
        DataStorage db = new DataStorage();
        AlertStorage alertStorage = new AlertStorage();
        HealthDataGenerator alertGenerator = new HealthDataGenerator(alertStorage);
        db.addPatientData(1, 175.0, "Systolic Pressure", 1714376789050L);
        Patient patient = db.getAllPatients().get(0);
        String patientId = String.valueOf(patient.getPatientId());

        // Assert: not triggered before
        assertEquals(false, alertGenerator.isTriggered(patientId));
        assertEquals(null, alertGenerator.getActiveAlert(patientId));

        // Act
        Alert triggered = alertGenerator.triggerAlert(patientId);

        // Assert: triggered after
        assertEquals(true, alertGenerator.isTriggered(patientId));
        assertEquals(triggered, alertGenerator.getActiveAlert(patientId));
    }

    @Test
    void testUntriggerWithoutPriorTriggerDoesNothing() {
        // Arrange
        AlertStorage alertStorage = new AlertStorage();
        HealthDataGenerator alertGenerator = new HealthDataGenerator(alertStorage);
        Alert fakeAlert = new Alert("99", "Manual alert triggered", System.currentTimeMillis(), "triggered alert");

        // Act: untrigger on patient that was never triggered
        alertGenerator.untriggerAlert("99", fakeAlert);

        // Assert: storage unchanged, no active alert
        assertEquals(0, alertStorage.getAlerts().size());
        assertEquals(false, alertGenerator.isTriggered("99"));
    }
}
