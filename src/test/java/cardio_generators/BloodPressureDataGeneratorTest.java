package cardio_generators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.cardio_generator.generators.BloodPressureDataGenerator;
import com.cardio_generator.outputs.OutputStrategy;

public class BloodPressureDataGeneratorTest {
    @Test
    void testValidPatientCountDoesNotThrow() {
        //Arrange
        BloodPressureDataGenerator gen = new BloodPressureDataGenerator(5);
        OutputStrategy mockOutput = (id, ts, label, data) -> {}; // Lambda-Stub

        // Act and Assert
        assertDoesNotThrow(() -> gen.generate(5, mockOutput));
    }

    @Test
    void testPatientCountZeroDoesNotThrow() {
        //Arrange
        BloodPressureDataGenerator gen = new BloodPressureDataGenerator(0);
        OutputStrategy mockOutput = (id, ts, label, data) -> {}; // Lambda-Stub

        // Act and Assert
        assertDoesNotThrow(() -> gen.generate(3, mockOutput));
    }

    @Test
    void testInvalidPatientIdPrintsError() {
        // Arrange
        BloodPressureDataGenerator gen = new BloodPressureDataGenerator(0);
        OutputStrategy mockOutput = (id, ts, label, data) -> {};

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        // Act
        gen.generate(1, mockOutput); 

        // Assert
        System.setErr(originalErr); 
        assertTrue(errContent.toString().contains("An error occurred while generating blood pressure data for patient 1"));
    }

    @Test
    void testValidPatientIdOutputsValues() {
        // Arrange
        BloodPressureDataGenerator gen = new BloodPressureDataGenerator(1);
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
        assertEquals(2,labels.size());
        assertTrue(labels.contains("Systolic Pressure")); //for learning: either one of them because patientCount = 1
        assertTrue(labels.contains("Diastolic Pressure"));
        assertTrue(values.size() == 2);
        assertTrue(ids.size() == 2); //for learning: two id values but same id...
        assertEquals(1, ids.get(0));        
    }

    @Test
    void testInvalidPatientIdThrowsError() {
        // Arrange
        BloodPressureDataGenerator gen = new BloodPressureDataGenerator(2);
        OutputStrategy mockOutput = (id, ts, label, data) -> {};

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        //Act
        gen.generate(3, mockOutput);

        // Assert
        System.setErr(originalErr); 
        assertTrue(errContent.toString().contains("An error occurred while generating blood pressure data for patient 3"));

    }

}
