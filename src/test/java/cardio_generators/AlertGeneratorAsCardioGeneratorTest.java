package cardio_generators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.cardio_generator.generators.AlertGenerator;
import com.cardio_generator.outputs.OutputStrategy;

public class AlertGeneratorAsCardioGeneratorTest {
    @Test
    void testValidPatientIdDoesNotThrow() {
        AlertGenerator gen = new AlertGenerator(5);
        OutputStrategy mockOutput = (id, ts, label, data) -> {}; // Lambda-Stub

        assertDoesNotThrow(() -> gen.generate(5, mockOutput));
    }

    @Test
    void testInvalidPatientIdThrowsArrayOutOfBounds() {
        AlertGenerator gen = new AlertGenerator(5);
        OutputStrategy mockOutput = (id, ts, label, data) -> {};

        assertThrows(RuntimeException.class, () -> gen.generate(6, mockOutput));
    }
}
