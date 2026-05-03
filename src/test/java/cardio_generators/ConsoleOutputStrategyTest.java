package cardio_generators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import com.cardio_generator.outputs.ConsoleOutputStrategy;

public class ConsoleOutputStrategyTest {
    @Test
    void testOutputFunctionality() {
        //Arrange
        ConsoleOutputStrategy outputStrategy = new ConsoleOutputStrategy();
        int patientId = 1;
        long timestamp = 123;
        String label = "nothing";
        String data = "nothing";

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        //Act
        outputStrategy.output(patientId, timestamp, label, data);

        //Assert
        String expected = String.format(
            "Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n",
            patientId, timestamp, label, data
        );
        assertEquals(expected, outContent.toString());
    }


}
