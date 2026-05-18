package alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;


import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.BloodOxygenAlertFactory;


public class BloodOxygenAlertFactoryTest {
    @Test
    void testCreateAlert() {
        // Arrange
        BloodOxygenAlertFactory factory = new BloodOxygenAlertFactory();

        // Act
        Alert alert = factory.createAlert("1", "low/high Blood Oxygen", 1714376789050L);

        // Assert
        assertEquals("1", alert.getPatientId());
        assertEquals("low/high Blood Oxygen", alert.getCondition());
        assertEquals(1714376789050L, alert.getTimestamp());
        assertEquals("Blood Oxygen", alert.getType());
    }

    @Test
    void checkCreateAlertType() {
        // Arrange
        BloodOxygenAlertFactory factory = new BloodOxygenAlertFactory();

        // Act
        Alert alert = factory.createAlert("1", "low/high Blood Oxygen", 1714376789050L);

        //Assert
        assertEquals("Blood Oxygen", alert.getType());
    }

    @Test
    void checkMultipleCreateAlertAlerts() {
        // Arrange
        BloodOxygenAlertFactory factory = new BloodOxygenAlertFactory();

        // Act
        Alert alert1 = factory.createAlert("1", "low/high Blood Oxygen", 1714376789050L);
        Alert alert2 = factory.createAlert("2", "low/high Blood Oxygen", 1714376789051L);

        // Assert
        assertNotNull(alert1);
        assertNotNull(alert2);
        assertNotSame(alert1, alert2); 

        assertEquals("1", alert1.getPatientId());
        assertEquals("2", alert2.getPatientId());
        assertEquals(1714376789050L, alert1.getTimestamp());
        assertEquals(1714376789051L, alert2.getTimestamp());
    }
}