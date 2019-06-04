package ru.hse.hw10;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Server {
    static final Logger LOGGER = Logger.getLogger("ServerLogger");
    private static final int TIMEOUT = 1000;

    @NotNull private ExecutorService threadPool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());

    @NotNull private Selector inputListenerSelector;
    @NotNull private Lock inputListenerSelectorLock = new ReentrantLock();

    @NotNull private Selector outputWriterSelector;
    @NotNull private Lock outputWriterSelectorLock = new ReentrantLock();

    @NotNull private Thread acceptReceiverThread;
    @NotNull private Thread inputListenerThread;
    @NotNull private Thread outputWriterThread;

    public void start() throws IOException {
        inputListenerSelector = Selector.open();
        outputWriterSelector = Selector.open();
        inputListenerThread = new Thread(new InputListener(
                inputListenerSelector, inputListenerSelectorLock,
                outputWriterSelector, outputWriterSelectorLock, threadPool));
        outputWriterThread = new Thread(new OutputWriter(outputWriterSelector));
        acceptReceiverThread = new Thread(new AcceptReceiver(inputListenerSelector,
                inputListenerSelectorLock));
        inputListenerThread.start();
        outputWriterThread.start();
        acceptReceiverThread.start();
    }

    public void stop() throws IOException {
        inputListenerThread.interrupt();
        outputWriterThread.interrupt();
        acceptReceiverThread.interrupt();
        threadPool.shutdown();
        inputListenerSelector.close();
        outputWriterSelector.close();
        LOGGER.info("server stop");
    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.start();
        } catch (IOException e) {
            // ignore
        }
    }

    public Server() {
        setupLogger();
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

    private void setupLogger() {
        if (LOGGER.getHandlers().length == 0) {
            try {
                LOGGER.setUseParentHandlers(false);
                LOGGER.addHandler(new FileHandler("serverLogs"));
            } catch (IOException ignored) {
            }
        }
    }
}
