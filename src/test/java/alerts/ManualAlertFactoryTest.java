package alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.ManualAlertFactory;


public class ManualAlertFactoryTest {
    @Test
    void testCreateAlert() {
        // Arrange
        ManualAlertFactory factory = new ManualAlertFactory();

        // Act
        Alert alert = factory.createAlert("1", "Manual alert triggered", 1714376789050L);

        // Assert
        assertEquals("1", alert.getPatientId());
        assertEquals("Manual alert triggered", alert.getCondition());
        assertEquals(1714376789050L, alert.getTimestamp());
        assertEquals("Manual alert", alert.getType());
    }

    @Test
    void checkCreateAlertType() {
        // Arrange
        ManualAlertFactory factory = new ManualAlertFactory();

        // Act
        Alert alert = factory.createAlert("1", "Manual alert triggered", 1714376789050L);

        //Assert
        assertEquals("Manual alert", alert.getType());
    }

    @Test
    void checkMultipleCreateAlertAlerts() {
        // Arrange
        ManualAlertFactory factory = new ManualAlertFactory();

        // Act
        Alert alert1 = factory.createAlert("1", "Manual alert triggered", 1714376789050L);
        Alert alert2 = factory.createAlert("2", "Manual alert triggered", 1714376789051L);

        // Assert
        assertNotNull(alert1);
        assertNotNull(alert2);
        assertNotSame(alert1, alert2); // zwei verschiedene Objekte

        assertEquals("1", alert1.getPatientId());
        assertEquals("2", alert2.getPatientId());
        assertEquals(1714376789050L, alert1.getTimestamp());
        assertEquals(1714376789051L, alert2.getTimestamp());
    }
}
