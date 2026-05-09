package alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;


import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.ECGAlertFactory;


public class ECGAlertFactoryTest {
    @Test
    void testCreateAlert() {
        // Arrange
        ECGAlertFactory factory = new ECGAlertFactory();

        // Act
        Alert alert = factory.createAlert(1, "Abnormal ECG peak", 1714376789050L);

        // Assert
        assertEquals(1, alert.getPatientId());
        assertEquals("Abnormal ECG peak", alert.getCondition());
        assertEquals(1714376789050L, alert.getTimestamp());
        assertEquals("ECG Alert", alert.getType());
    }

    @Test
    void checkCreateAlertType() {
        // Arrange
        ECGAlertFactory factory = new ECGAlertFactory();

        // Act
        Alert alert = factory.createAlert(1, "Abnormal ECG peak", 1714376789050L);

        //Assert
        assertEquals("ECG Alert", alert.getType());
    }

    @Test
    void checkMultipleCreateAlertAlerts() {
        // Arrange
        ECGAlertFactory factory = new ECGAlertFactory();

        // Act
        Alert alert1 = factory.createAlert(1, "Abnormal ECG peak", 1714376789050L);
        Alert alert2 = factory.createAlert(2, "Abnormal ECG peak", 1714376789051L);

        // Assert
        assertNotNull(alert1);
        assertNotNull(alert2);
        assertNotSame(alert1, alert2); // zwei verschiedene Objekte

        assertEquals(1, alert1.getPatientId());
        assertEquals(2, alert2.getPatientId());
        assertEquals(1714376789050L, alert1.getTimestamp());
        assertEquals(1714376789051L, alert2.getTimestamp());
    }
}