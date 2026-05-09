package alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.AlertDecorator;
import com.alerts.ManualAlert;
import com.alerts.PriorityAlertDecorator;
import com.alerts.RepeatedAlertDecorator;

public class AlertDecoratorTest {

    @Test
    void testBaseDecoratorDelegatesAllFields() {
        // Arrange
        Alert baseAlert = new ManualAlert(7, "Manual alert triggered", 1714376789050L);
        AlertDecorator decorator = new AlertDecorator(baseAlert);

        // Act
        int patientId = decorator.getPatientId();
        String condition = decorator.getCondition();
        long timestamp = decorator.getTimestamp();
        String type = decorator.getType();

        // Assert
        assertEquals(7, patientId);
        assertEquals("Manual alert triggered", condition);
        assertEquals(1714376789050L, timestamp);
        assertEquals("Manual alert", type);
    }

    @Test
    void testPriorityDecoratorAddsPriorityToType() {
        // Arrange
        Alert baseAlert = new ManualAlert(8, "Manual alert triggered", 1714376789051L);
        PriorityAlertDecorator decorator = new PriorityAlertDecorator(baseAlert, "high");

        // Act
        String type = decorator.getType();
        String priority = decorator.getPriority();

        // Assert
        assertEquals("HIGH", priority);
        assertEquals("Manual alert | priority=HIGH", type);
    }

    @Test
    void testRepeatedDecoratorAddsRepeatMetadata() {
        // Arrange
        Alert baseAlert = new ManualAlert(9, "Manual alert triggered", 1714376789052L);
        RepeatedAlertDecorator decorator = new RepeatedAlertDecorator(baseAlert, 3);

        // Act
        String condition = decorator.getCondition();
        String type = decorator.getType();
        int repeatCount = decorator.getRepeatCount();

        // Assert
        assertEquals(3, repeatCount);
        assertEquals("Manual alert triggered (repeated x3)", condition);
        assertEquals("Manual alert | repeated", type);
    }

    @Test
    void testDecoratorsCanBeChained() {
        // Arrange
        Alert baseAlert = new ManualAlert(10, "Manual alert triggered", 1714376789053L);
        Alert chained = new PriorityAlertDecorator(new RepeatedAlertDecorator(baseAlert, 2), "critical");

        // Act
        String condition = chained.getCondition();
        String type = chained.getType();

        // Assert
        assertEquals("Manual alert triggered (repeated x2)", condition);
        assertEquals("Manual alert | repeated | priority=CRITICAL", type);
    }

    @Test
    void testRepeatedDecoratorRejectsInvalidRepeatCount() {
        // Arrange
        Alert baseAlert = new ManualAlert(11, "Manual alert triggered", 1714376789054L);

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> new RepeatedAlertDecorator(baseAlert, 1));
    }

    @Test
    void testPriorityDecoratorRejectsBlankPriority() {
        // Arrange
        Alert baseAlert = new ManualAlert(12, "Manual alert triggered", 1714376789055L);

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> new PriorityAlertDecorator(baseAlert, "  "));
    }
}
