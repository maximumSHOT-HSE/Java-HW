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

    private void writeToChannel(@NotNull SelectionKey key) {
        var data = (ClientData) key.attachment();
        System.out.println("TRY OT WRITE to channel: is Finished = " + data.isFinished());
        if (data.isFinished()) {
            try {
                key.cancel();
                key.channel().close();
                data.close();
            } catch (IOException ignore) {
                // TODO handle me
            }
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

    @Override
    public void run() {
        System.out.println("RUN OUT writer???");
        while (!Thread.interrupted()) {
            if (Server.select(outputWriterSelector) == 0) {
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
        try {
            outputWriterSelector.close();
        } catch (IOException ignore) {
            // TODO handle me
        }
    }
}
