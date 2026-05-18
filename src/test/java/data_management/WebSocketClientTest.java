package data_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import com.data_management.WebSocketClient;

class WebSocketClientTest {

    private final List<TestWebSocketServer> startedServers = new ArrayList<>();

    @BeforeEach
    void setUp() {
        DataStorage.resetInstance();
    }

    @AfterEach
    void tearDown() throws Exception {
        for (TestWebSocketServer server : startedServers) {
            server.stopSafely();
        }
        startedServers.clear();
        DataStorage.resetInstance();
    }

    @Test
    void constructorShouldThrowForInvalidUrl() {
        // Arrange / Act / Assert
        assertThrows(IOException.class, () -> new WebSocketClient("ws:// bad"));
    }

    @Test
    @Timeout(5)
    void readDataShouldStoreValidMessage() throws Exception {
        // Arrange
        TestWebSocketServer server = startServer(List.of("1,1715250000000,ECG,82.5"), true);
        DataStorage storage = DataStorage.getInstance();
        WebSocketClient client = new WebSocketClient(server.getWsUrl());

        // Act
        storage.load(client);

        // Assert
        List<PatientRecord> records = storage.getRecords("1", 1715250000000L, 1715250000000L);
        assertEquals(1, records.size());
        assertEquals("1", records.get(0).getPatientId());
        assertEquals("ECG", records.get(0).getRecordType());
        assertEquals(82.5, records.get(0).getMeasurementValue());
    }

    @Test
    @Timeout(5)
    void readDataShouldIgnoreMalformedMessage() throws Exception {
        // Arrange
        TestWebSocketServer server = startServer(List.of("1,1715250000000,ECG"), true);
        DataStorage storage = DataStorage.getInstance();
        WebSocketClient client = new WebSocketClient(server.getWsUrl());

        // Act
        storage.load(client);

        // Assert
        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    @Timeout(5)
    void readDataShouldIgnoreMalformedNumericValues() throws Exception {
        // Arrange
        TestWebSocketServer server = startServer(List.of("abc,1715250000000,ECG,xyz"), true);
        DataStorage storage = DataStorage.getInstance();
        WebSocketClient client = new WebSocketClient(server.getWsUrl());

        // Act
        storage.load(client);

        // Assert
        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    @Timeout(5)
    void readDataShouldStoreMultipleIncomingMessages() throws Exception {
        // Arrange
        List<String> messages = List.of(
                "1,1715250000000,ECG,80.0",
                "1,1715250000001,Saturation,98.0",
                "2,1715250000002,ECG,77.0");

        TestWebSocketServer server = startServer(messages, true);
        DataStorage storage = DataStorage.getInstance();
        WebSocketClient client = new WebSocketClient(server.getWsUrl());

        // Act
        storage.load(client);

        // Assert
        assertEquals(2, storage.getRecords("1", 1715250000000L, 1715250000002L).size());
        assertEquals(1, storage.getRecords("2", 1715250000000L, 1715250000002L).size());
    }

    @Test
    @Timeout(5)
    void readDataShouldStoreAlertStateAsNumericValue() throws Exception {
        // Arrange
        TestWebSocketServer server = startServer(List.of("1,1715250000003,Alert,triggered"), true);
        DataStorage storage = DataStorage.getInstance();
        WebSocketClient client = new WebSocketClient(server.getWsUrl());

        // Act
        storage.load(client);

        // Assert
        List<PatientRecord> records = storage.getRecords("1", 1715250000003L, 1715250000003L);
        assertEquals(1, records.size());
        assertEquals("Alert", records.get(0).getRecordType());
        assertEquals(1.0, records.get(0).getMeasurementValue());
    }

    @Test
    @Timeout(5)
    void readDataShouldParsePercentageFormattedValue() throws Exception {
        // Arrange
        TestWebSocketServer server = startServer(List.of("1,1715250000004,Saturation,97.0"), true);
        DataStorage storage = DataStorage.getInstance();
        WebSocketClient client = new WebSocketClient(server.getWsUrl());

        // Act
        storage.load(client);

        // Assert
        List<PatientRecord> records = storage.getRecords("1", 1715250000004L, 1715250000004L);
        assertEquals(1, records.size());
        assertEquals("Saturation", records.get(0).getRecordType());
        assertEquals(97.0, records.get(0).getMeasurementValue());
    }

    @Test
    @Timeout(5)
    void stopListeningShouldUnblockReadData() throws Exception {
        // Arrange
        TestWebSocketServer server = startServer(List.of(), false);
        DataStorage storage = DataStorage.getInstance();
        WebSocketClient client = new WebSocketClient(server.getWsUrl());
        AtomicReference<Throwable> threadError = new AtomicReference<>();

        Thread readerThread = new Thread(() -> {
            try {
                storage.load(client);
            } catch (Throwable t) {
                threadError.set(t);
            }
        });

        // Act
        readerThread.start();
        assertTrue(server.awaitClientConnected(2, TimeUnit.SECONDS));
        client.stopListening();
        readerThread.join(2000);

        // Assert
        assertFalse(readerThread.isAlive());
        assertEquals(null, threadError.get());
    }

    @Test
    @Timeout(5)
    void readDataShouldThrowWhenConnectionFails() {
        // Arrange / Act / Assert
        IOException ex = assertThrows(IOException.class,
                () -> DataStorage.getInstance().load(new WebSocketClient("ws://127.0.0.1:1")));
        assertNotNull(ex.getCause());
    }

    private TestWebSocketServer startServer(List<String> messagesOnOpen, boolean closeAfterSend) throws Exception {
        TestWebSocketServer server = new TestWebSocketServer(messagesOnOpen, closeAfterSend);
        server.start();
        assertTrue(server.awaitStarted(2, TimeUnit.SECONDS));
        startedServers.add(server);
        return server;
    }

    private static final class TestWebSocketServer extends WebSocketServer {
        private final List<String> messagesOnOpen;
        private final boolean closeAfterSend;
        private final CountDownLatch started = new CountDownLatch(1);
        private final CountDownLatch clientConnected = new CountDownLatch(1);

        private TestWebSocketServer(List<String> messagesOnOpen, boolean closeAfterSend) {
            super(new InetSocketAddress("127.0.0.1", 0));
            this.messagesOnOpen = messagesOnOpen;
            this.closeAfterSend = closeAfterSend;
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            clientConnected.countDown();
            for (String message : messagesOnOpen) {
                conn.send(message);
            }
            if (closeAfterSend) {
                conn.close(1000, "done");
            }
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            // no-op for tests
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            // server does not need to process incoming messages in these tests
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            // no-op for tests
        }

        @Override
        public void onStart() {
            started.countDown();
        }

        private boolean awaitStarted(long timeout, TimeUnit unit) throws InterruptedException {
            return started.await(timeout, unit);
        }

        private boolean awaitClientConnected(long timeout, TimeUnit unit) throws InterruptedException {
            return clientConnected.await(timeout, unit);
        }

        private String getWsUrl() {
            return "ws://127.0.0.1:" + getPort();
        }

        private void stopSafely() throws Exception {
            if (getConnections() != null) {
                for (WebSocket connection : getConnections()) {
                    connection.close();
                }
            }
            if (started.getCount() == 0) {
                stop(1000);
            }
        }
    }
}
