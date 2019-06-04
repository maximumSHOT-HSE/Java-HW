package ru.hse.hw10;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.concurrent.locks.Lock;

public class AcceptReceiver implements Runnable {

    @NotNull private ServerSocketChannel serverSocketChannel;

    @NotNull private Selector inputListenerSelector;
    @NotNull private Lock inputListenerSelectorLock;

    public AcceptReceiver(@NotNull Selector inputListenerSelector,
                          @NotNull Lock inputListenerSelectorLock) throws IOException {
        this.inputListenerSelector = inputListenerSelector;
        this.inputListenerSelectorLock = inputListenerSelectorLock;
        serverSocketChannel = ServerSocketChannel
                .open()
                .bind(new InetSocketAddress(InetAddress.getLocalHost(), 9999));
        serverSocketChannel.configureBlocking(false);
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
            SelectionKey key;
            try {
                inputListenerSelectorLock.lock();
                key = socketChannel.register(inputListenerSelector, SelectionKey.OP_READ, data);
            } catch (ClosedChannelException e) {
                // TODO handle me
                continue;
            } finally {
                inputListenerSelectorLock.unlock();
            }
            key.interestOps(SelectionKey.OP_READ);
        }
    }
}
