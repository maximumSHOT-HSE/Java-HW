package ru.hse.hw10;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Server worker which sends answers for the client's
 * requests and if answer has been provided fully
 * then connection with such client will be closed
 */
public class OutputWriter implements Runnable {
    @NotNull private Selector outputWriterSelector;

    public OutputWriter(@NotNull Selector outputWriterSelector) {
        this.outputWriterSelector = outputWriterSelector;
    }

    private void writeToChannel(@NotNull SelectionKey key) {
        var data = (ClientData) key.attachment();
        data.fillBuffer();
        if (data.isFinished()) {
            key.cancel();
            return;
        }
        var buffer = data.getBuffer();
        var socketChannel = (SocketChannel) key.channel();
        try {
            socketChannel.write(buffer);
        } catch (IOException ignore) {
        }
    }

    @Override
    public void run() {
        Server.LOGGER.info("run out writer");
        while (!Thread.interrupted()) {
            if (Server.select(outputWriterSelector) == 0) {
                continue;
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
        try {
            outputWriterSelector.close();
        } catch (IOException ignore) {
        }
    }
}
