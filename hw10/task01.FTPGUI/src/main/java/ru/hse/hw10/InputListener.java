package ru.hse.hw10;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;

public class InputListener implements Runnable {

    private static final int BLOCK_SIZE = 4096;
    private static final int TIMEOUT = 1000;

    @NotNull private Selector inputListenerSelector;
    @NotNull private Lock inputListenerSelectorLock;

    @NotNull private Selector outputWriterSelector;
    @NotNull private Lock outputWriterSelectorLock;

    @NotNull private ByteBuffer buffer = ByteBuffer.allocate(BLOCK_SIZE);
    @NotNull private ExecutorService threadPool;

    public InputListener(@NotNull Selector inputListenerSelector,
                         @NotNull Lock inputListenerSelectorLock,
                         @NotNull Selector outputWriterSelector,
                         @NotNull Lock outputWriterSelectorLock,
                         @NotNull ExecutorService threadPool) {
        this.inputListenerSelector = inputListenerSelector;
        this.inputListenerSelectorLock = inputListenerSelectorLock;
        this.outputWriterSelector = outputWriterSelector;
        this.outputWriterSelectorLock = outputWriterSelectorLock;
        this.threadPool = threadPool;
    }

    private void readFromChannel(SelectionKey key) throws IOException {
        var data = (ClientData) key.attachment();
        buffer.clear();
        ((SocketChannel) key.channel()).read(buffer);
        buffer.flip();
        while (buffer.hasRemaining()) {
            byte b = buffer.get();
//            data.append(buffer.get());
            System.out.println("b = " + b);
            data.append(b);
        }
        if (data.isFull()) {
            key.cancel();
            threadPool.submit(new ThreadPoolTask(data, (SocketChannel) key.channel(),
                    outputWriterSelector, outputWriterSelectorLock));
        }
    }

    @Override
    public void run() {
        int lastSelect;
        while (true) {
            try {
                lastSelect = inputListenerSelector.select(TIMEOUT);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            if (lastSelect == 0) {
                continue;
            }
            Set<SelectionKey> selectedKeys;
            try {
                inputListenerSelectorLock.lock();
                selectedKeys = inputListenerSelector.selectedKeys();
            } finally {
                inputListenerSelectorLock.unlock();
            }
            var iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                var key = iterator.next();
                if (key.isReadable()) {
                    try {
                        readFromChannel(key);
                    } catch (IOException ignored) {
                        // to simplify home task such exceptions can be skipped
                    }
                }
                iterator.remove();
            }
        }
    }
}
