package ru.hse.hw10;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.hse.hw10.client.Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    private static final int PORT = 9999;
    private static String serverIP;
    private static Server server;

    @BeforeAll
    static void setUp() throws IOException {
        try (final var socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), PORT);
            serverIP = socket.getLocalAddress().getHostAddress();
        } catch (Exception ignored) {
            serverIP = "UNKNOWN";
        }
        server = new Server();
        server.start(PORT);
    }

    @AfterAll
    static void free() throws IOException, InterruptedException {
        server.stop();
    }

    @Test
    void testSimpleListRequest() throws UnknownHostException {
        var client = new Client(serverIP, PORT);
        Map<String, Boolean> foundContent = new TreeMap<>();
        for (var file : client.executeList("src/test/resources")) {
            foundContent.put(file.getName(), file.isDirectory());
        }
        Map<String, Boolean> expectedContent = new TreeMap<>();
        expectedContent.put("Dir1", true);
        assertEquals(expectedContent, foundContent);
    }

    @Test
    void testNonExistentDirectoryListRequest() throws UnknownHostException {
        var client = new Client(serverIP, PORT);
        var answer = client.executeList("src/test/resources/Dir3");
        assertNull(answer);
    }

    @Test
    void testSimpleListRequestSequence() throws UnknownHostException {
        var client = new Client(serverIP, PORT);
        Map<String, Boolean> foundContent = new TreeMap<>();
        for (var file : client.executeList("src/test/resources")) {
            foundContent.put(file.getName(), file.isDirectory());
        }
        Map<String, Boolean> expectedContent = new TreeMap<>();
        expectedContent.put("Dir1", true);
        assertEquals(expectedContent, foundContent);
        assertNull(client.executeList("blablabla"));
        foundContent.clear();
        for (var file : client.executeList("src/test/resources/Dir1")) {
            foundContent.put(file.getName(), file.isDirectory());
        }
        expectedContent.clear();
        expectedContent.put("Subdir1", true);
        expectedContent.put("Subdir2", true);
        expectedContent.put("file1.txt", false);
        expectedContent.put("file2.txt", false);
        assertEquals(expectedContent, foundContent);
    }

    @Test
    void testSimpleGetRequest() throws IOException {
        var client = new Client(serverIP, PORT);
        var file = new File("FILE_FOR_TESTING.TXT");
        assertTrue(file.createNewFile());
        assertTrue(client.executeGet(Paths.get("src/test/resources/Dir1/file1.txt"), file.toPath()));
        var foundBytes = new FileInputStream(file).readAllBytes();
        var foundContent = new String(foundBytes, StandardCharsets.UTF_8);
        var expectedContent = "Hello, I am the first file!";
        assertEquals(expectedContent, foundContent);
        assertTrue(file.delete());
    }

    @Test
    void testUnexistentFileGetRequest() throws UnknownHostException {
        var client = new Client(serverIP, PORT);
        assertFalse(client.executeGet(Paths.get("src/test/resources/Dir1/filee2.txt"),
                Paths.get(".")));
    }

    @Test
    void testGetAfterNonexistentFileOrDirectoryAksed() throws UnknownHostException {
        var client = new Client(serverIP, PORT);
        Map<String, Boolean> foundContent = new TreeMap<>();
        for (var file : client.executeList("src/test/resources")) {
            foundContent.put(file.getName(), file.isDirectory());
        }
        Map<String, Boolean> expectedContent = new TreeMap<>();
        expectedContent.put("Dir1", true);
        assertEquals(expectedContent, foundContent);
        assertNull(client.executeList("blablabla"));
        assertFalse(client.executeGet(Paths.get("src/test/resources/Dir1/filee2.txt"), Paths.get(".")));
        foundContent.clear();
        for (var file : client.executeList("src/test/resources/Dir1")) {
            foundContent.put(file.getName(), file.isDirectory());
        }
        expectedContent.clear();
        expectedContent.put("Subdir1", true);
        expectedContent.put("Subdir2", true);
        expectedContent.put("file1.txt", false);
        expectedContent.put("file2.txt", false);
        assertEquals(expectedContent, foundContent);
    }

    @Test
    void testMultiClientMode() throws InterruptedException {
        var threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            final int I = i;
            threads[i] = new Thread(() -> {
                Client client = null;
                try {
                    client = new Client(serverIP, PORT);
                } catch (UnknownHostException ignore) {
                    // simplify for testing
                }
                Map<String, Boolean> foundContent = new TreeMap<>();
                assert client != null;
                for (var file : client.executeList("src/test/resources")) {
                    foundContent.put(file.getName(), file.isDirectory());
                }
                Map<String, Boolean> expectedContent = new TreeMap<>();
                expectedContent.put("Dir1", true);
                assertEquals(expectedContent, foundContent);
                assertNull(client.executeList("blablabla"));
                assertFalse(client.executeGet(Paths.get("src/test/resources/Dir1/filee2.txt"), Paths.get(".")));
                foundContent.clear();
                for (var file : client.executeList("src/test/resources/Dir1")) {
                    foundContent.put(file.getName(), file.isDirectory());
                }
                expectedContent.clear();
                expectedContent.put("Subdir1", true);
                expectedContent.put("Subdir2", true);
                expectedContent.put("file1.txt", false);
                expectedContent.put("file2.txt", false);
                assertEquals(expectedContent, foundContent);
                var file = new File("HELPER_FILE_" + I);
                assertDoesNotThrow(() -> assertTrue(file.createNewFile()));
                assertTrue(client.executeGet(Paths.get("src/test/resources/Dir1/file1.txt"), file.toPath()));
                byte[] foundBytes = new byte[0];
                try {
                    foundBytes = new FileInputStream(file).readAllBytes();
                } catch (IOException ignore) {
                    fail();
                }
                var foundContentString = new String(foundBytes, StandardCharsets.UTF_8);
                var expectedContentString = "Hello, I am the first file!";
                assertEquals(expectedContentString, foundContentString);
                assertTrue(file.delete());
            });
        }
        for (int i = 0; i < 10; i++) {
            threads[i].start();
        }
        for (int i = 0; i < 10; i++) {
            threads[i].join();
        }
    }
}