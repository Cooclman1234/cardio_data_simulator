package cardio_generators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

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
    void testTCPConnectsWithServer() throws Exception {
        // Arrange
        try (TcpOutputStrategy tcp = new TcpOutputStrategy(0)) {
            int port = tcp.getLocalPort();
            try (Socket client = new Socket("localhost", port);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
                client.setSoTimeout(300);

                String received = null;

                // Act + Assert with retry to handle async accept thread race.
                for (int attempt = 0; attempt < 10 && received == null; attempt++) {
                    tcp.output(1, 1000L, "ECG", "0.5");
                    try {
                        received = reader.readLine();
                    } catch (SocketTimeoutException ignored) {
                        // Retry until server-side writer is ready.
                    }
                }

                assertEquals("1,1000,ECG,0.5", received);
            }
        }
    }

}
