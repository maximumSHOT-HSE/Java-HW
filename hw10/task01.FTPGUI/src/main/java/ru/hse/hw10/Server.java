package ru.hse.hw10;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {

    private static final int TIMEOUT = 1000;

    @NotNull private ExecutorService acceptReceiverService = Executors.newSingleThreadExecutor();
    @NotNull private ExecutorService inputListenerService = Executors.newSingleThreadExecutor();
    @NotNull private ExecutorService outputWriterService = Executors.newSingleThreadExecutor();
    @NotNull private ExecutorService threadPool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());

    @NotNull private Selector inputListenerSelector;
    @NotNull private Lock inputListenerSelectorLock = new ReentrantLock();

    @NotNull private Selector outputWriterSelector;
    @NotNull private Lock outputWriterSelectorLock = new ReentrantLock();

    void start() throws IOException {
        inputListenerSelector = Selector.open();
        outputWriterSelector = Selector.open();
        inputListenerService.submit(new InputListener(inputListenerSelector, inputListenerSelectorLock,
                outputWriterSelector, outputWriterSelectorLock, threadPool));
        outputWriterService.submit(new OutputWriter(outputWriterSelector));
        acceptReceiverService.submit(new AcceptReceiver(inputListenerSelector, inputListenerSelectorLock));
    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.start();
        } catch (IOException e) {
            // ignore
        }
    }

    public static int select(@NotNull Selector selector) {
        int lastSelect;
        try {
            lastSelect = selector.select(TIMEOUT);
        } catch (IOException ignored) {
            lastSelect = 0;
        }
        return lastSelect;
    }
}
