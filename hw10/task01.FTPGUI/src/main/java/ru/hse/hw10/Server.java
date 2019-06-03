package ru.hse.hw10;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    @NotNull private ExecutorService inputListenerService = Executors.newSingleThreadExecutor();
    @NotNull private ExecutorService outputWriterService = Executors.newSingleThreadExecutor();
    @NotNull private ExecutorService threadPool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());

    @Nullable private ServerSocketChannel serverSocketChannel;
    @Nullable private Selector inputListenerSelector;
    @Nullable private Selector outputWriterSelector;

    void start(int port) throws IOException {
        serverSocketChannel = ServerSocketChannel.open().bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
        serverSocketChannel.configureBlocking(true);
        inputListenerSelector = Selector.open();
        outputWriterSelector = Selector.open();
        inputListenerService.submit(new InputListener(inputListenerSelector, outputWriterSelector));
        outputWriterService.submit(new OutputWriter());
        while (true) {
            var socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            var data = new ClientData();
            var selectionKey = socketChannel.register(inputListenerSelector, SelectionKey.OP_READ, data);
        }
    }
}
