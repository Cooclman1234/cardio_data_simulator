package data_management;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.data_management.DataStorage;
import com.data_management.FileReader;
import com.data_management.PatientRecord;

public class FileReaderTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        DataStorage.resetInstance();
    }

    @Test
    void testInvalidFilePathError() throws IOException {
        // Arrange
        DataStorage storage = DataStorage.getInstance();
        FileReader reader = new FileReader("nonsense");

        // Act / Assert
        assertThrows(IOException.class, () -> reader.readData(storage));
    }

    @Test
    void testFilePathInsteadOfDirectoryThrowsIOException() throws IOException {
        // Arrange
        DataStorage storage = DataStorage.getInstance();
        Path singleFile = Files.createFile(tempDir.resolve("single.txt"));
        FileReader reader = new FileReader(singleFile.toString());

        // Act / Assert
        assertThrows(IOException.class, () -> reader.readData(storage));
    }

    @Test
    void testReadDataLoadsValidRecordsFromTxtFiles() throws IOException {
        // Arrange
        Files.write(tempDir.resolve("VitalsA.txt"), List.of(
                "PatientID: 1, Timestamp: 1715250000000, Label: ECG, Data: 82.5",
                "PatientID: 1, Timestamp: 1715250000001, Label: Saturation, Data: 97.2"));
        Files.write(tempDir.resolve("VitalsB.txt"), List.of(
                "PatientID: 2, Timestamp: 1715250000002, Label: SystolicPressure, Data: 121.0"));

        DataStorage storage = DataStorage.getInstance();
        FileReader reader = new FileReader(tempDir.toString());

        // Act
        reader.readData(storage);

        // Assert
        List<PatientRecord> patientOneRecords = storage.getRecords(1, 1715250000000L, 1715250000010L);
        List<PatientRecord> patientTwoRecords = storage.getRecords(2, 1715250000000L, 1715250000010L);
        assertEquals(2, patientOneRecords.size());
        assertEquals(1, patientTwoRecords.size());
        assertEquals("ECG", patientOneRecords.get(0).getRecordType());
        assertEquals(82.5, patientOneRecords.get(0).getMeasurementValue());
    }

    @Test
    void testReadDataIgnoresMalformedAndBlankLines() throws IOException {
        // Arrange
        Files.write(tempDir.resolve("Mixed.txt"), List.of(
                "",
                "Malformed line with no delimiters",
                "PatientID: X, Timestamp: 1715250000000, Label: ECG, Data: notANumber",
                "PatientID: 3, Timestamp: 1715250000003, Label: ECG, Data: 78.0"));

        DataStorage storage = DataStorage.getInstance();
        FileReader reader = new FileReader(tempDir.toString());

        // Act
        reader.readData(storage);

        // Assert
        List<PatientRecord> patientThreeRecords = storage.getRecords(3, 1715250000000L, 1715250000010L);
        assertEquals(1, patientThreeRecords.size());
        assertEquals(78.0, patientThreeRecords.get(0).getMeasurementValue());
        assertTrue(storage.getRecords(99, 1715250000000L, 1715250000010L).isEmpty());
    }

    @Test
    void testReadDataProcessesOnlyTxtFiles() throws IOException {
        // Arrange
        Files.write(tempDir.resolve("valid.txt"), List.of(
                "PatientID: 4, Timestamp: 1715250000004, Label: DiastolicPressure, Data: 81.0"));
        Files.write(tempDir.resolve("ignored.csv"), List.of(
                "PatientID: 4, Timestamp: 1715250000005, Label: ECG, Data: 75.0"));

        DataStorage storage = DataStorage.getInstance();
        FileReader reader = new FileReader(tempDir.toString());

        // Act
        reader.readData(storage);

        // Assert
        List<PatientRecord> patientFourRecords = storage.getRecords(4, 1715250000000L, 1715250000010L);
        assertEquals(1, patientFourRecords.size());
        assertEquals("DiastolicPressure", patientFourRecords.get(0).getRecordType());
    }
}
