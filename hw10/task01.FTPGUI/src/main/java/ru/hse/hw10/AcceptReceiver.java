package ru.hse.hw10;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.concurrent.locks.Lock;

/**
 * Server worker which binds server socket
 * to the predefined port, accepts new
 * request from clients and after client
 * will be accepted then appropriate socket
 * channel will be registered in input listener
 */
public class AcceptReceiver implements Runnable {
    @NotNull private ServerSocketChannel serverSocketChannel;

    @NotNull private Selector inputListenerSelector;
    @NotNull private Lock inputListenerSelectorLock;

    public AcceptReceiver(@NotNull Selector inputListenerSelector,
                          @NotNull Lock inputListenerSelectorLock, int port) throws IOException {
        this.inputListenerSelector = inputListenerSelector;
        this.inputListenerSelectorLock = inputListenerSelectorLock;
        serverSocketChannel = ServerSocketChannel.open()
                .bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
    }

    @Override
    public void run() {
        Server.LOGGER.info("RECEIVER BEGIN");
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
            Server.LOGGER.info("ACCEPT + " + socketChannel);
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
