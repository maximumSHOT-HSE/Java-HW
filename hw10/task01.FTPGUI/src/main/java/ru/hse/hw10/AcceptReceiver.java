package ru.hse.hw10;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.concurrent.locks.Lock;

public class AcceptReceiver implements Runnable {

    private static final int PORT = 9999;

    @NotNull private ServerSocketChannel serverSocketChannel;

    @NotNull private Selector inputListenerSelector;
    @NotNull private Lock inputListenerSelectorLock;

    public AcceptReceiver(@NotNull Selector inputListenerSelector,
                          @NotNull Lock inputListenerSelectorLock) throws IOException {
        this.inputListenerSelector = inputListenerSelector;
        this.inputListenerSelectorLock = inputListenerSelectorLock;
        serverSocketChannel = ServerSocketChannel.open()
                .bind(new InetSocketAddress(PORT));
        serverSocketChannel.configureBlocking(false);

        String serverIP;
        try (final var socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), PORT);
            serverIP = socket.getLocalAddress().getHostAddress();
        } catch (Exception ignored) {
            serverIP = "UNKNOWN";
        }

        System.out.println("SERVER LISTENING...");
        System.out.println("IP = " + serverIP);
        System.out.println("PORT = " + PORT);
    }

    @Override
    public void run() {
        System.out.println("RECEIVER BEGIN");
        while (true) {
            SocketChannel socketChannel = null;
            try {
                socketChannel = serverSocketChannel.accept();
            } catch (IOException ignore) {
                // TODO handle me
            }
            if (socketChannel == null) {
                continue;
            }
            System.out.println("ACCEPT!!! + " + socketChannel);
            try {
                socketChannel.configureBlocking(false);
                socketChannel.socket().setTcpNoDelay(true);
            } catch (IOException ignore) {
                // TODO handle me
            }
            var data = new ClientData();
            try {
                inputListenerSelectorLock.lock();
                socketChannel.register(inputListenerSelector, SelectionKey.OP_READ, data);
            } catch (ClosedChannelException e) {
                // TODO handle me
            } finally {
                inputListenerSelectorLock.unlock();
            }
        }
    }
}
