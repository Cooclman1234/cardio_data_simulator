package cardio_generators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.cardio_generator.HealthDataSimulator;
import com.cardio_generator.outputs.ConsoleOutputStrategy;
import com.cardio_generator.outputs.FileOutputStrategy;
import com.cardio_generator.outputs.OutputStrategy;

public class HealthDataSimulatorTest {
    
    @TempDir
    Path tempDir;

    private void resetStaticFields() throws Exception {
        // Reset patientCount to default
        Field patientCountField = HealthDataSimulator.class.getDeclaredField("patientCount");
        patientCountField.setAccessible(true);
        patientCountField.set(null, 50);
        
        // Reset outputStrategy to default
        Field outputStrategyField = HealthDataSimulator.class.getDeclaredField("outputStrategy");
        outputStrategyField.setAccessible(true);
        outputStrategyField.set(null, new ConsoleOutputStrategy());
    }

    @BeforeEach
    void setUp() throws Exception {
        resetStaticFields();
    }

    // Test parseArguments with default values (no args)
    @Test
    void testParseArgumentsDefaultValues() throws Exception {
        // Arrange
        Method parseMethod = HealthDataSimulator.class.getDeclaredMethod("parseArguments", String[].class);
        parseMethod.setAccessible(true);
        String[] args = {};

        Field patientCountField = HealthDataSimulator.class.getDeclaredField("patientCount");
        patientCountField.setAccessible(true);

        // Act
        parseMethod.invoke(null, (Object) args);

        // Assert
        assertEquals(50, patientCountField.get(null)); // Default value
    }

    @Test
    void testParseArgumentsPatientCount() throws Exception {
        // Arrange
        Method parseMethod = HealthDataSimulator.class.getDeclaredMethod("parseArguments", String[].class);
        parseMethod.setAccessible(true);
        String[] args = {"--patient-count", "100"};

        Field patientCountField = HealthDataSimulator.class.getDeclaredField("patientCount");
        patientCountField.setAccessible(true);

        // Act
        parseMethod.invoke(null, (Object) args);

        // Assert
        assertEquals(100, patientCountField.get(null));
    }

    @Test
    void testParseArgumentsPatientCountInvalid() throws Exception {
        // Arrange
        Method parseMethod = HealthDataSimulator.class.getDeclaredMethod("parseArguments", String[].class);
        parseMethod.setAccessible(true);
        String[] args = {"--patient-count", "notanumber"};

        Field patientCountField = HealthDataSimulator.class.getDeclaredField("patientCount");
        patientCountField.setAccessible(true);

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        // Act
        parseMethod.invoke(null, (Object) args);

        // Assert
        System.setErr(originalErr);
        assertEquals(50, patientCountField.get(null)); // Should remain default
        assertTrue(errContent.toString().contains("Invalid number of patients"));
    }

    @Test
    void testParseArgumentsOutputConsole() throws Exception {
        // Arrange
        Method parseMethod = HealthDataSimulator.class.getDeclaredMethod("parseArguments", String[].class);
        parseMethod.setAccessible(true);
        String[] args = {"--output", "console"};

        Field outputStrategyField = HealthDataSimulator.class.getDeclaredField("outputStrategy");
        outputStrategyField.setAccessible(true);

        // Act
        parseMethod.invoke(null, (Object) args);

        // Assert
        OutputStrategy strategy = (OutputStrategy) outputStrategyField.get(null);
        assertTrue(strategy instanceof ConsoleOutputStrategy);
    }

    @Test
    void testParseArgumentsOutputFile() throws Exception {
        // Arrange
        Method parseMethod = HealthDataSimulator.class.getDeclaredMethod("parseArguments", String[].class);
        parseMethod.setAccessible(true);
        String baseDir = tempDir.toString();
        String[] args = {"--output", "file:" + baseDir};

        Field outputStrategyField = HealthDataSimulator.class.getDeclaredField("outputStrategy");
        outputStrategyField.setAccessible(true);

        // Act
        parseMethod.invoke(null, (Object) args);

        // Assert
        OutputStrategy strategy = (OutputStrategy) outputStrategyField.get(null);
        assertTrue(strategy instanceof FileOutputStrategy);
        assertTrue(Files.exists(tempDir)); // Directory should be created
    }

    @Test
    void testParseArgumentsOutputWebSocket() throws Exception {
        // Arrange
        Method parseMethod = HealthDataSimulator.class.getDeclaredMethod("parseArguments", String[].class);
        parseMethod.setAccessible(true);
        String[] args = {"--output", "websocket:8765"};

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            // Act
            parseMethod.invoke(null, (Object) args);

            // Assert
            System.setOut(originalOut);
            assertTrue(outContent.toString().contains("WebSocket output will be on port: 8765"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testParseArgumentsOutputWebSocketInvalid() throws Exception {
        // Arrange
        Method parseMethod = HealthDataSimulator.class.getDeclaredMethod("parseArguments", String[].class);
        parseMethod.setAccessible(true);
        String[] args = {"--output", "websocket:invalid"};

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        // Act
        parseMethod.invoke(null, (Object) args);

        // Assert
        System.setErr(originalErr);
        assertTrue(errContent.toString().contains("Invalid port for WebSocket"));
    }

    @Test
    void testParseArgumentsOutputTcp() throws Exception {
        // Arrange
        Method parseMethod = HealthDataSimulator.class.getDeclaredMethod("parseArguments", String[].class);
        parseMethod.setAccessible(true);
        String[] args = {"--output", "tcp:9876"};

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            // Act
            parseMethod.invoke(null, (Object) args);

            // Assert
            System.setOut(originalOut);
            assertTrue(outContent.toString().contains("TCP socket output will be on port: 9876"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testParseArgumentsOutputTcpInvalid() throws Exception {
        // Arrange
        Method parseMethod = HealthDataSimulator.class.getDeclaredMethod("parseArguments", String[].class);
        parseMethod.setAccessible(true);
        String[] args = {"--output", "tcp:notaport"};

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        // Act
        parseMethod.invoke(null, (Object) args);

        // Assert
        System.setErr(originalErr);
        assertTrue(errContent.toString().contains("Invalid port for TCP"));
    }

    @Test
    void testInitializePatientIds() throws Exception {
        // Arrange
        Method method = HealthDataSimulator.class.getDeclaredMethod("initializePatientIds", int.class);
        method.setAccessible(true);

        // Act
        @SuppressWarnings("unchecked")
        List<Integer> result = (List<Integer>) method.invoke(null, 5);

        // Assert
        assertEquals(5, result.size());
        assertEquals(1, result.get(0));
        assertEquals(5, result.get(4));
    }

    @Test
    void testInitializePatientIdsContainsNoDuplicates() throws Exception {
        // Arrange
        Method method = HealthDataSimulator.class.getDeclaredMethod("initializePatientIds", int.class);
        method.setAccessible(true);

        // Act
        @SuppressWarnings("unchecked")
        List<Integer> result = (List<Integer>) method.invoke(null, 100);

        // Assert
        assertEquals(100, result.size());
        assertEquals(100, result.stream().distinct().count()); // No duplicates
    }

    
}