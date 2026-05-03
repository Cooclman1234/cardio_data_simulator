package cardio_generators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


import org.junit.jupiter.api.Test;

import com.cardio_generator.outputs.TcpOutputStrategy;

public class TCPOutputStrategyTest {
    @Test
    void testPortConnectionFunctionality() throws IOException {
        int port = 0; // 0 = OS sucht freien Port
        try (ServerSocket server = new ServerSocket(port)) {
            assertTrue(server.getLocalPort() > 0);
        }
    }

    @Test
    void testTCPConnectsWithServer() throws IOException{
        // Arrange
        int port = 9876;
        TcpOutputStrategy tcp = new TcpOutputStrategy(port);

        Socket client = new Socket("localhost", port);
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(client.getInputStream())
        );

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Act
        tcp.output(1, 1000L, "ECG", "0.5");

        // Assert
        String received = reader.readLine();
        assertEquals("1,1000,ECG,0.5", received);

        client.close();
    }
        
}
