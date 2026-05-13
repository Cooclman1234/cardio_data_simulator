package com.cardio_generator;

import com.data_management.DataStorage;
import com.data_management.WebSocketClient;
import java.io.IOException;

/**
 * Launcher that runs both the HealthDataSimulator (with WebSocket output)
 * and the WebSocketClient (to listen and process the data) concurrently.
 */
public class WebSocketSimulatorLauncher {

    private static final int DEFAULT_PORT = 8080; // set the port specifically to 8080, so this is also expected when running HealthDataSimulator
    private static final int DEFAULT_PATIENT_COUNT = 50;

    public static void main(String[] args) throws IOException, InterruptedException {
        int portTemp = DEFAULT_PORT;
        int patientCountTemp = DEFAULT_PATIENT_COUNT;

        // Parse arguments if provided
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--port") && i + 1 < args.length) {
                try {
                    portTemp = Integer.parseInt(args[++i]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid port. Using default: " + DEFAULT_PORT);
                    portTemp = DEFAULT_PORT;
                }
            } else if (args[i].equals("--patient-count") && i + 1 < args.length) {
                try {
                    patientCountTemp = Integer.parseInt(args[++i]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid patient count. Using default: " + DEFAULT_PATIENT_COUNT);
                    patientCountTemp = DEFAULT_PATIENT_COUNT;
                }
            }
        }

        final int port = portTemp;
        final int patientCount = patientCountTemp;

        System.out.println("Starting WebSocket Simulator Launcher...");
        System.out.println("Port: " + port);
        System.out.println("Patient Count: " + patientCount);
        System.out.println();

        // Start the server in a separate thread
        Thread serverThread = new Thread(() -> {
            try {
                String[] serverArgs = { "--patient-count", String.valueOf(patientCount), "--output", "websocket:" + port};
                HealthDataSimulator.main(serverArgs);
            } catch (IOException e) {
                System.err.println("Server error: " + e.getMessage());
                e.printStackTrace();
            }
        });
        serverThread.setName("HealthDataSimulator-Thread");
        serverThread.start();

        // Give the server a moment to start
        Thread.sleep(2000);

        // Start the client in a separate thread
        Thread clientThread = new Thread(() -> {
            try {
                String url = "ws://localhost:" + port;
                System.out.println("Client connecting to: " + url);
                
                WebSocketClient client = new WebSocketClient(url);
                DataStorage dataStorage = DataStorage.getInstance();
                
                // This will block until the connection is closed
                client.readData(dataStorage);
            } catch (IOException e) {
                System.err.println("Client error: " + e.getMessage());
                e.printStackTrace();
            }
        });
        clientThread.setName("WebSocketClient-Thread");
        clientThread.start();

        // Keep the main thread alive
        serverThread.join();
        clientThread.join();

        System.out.println("WebSocket Simulator Launcher finished.");
    }
}
