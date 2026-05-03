package cardio_generators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.Test;

import com.cardio_generator.outputs.WebSocketOutputStrategy;

public class WebSocketOutputStrategyTest {

    @Test
    void testServerStartsWithoutException() {
        // Arrange + Act + Assert
        assertDoesNotThrow(() -> {
            WebSocketOutputStrategy strategy = new WebSocketOutputStrategy(8765);
            strategy.output(1, 1000L, "ECG", "0.5"); // kein Client verbunden → kein Crash
        });
    }

    @Test
    void testOutputWithNoClientsDoesNotThrow() {
        // Arrange
        WebSocketOutputStrategy strategy = new WebSocketOutputStrategy(8766);

        // Act + Assert
        assertDoesNotThrow(() -> strategy.output(2, 2000L, "Saturation", "98.0"));
    }

    @Test
    void testOutputMessageReceivedByClient() throws Exception {
        // Arrange
        int port = 8767;
        WebSocketOutputStrategy strategy = new WebSocketOutputStrategy(port);
        Thread.sleep(300); 

        List<String> received = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        WebSocketClient client = new WebSocketClient(new URI("ws://localhost:" + port)) {
            @Override public void onOpen(ServerHandshake h) {}
            @Override public void onMessage(String message) {
                received.add(message);
                latch.countDown();
            }
            @Override public void onClose(int c, String r, boolean remote) {}
            @Override public void onError(Exception e) {}
        };
        client.connect();
        Thread.sleep(200);

        // Act
        strategy.output(1, 1000L, "ECG", "0.5");

        // Assert
        latch.await(2, TimeUnit.SECONDS); 
        assertEquals("1,1000,ECG,0.5", received.get(0));

        client.close();
    }
}