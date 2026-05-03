package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Outputs patient health data to a connected TCP client.
 */
public class TcpOutputStrategy implements OutputStrategy, AutoCloseable {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private java.util.concurrent.ExecutorService acceptExecutor;

    /**
     * Starts a TCP server on the specified port and accepts a single client
     * connection in a background thread.
     * 
     * @param port the port number where a TCP server should start
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + serverSocket.getLocalPort());

            acceptExecutor = java.util.concurrent.Executors.newSingleThreadExecutor();
            acceptExecutor.submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getLocalPort() {
        return serverSocket != null ? serverSocket.getLocalPort() : -1;
    }

    /**
     * Creates a message containing all important data about the patient in a
     * string.
     * 
     * @param patientId unique patient identifier
     * @param timestamp unique point in time
     * @param label     label for data type
     * @param data      the actual data
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }

    @Override
    public void close() {
        try {
            if (out != null) {
                out.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (acceptExecutor != null) {
                acceptExecutor.shutdownNow();
            }
        }
    }
}
