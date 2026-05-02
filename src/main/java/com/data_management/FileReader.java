package com.data_management;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FileReader implements DataReader{
    Path path;

    public FileReader(String _path){
        this.path = Paths.get(_path);
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            throw new IOException("Invalid input directory: " + path);
        }

        for (Path file : Files.newDirectoryStream(path, "*.txt")) {
            for (String line : Files.readAllLines(file)) {
                if (line.isBlank()) continue;
                parseLine(line, file, dataStorage);
            }
        }
    }

    private void parseLine(String line, Path file, DataStorage dataStorage) {
        try {
            String[] parts = line.split(", ");
            int patientId = Integer.parseInt(parts[0].split(": ")[1]);
            long timestamp = Long.parseLong(parts[1].split(": ")[1]);
            String label = parts[2].split(": ")[1];
            double value = Double.parseDouble(parts[3].split(": ")[1]);
            dataStorage.addPatientData(patientId, value, label, timestamp);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Skipping malformed line in " + file + ": " + line);
        }
    }
    
}
