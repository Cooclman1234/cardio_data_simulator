package alerts;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.AlertStorage;
import com.data_management.DataReader;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class AlertGeneratorTest {

    @Test
    void testHighSystolicPressureTriggersAlert() {
        // Arrange
        DataStorage db = new DataStorage();
        AlertStorage alertStorage = new AlertStorage();
        AlertGenerator alertGenerator = new AlertGenerator(db, alertStorage);

        db.addPatientData(1, 185.0, "Systolic Pressure", 1714376789050L); 
        Patient patient = db.getAllPatients().get(0);

        // Act
        alertGenerator.evaluateData(patient);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(1, alerts.size());
        assertEquals("high Systolic Pressure", alerts.get(0).getCondition());
    }

    @Test
    void testLowSystolicPressureTriggersAlert() {
        // Arrange
        DataStorage db = new DataStorage();
        AlertStorage alertStorage = new AlertStorage();
        AlertGenerator alertGenerator = new AlertGenerator(db, alertStorage);

        db.addPatientData(1, 85.0, "Systolic Pressure", 1714376789050L); 
        Patient patient = db.getAllPatients().get(0);

        // Act
        alertGenerator.evaluateData(patient);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(1, alerts.size());
        assertEquals("low Systolic Pressure", alerts.get(0).getCondition());
    }

    @Test
    void testHighSystolicPressureTriggersTrendAlert() {
        // Arrange
        DataStorage db = new DataStorage();
        AlertStorage alertStorage = new AlertStorage();
        AlertGenerator alertGenerator = new AlertGenerator(db, alertStorage);

        db.addPatientData(1, 185.0, "Systolic Pressure", 1714376789050L); 
        db.addPatientData(1, 197.0, "Systolic Pressure", 1714376789051L);
        db.addPatientData(1, 208.0, "Systolic Pressure", 1714376789052L);
        Patient patient = db.getAllPatients().get(0);

        // Act
        alertGenerator.evaluateData(patient);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(4, alerts.size());
        assertEquals("Trend alert - increasing Systolic Pressure", alerts.get(3).getCondition());
    }

    @Test
    void testLowSystolicPressureTriggersTrendAlert() {
        // Arrange
        DataStorage db = new DataStorage();
        AlertStorage alertStorage = new AlertStorage();
        AlertGenerator alertGenerator = new AlertGenerator(db, alertStorage);

        db.addPatientData(1, 89.0, "Systolic Pressure", 1714376789050L); 
        db.addPatientData(1, 78.0, "Systolic Pressure", 1714376789051L);
        db.addPatientData(1, 66.0, "Systolic Pressure", 1714376789052L);
        Patient patient = db.getAllPatients().get(0);

        // Act
        alertGenerator.evaluateData(patient);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(4, alerts.size());
        assertEquals("Trend alert - decreasing Systolic Pressure", alerts.get(3).getCondition());
    }

    @Test
    void testHighDiastolicPressureTriggersAlert() {
        // Arrange
        DataStorage db = new DataStorage();
        AlertStorage alertStorage = new AlertStorage();
        AlertGenerator alertGenerator = new AlertGenerator(db, alertStorage);

        db.addPatientData(1, 125.0, "Diastolic Pressure", 1714376789050L); 
        Patient patient = db.getAllPatients().get(0);

        // Act
        alertGenerator.evaluateData(patient);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(1, alerts.size());
        assertEquals("high Diastolic Pressure", alerts.get(0).getCondition());
    }

    @Test
    void testLowDiastolicPressureTriggersAlert() {
        // Arrange
        DataStorage db = new DataStorage();
        AlertStorage alertStorage = new AlertStorage();
        AlertGenerator alertGenerator = new AlertGenerator(db, alertStorage);

        db.addPatientData(1, 55.0, "Diastolic Pressure", 1714376789050L); 
        Patient patient = db.getAllPatients().get(0);

        // Act
        alertGenerator.evaluateData(patient);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(1, alerts.size());
        assertEquals("low Diastolic Pressure", alerts.get(0).getCondition());
    }

    @Test
    void testHighDiastolicPressureTriggersTrendAlert() {
        // Arrange
        DataStorage db = new DataStorage();
        AlertStorage alertStorage = new AlertStorage();
        AlertGenerator alertGenerator = new AlertGenerator(db, alertStorage);

        db.addPatientData(1, 125.0, "Diastolic Pressure", 1714376789050L); 
        db.addPatientData(1, 137.0, "Diastolic Pressure", 1714376789051L);
        db.addPatientData(1, 148.0, "Diastolic Pressure", 1714376789052L);
        Patient patient = db.getAllPatients().get(0);

        // Act
        alertGenerator.evaluateData(patient);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(4, alerts.size());
        assertEquals("Trend alert - increasing Diastolic Pressure", alerts.get(3).getCondition());
    }

    @Test
    void testLowDiastolicPressureTriggersTrendAlert() {
        // Arrange
        DataStorage db = new DataStorage();
        AlertStorage alertStorage = new AlertStorage();
        AlertGenerator alertGenerator = new AlertGenerator(db, alertStorage);

        db.addPatientData(1, 55.0, "Diastolic Pressure", 1714376789050L); 
        db.addPatientData(1, 44.0, "Diastolic Pressure", 1714376789051L);
        db.addPatientData(1, 32.0, "Diastolic Pressure", 1714376789052L);
        Patient patient = db.getAllPatients().get(0);

        // Act
        alertGenerator.evaluateData(patient);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(4, alerts.size());
        assertEquals("Trend alert - decreasing Diastolic Pressure", alerts.get(3).getCondition());
    }

    @Test
    void testLowSaturation() {
        // Arrange
        DataStorage db = new DataStorage();
        AlertStorage alertStorage = new AlertStorage();
        AlertGenerator alertGenerator = new AlertGenerator(db, alertStorage);

        db.addPatientData(1, 88, "Saturation", 1714376789050L); 
        Patient patient = db.getAllPatients().get(0);

        // Act
        alertGenerator.evaluateData(patient);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(1, alerts.size());
        assertEquals("low Saturation", alerts.get(0).getCondition());
    }

    @Test
    void testRapidDropSaturation() {
        // Arrange
        DataStorage db = new DataStorage();
        AlertStorage alertStorage = new AlertStorage();
        AlertGenerator alertGenerator = new AlertGenerator(db, alertStorage);

        db.addPatientData(1, 90, "Saturation", 1714376789050L); 
        db.addPatientData(1, 80, "Saturation", 1714376789051L);
        Patient patient = db.getAllPatients().get(0);

        // Act
        alertGenerator.evaluateData(patient);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(3, alerts.size());
        assertEquals("Rapid drop in Saturation", alerts.get(2).getCondition());
    }

    @Test
    void testHypotensiveHypoxemiaAlert() {
        // Arrange
        DataStorage db = new DataStorage();
        AlertStorage alertStorage = new AlertStorage();
        AlertGenerator alertGenerator = new AlertGenerator(db, alertStorage);

        db.addPatientData(1, 89.0, "Systolic Pressure", 1714376789050L);
        db.addPatientData(1, 88, "Saturation", 1714376789050L); 
        Patient patient = db.getAllPatients().get(0);

        // Act
        alertGenerator.evaluateData(patient);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(3, alerts.size());
        assertEquals("Hypotensive Hypoxemia Alert", alerts.get(2).getCondition());
    }

    @Test
    void ECGAlert() {
        // Arrange
        DataStorage db = new DataStorage();
        AlertStorage alertStorage = new AlertStorage();
        AlertGenerator alertGenerator = new AlertGenerator(db, alertStorage);

        db.addPatientData(1, 89.0, "Systolic Pressure", 1714376789050L);
        db.addPatientData(1, 88, "Saturation", 1714376789050L); 
        Patient patient = db.getAllPatients().get(0);

        // Act
        alertGenerator.evaluateData(patient);

        // Assert
        List<Alert> alerts = alertStorage.getAlerts();
        assertEquals(3, alerts.size());
        assertEquals("Hypotensive Hypoxemia Alert", alerts.get(2).getCondition());
    }
    
}
