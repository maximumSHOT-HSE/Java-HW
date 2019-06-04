package ru.hse.hw10;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class OutputWriter implements Runnable {

    private static final int TIMEOUT = 1000;

    @NotNull private Selector outputWriterSelector;

    public OutputWriter(@NotNull Selector outputWriterSelector) {
        this.outputWriterSelector = outputWriterSelector;
    }

    private void processList(@NotNull ClientData data, @NotNull SelectionKey key) {
//        System.out.println("TRY OT WRITE to channel: is Finished = " + data.isFinished());
        if (data.isFinished()) {
            key.cancel();
            return;
        }
        var buffer = data.getBuffer();
        var socketChannel = (SocketChannel) key.channel();
        try {
            socketChannel.write(buffer);
        } catch (IOException ignore) {
            // TODO handle me
        }
    }

    private void processGet(@NotNull ClientData data) {
        // TODO finish it
    }

    private void writeToChannel(@NotNull SelectionKey key) {
        var data = (ClientData) key.attachment();
        switch (data.getRequestType()) {
            case LIST:
                processList(data, key);
                break;
            case GET:
                processGet(data);
                break;
        }
    }

    private int select() {
        int lastSelect;
        try {
            lastSelect = outputWriterSelector.select(TIMEOUT);
        } catch (IOException ignored) {
            // TODO handle me
            lastSelect = 0;
        }
        return lastSelect;
    }

    @Override
    public void run() {
        System.out.println("RUN OUT writer???");
        while (true) {
            if (select() == 0) {
                continue; // TODO remove duplicate code
            }
            var selectedKeys = outputWriterSelector.selectedKeys();
            var iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                var key = iterator.next();
                if (key.isWritable()) {
                    writeToChannel(key);
                }
                iterator.remove();
            }
        }
    }
}
