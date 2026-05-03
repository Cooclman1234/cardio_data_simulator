package cardio_generators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.cardio_generator.generators.ECGDataGenerator;
import com.cardio_generator.outputs.OutputStrategy;

public class ECGDataGeneratorTest {
    @Test
    void testValidPatientCountDoesNotThrow() {
        //Arrange
        ECGDataGenerator gen = new ECGDataGenerator(5);
        OutputStrategy mockOutput = (id, ts, label, data) -> {}; // Lambda-Stub

        // Act and Assert
        assertDoesNotThrow(() -> gen.generate(5, mockOutput));
    }

    @Test
    void testPatientCountZeroDoesNotThrow() {
        //Arrange
        ECGDataGenerator gen = new ECGDataGenerator(0);
        OutputStrategy mockOutput = (id, ts, label, data) -> {}; // Lambda-Stub

        // Act and Assert
        assertDoesNotThrow(() -> gen.generate(3, mockOutput));
    }

    @Test
    void testInvalidPatientIdPrintsError() {
        // Arrange
        ECGDataGenerator gen = new ECGDataGenerator(0);
        OutputStrategy mockOutput = (id, ts, label, data) -> {};

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        // Act
        gen.generate(1, mockOutput); 

        // Assert
        System.setErr(originalErr); 
        assertTrue(errContent.toString().contains("An error occurred while generating ECG data for patient 1"));
    }

    @Test
    void testValidPatientIdOutputsValues() {
        // Arrange
        ECGDataGenerator gen = new ECGDataGenerator(1);
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
        assertEquals(1,labels.size());
        assertTrue(labels.contains("ECG")); //for learning: either one of them because patientCount = 1
        assertTrue(values.size() == 1);
        assertTrue(ids.size() == 1); //for learning: two id values but same id...
        assertEquals(1, ids.get(0));        
    }

    @Test
    void testInvalidPatientIdThrowsError() {
        // Arrange
        ECGDataGenerator gen = new ECGDataGenerator(2);
        OutputStrategy mockOutput = (id, ts, label, data) -> {};

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        //Act
        gen.generate(3, mockOutput);

        // Assert
        System.setErr(originalErr); 
        assertTrue(errContent.toString().contains("An error occurred while generating ECG data for patient 3"));

    }
}
