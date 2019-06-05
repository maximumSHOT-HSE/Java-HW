package ru.hse.hw10;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.hse.hw10.client.Client;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    private static final int PORT = 9999;
    private static String serverIP;

    @BeforeAll
    static void setUp() throws IOException {
        try (final var socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), PORT);
            serverIP = socket.getLocalAddress().getHostAddress();
        } catch (Exception ignored) {
            serverIP = "UNKNOWN";
        }
        new Server().start();
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
        expectedContent.put("TestDir", true);
        assertEquals(expectedContent, foundContent);
    }

    @Test
    void testNonExistentDirectoryListRequest() throws UnknownHostException {
        var client = new Client(serverIP, PORT);
        var answer = client.executeList("src/test/resources/Dir3");
        assertNull(answer);
    }

    @Test
    void testSimpleRequestSequence() throws UnknownHostException {
        var client = new Client(serverIP, PORT);
        Map<String, Boolean> foundContent = new TreeMap<>();
        for (var file : client.executeList("src/test/resources")) {
            foundContent.put(file.getName(), file.isDirectory());
        }
        Map<String, Boolean> expectedContent = new TreeMap<>();
        expectedContent.put("Dir1", true);
        expectedContent.put("TestDir", true);
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
    }


}