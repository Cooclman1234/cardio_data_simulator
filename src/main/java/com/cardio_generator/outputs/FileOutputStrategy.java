package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

// mistake : this doc has also missing documentation - the only exception
// is when code is easily understandle, which is not the case so therefore
// javaDoc is necessary for class and constructor

/**
 * Writes patient data to label-specific text files in a configured directory.
 */
public class FileOutputStrategy implements OutputStrategy {
    // mistake : variable or a field has to be written in lowerCamelCase, before it started with a capital letter
    private String baseDirectory;
    // mistake : it is not allowed to use _ for variables, only lowerCamelCase
    // mistake : fields have to be private to ensure encapsulation
    private final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    // mistake : there should not be an extra unnecessary free line and javaDoc was forgotten - constructors with parameters need javaDoc
    /**
    * Creates a new FileOutputStrategy.
    *
    * @param baseDirectory the directory where output files will be stored
    */
    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }
    /**
     * Creates a directory, defines a file path and writes one line containing patient data.
     * 
     * @param patientId unique patient identifier
     * @param timestamp unique point in time
     * @param label label for data type
     * @param data the actual data
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the FilePath variable
        // variable, has to start with small letter
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}