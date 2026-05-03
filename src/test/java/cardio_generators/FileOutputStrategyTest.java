package cardio_generators;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.cardio_generator.outputs.FileOutputStrategy;

public class FileOutputStrategyTest {
    @Test
    void testInvalidBaseDirectoryPrintsError() {
        //Arrange
        String baseDirectory = "src/main/java/com/alerts/Alert.java"; //for learning: this has to be insdie ur workspace
        String value = "1234";
        String label = "alsoNothing";
        FileOutputStrategy fileOutput = new FileOutputStrategy(baseDirectory);

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        //Act
        fileOutput.output(0, 0, label, value);

        // Assert
        System.setErr(originalErr); 
        assertTrue(errContent.toString().contains("Error creating base directory: "));
    }

    //for learning: creates a temporary directory to test the functionality, tehn junit deletes it direclty after
    @TempDir
    Path tempDir;

    @Test
    void testValidBaseDirectory() {
        //Arrange
        String baseDirectory = tempDir.toString(); //for learning: this has to be insdie ur workspace
        String value = "1234";
        String label = "BaseDirecotryWorks";
        FileOutputStrategy fileOutput = new FileOutputStrategy(baseDirectory);

        //Act
        fileOutput.output(0, 0, label, value);

        // Assert
        Path dir = Paths.get(baseDirectory);
        Path outFile = dir.resolve(label + ".txt");
        assertTrue(Files.exists(dir));
        assertTrue(Files.isDirectory(dir));
        assertTrue(Files.exists(outFile));
    }

    @Test
    void testInvalidInputPrintsError() throws Exception {
        // Arrange
        String label = "BadTarget";
        String value = "1234";
        String baseDirectory = tempDir.toString();
        FileOutputStrategy fileOutput = new FileOutputStrategy(baseDirectory);

        Files.createDirectories(tempDir.resolve(label + ".txt"));

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));
        try {
            // Act
            fileOutput.output(1, 123L, label, value);
        } finally {
            // Always restore stderr
            System.setErr(originalErr);
        }

        // Assert
        assertTrue(errContent.toString().contains("Error writing to file"));
}

}
