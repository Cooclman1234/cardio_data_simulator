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
    }
}
