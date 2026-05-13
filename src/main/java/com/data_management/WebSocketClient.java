package com.data_management;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.java_websocket.handshake.ServerHandshake;

public class WebSocketClient implements DataReader {

    private final URI serverUri;
    private org.java_websocket.client.WebSocketClient wsClient;

    public WebSocketClient(String url) throws IOException {
        try {
            this.serverUri = new URI(url);
        } catch (URISyntaxException e) {
            throw new IOException("Invalid WebSocket URL: " + url, e);
        }
    }

    /**
     * Connects to the WebSocket server and continuously stores incoming patient
     * data into {@code dataStorage}. This method blocks until {@link #stopListening()}
     * is called or the server closes the connection.
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        CountDownLatch stopSignal = new CountDownLatch(1); // released when connection closes or errors
        AtomicReference<Exception> asyncError = new AtomicReference<>(); // holds any error from the WebSocket thread

        // anonymous subclass wiring up the four WebSocket event callbacks
        // for learning: an anonymous subclass is a class that directly created at the place where it is also instantiated, without giving a name
        wsClient = new org.java_websocket.client.WebSocketClient(serverUri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("Connected to WebSocket source: " + serverUri); // confirm connection
            }

            // this is what creates teh live mechanism. This method is automatically called from the webSoket library, whenever a message from server arrives
            // here you can also modify the way it prints out alerts or any specifc alerts
            @Override
            public void onMessage(String message) {
                System.out.println("Received WebSocket message: " + message); // Print all messages
                if (message.contains(",Alert,")) {
                    System.out.println("[ALERT] " + message); // Highlight alerts
                }
                parseAndStore(message, dataStorage); // parse CSV and write to DataStorage on every message
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("WebSocket connection closed: " + reason);
                stopSignal.countDown(); // wake up await() below so readData() can return
            }

            @Override
            public void onError(Exception ex) {
                asyncError.compareAndSet(null, ex); // store first error only (thread-safe)
                stopSignal.countDown(); // wake up await() so the error can be rethrown
            }
        };

        try {
            wsClient.connectBlocking(); // open connection, waits until onOpen() fires

            // blocks here indefinitely — onMessage() runs live on the WebSocket thread
            // the await() blocks the thread permanently, so onMessage() gets closed
            stopSignal.await(); 

            if (asyncError.get() != null) {
                throw new IOException("WebSocket read failed", asyncError.get()); // rethrow any async error
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore interrupt flag
            throw new IOException("Interrupted while reading WebSocket data", e);
        }
    }

    /**
     * Signals the continuous listening loop to stop and closes the connection.
     */
    public void stopListening() {
        if (wsClient != null) {
            wsClient.close();
        }
    }

    private void parseAndStore(String message, DataStorage dataStorage) {
        try {
            String[] parts = message.split(",", 4); // checks if the incoming String is made out fo the four parts of data: patientId, condition, ...
            if (parts.length != 4) {
                System.err.println("Skipping malformed WebSocket message: " + message); // prints ou an error if that is not the case
                return;
            }

            int patientId = Integer.parseInt(parts[0].trim());
            long timestamp = Long.parseLong(parts[1].trim());
            String label = parts[2].trim();
            String rawValue = parts[3].trim();
            double value;

            if ("Alert".equalsIgnoreCase(label)) {
                if ("triggered".equalsIgnoreCase(rawValue)) {
                    value = 1.0;
                } else if ("resolved".equalsIgnoreCase(rawValue)) {
                    value = 0.0;
                } else {
                    System.err.println("Skipping malformed alert value in WebSocket message: " + message);
                    return;
                }
            } else {
                // Accept values such as "97.0%" from saturation messages.
                String normalizedValue = rawValue.endsWith("%")
                        ? rawValue.substring(0, rawValue.length() - 1).trim()
                        : rawValue;
                value = Double.parseDouble(normalizedValue);
            }

            dataStorage.addPatientData(patientId, value, label, timestamp); // adds teh data into dataStorage
        } catch (NumberFormatException e) {
            System.err.println("Skipping malformed numeric values in WebSocket message: " + message); // putts out an error message when something wrong happens
        }
    }
}
