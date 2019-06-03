package ru.hse.hw10;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public class OutputWriter implements Runnable {

    @NotNull private Selector outputWriterSelector;

    public OutputWriter(@NotNull Selector outputWriterSelector) {
        this.outputWriterSelector = outputWriterSelector;
    }

    private void processList(ClientData data) {

    }

    private void processGet(ClientData data) {
        String path = data.getPath();
    }

    private void writeToChannel(SelectionKey key) {
        var data = (ClientData) key.attachment();
        switch (data.getRequestType()) {
            case LIST:
                processList(data);
                break;
            case GET:
                processGet(data);
                break;
        }
    }

    private int select() {
        int lastSelect;
        try {
            lastSelect = outputWriterSelector.select();
        } catch (IOException ignored) {
            // TODO handle me
            lastSelect = 0;
        }
        return lastSelect;
    }

    @Override
    public void run() {
        while (true) {
            if (select() == 0) {
                continue;
            }
            var selectedKeys=  outputWriterSelector.selectedKeys();
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
