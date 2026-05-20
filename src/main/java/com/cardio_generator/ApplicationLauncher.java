package com.cardio_generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.data_management.DataStorage;

/**
 * Central entry point for running the project in different modes.
 *
 * <p>The launcher supports selecting one of the available application modes
 * via {@code --mode}:
 * <ul>
 *   <li>{@code simulator} - starts {@link HealthDataSimulator}</li>
 *   <li>{@code websocket} - starts {@link WebSocketSimulatorLauncher}</li>
 *   <li>{@code storage} - starts {@link DataStorage}</li>
 * </ul>
 */
public class ApplicationLauncher {

    /** Default execution mode when {@code --mode} is not provided. */
    private static final String DEFAULT_MODE = "simulator";

    /**
     * Starts the application in the selected mode.
     *
     * @param args command-line arguments for launcher configuration and forwarding
     * @throws IOException if an I/O error occurs in the selected mode
     * @throws InterruptedException if execution is interrupted in the selected mode
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        String mode = DEFAULT_MODE;
        List<String> forwardedArgs = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if ("--mode".equals(args[i]) && i + 1 < args.length) {
                mode = args[++i].toLowerCase();
            } else if ("-h".equals(args[i]) || "--help".equals(args[i])) {
                printHelp();
                return;
            } else {
                forwardedArgs.add(args[i]);
            }
        }

        String[] targetArgs = forwardedArgs.toArray(new String[0]);

        switch (mode) {
            case "simulator":
                HealthDataSimulator.main(targetArgs);
                break;
            case "websocket":
                WebSocketSimulatorLauncher.main(targetArgs);
                break;
            case "storage":
                DataStorage.main(targetArgs);
                break;
            default:
                System.err.println("Unknown mode: " + mode);
                printHelp();
                System.exit(1);
        }
    }

    /**
     * Prints usage and mode information for the launcher.
     */
    private static void printHelp() {
        System.out.println("Usage: java -cp target/classes com.cardio_generator.ApplicationLauncher --mode <simulator|websocket|storage> [options]");
        System.out.println("Modes:");
        System.out.println(" --> simulator : Runs HealthDataSimulator (default mode)");
        System.out.println(" --> websocket : Runs WebSocketSimulatorLauncher");
        System.out.println(" --> storage   : Runs DataStorage");

    }
}
