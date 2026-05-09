package cardio_generators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.cardio_generator.generators.BloodLevelsDataGenerator;
import com.cardio_generator.outputs.OutputStrategy;


public class BloodLevelsDataGeneratorsTest {
    @Test
    void testValidPatientCountDoesNotThrow() {
        //Arrange
        BloodLevelsDataGenerator gen = new BloodLevelsDataGenerator(5);
        OutputStrategy mockOutput = (id, ts, label, data) -> {}; // Lambda-Stub

        // Act and Assert
        assertDoesNotThrow(() -> gen.generate(5, mockOutput));
    }

    @Test
    void testPatientCountZeroDoesNotThrow() {
        //Arrange
        BloodLevelsDataGenerator gen = new BloodLevelsDataGenerator(0);
        OutputStrategy mockOutput = (id, ts, label, data) -> {}; // Lambda-Stub

        // Act and Assert
        assertDoesNotThrow(() -> gen.generate(3, mockOutput));
    }

    @Test
    void testInvalidPatientIdPrintsError() {
        // Arrange
        BloodLevelsDataGenerator gen = new BloodLevelsDataGenerator(0);
        OutputStrategy mockOutput = (id, ts, label, data) -> {};

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        // Act
        gen.generate(1, mockOutput); 

        // Assert
        System.setErr(originalErr); 
        assertTrue(errContent.toString().contains("An error occurred while generating blood levels data for patient 1"));
    }

    @Test
    void testValidPatientIdOutputsValues() {
        // Arrange
        BloodLevelsDataGenerator gen = new BloodLevelsDataGenerator(1);
        List<String> labels = new ArrayList<>();
        List<String> values = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        OutputStrategy mockOutput = (id, ts, label, data) -> {
            ids.add(id);
            values.add(data);
            labels.add(label);
        };

        //Act
        gen.generate(1, mockOutput);

        //Assert
        assertEquals(3,labels.size());
        assertTrue(labels.contains("Cholesterol"));
        assertTrue(labels.contains("WhiteBloodCells"));
        assertTrue(labels.contains("RedBloodCells"));
        assertTrue(values.size() == 3);
        assertTrue(ids.size() == 3);
        assertEquals(1, ids.get(0));        
    }

    @Test
    void testInvalidPatientIdThrowsError() {
        // Arrange
        BloodLevelsDataGenerator gen = new BloodLevelsDataGenerator(2);
        OutputStrategy mockOutput = (id, ts, label, data) -> {};

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        //Act
        gen.generate(3, mockOutput);

        // Assert
        System.setErr(originalErr); 
        assertTrue(errContent.toString().contains("An error occurred while generating blood levels data for patient 3"));

    }
}
