package cardio_generators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import com.cardio_generator.generators.AlertGenerator;
import com.cardio_generator.outputs.OutputStrategy;

public class AlertGeneratorAsCardioGeneratorTest {
    @Test
    void testValidPatientIdDoesNotThrow() {
        //Arrange
        AlertGenerator gen = new AlertGenerator(5);
        OutputStrategy mockOutput = (id, ts, label, data) -> {}; // Lambda-Stub

        //Act + Assert
        assertDoesNotThrow(() -> gen.generate(5, mockOutput));
    }

    @Test
    void testInvalidPatientIdThrowsArrayOutOfBounds() {
        //Arrange
        AlertGenerator gen = new AlertGenerator(0);
        OutputStrategy mockOutput = (id, ts, label, data) -> {};

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        //Act
        gen.generate(1, mockOutput);

        //Assert
        System.setErr(originalErr); 
        assertTrue(errContent.toString().contains("An error occurred while generating alert data for patient 1"));


    }
}
