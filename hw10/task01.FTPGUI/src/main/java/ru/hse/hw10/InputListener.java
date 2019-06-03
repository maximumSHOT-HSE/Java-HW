package ru.hse.hw10;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

public class InputListener implements Runnable {

    private static final int BLOCK_SIZE = 4096;

    @NotNull private Selector inputListenerSelector;
    @NotNull private Selector outputWriterSelector;
    @NotNull private ByteBuffer buffer = ByteBuffer.allocate(BLOCK_SIZE);
    @NotNull private ExecutorService threadPool;

    public InputListener(@NotNull Selector inputListenerSelector,
                         @NotNull Selector outputWriterSelector,
                         @NotNull ExecutorService threadPool) {
        this.inputListenerSelector = inputListenerSelector;
        this.outputWriterSelector = outputWriterSelector;
        this.threadPool = threadPool;
    }

    private void readFromChannel(SelectionKey key) throws IOException {
        var data = (ClientData) key.attachment();
        if (data == null) {
            return;
        }
        buffer.clear();
        ((SocketChannel) key.channel()).read(buffer);
        buffer.flip();
        while (buffer.hasRemaining()) {
            data.append(buffer.get());
        }
        if (data.isFull()) {
            threadPool.submit(new ThreadPoolTask(data, outputWriterSelector));
            key.cancel();
        }
    }

    @Override
    public void run() {
        int lastSelect;
        while (true) {
            try {
                lastSelect = inputListenerSelector.select();
            } catch (IOException ignored) {
                continue;
            } catch (ClosedSelectorException e) {
                break;
            }
            if (lastSelect == 0) {
                continue;
            }
            var selectedKeys = inputListenerSelector.selectedKeys();
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
