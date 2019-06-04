package ru.hse.hw10;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import ru.hse.hw10.client.Client;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void test1() throws UnknownHostException {
        var client = new Client(serverIP, PORT);
        var answer = client.executeList(".");
        Map<String, Boolean> foundContent = new TreeMap<>();
        for (var file : answer) {
            System.out.println(file.getName() + " " + file.isDirectory());
            foundContent.put(file.getName(), file.isDirectory());
        }
    }
}