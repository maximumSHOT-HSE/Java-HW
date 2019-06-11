package ru.hse.hw10;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.locks.Lock;

/**
 * Task for thread pool is parse client's
 * request and provide processed data for output
 * writer which will send right answer for user
 * using processed request.
 */
public class ThreadPoolTask implements Runnable {
    @NotNull private ClientData data;
    @NotNull private SocketChannel socketChannel;

    @NotNull private Selector outputWriterSelector;
    @NotNull private Lock outputWriterSelectorLock;

    public ThreadPoolTask(@NotNull ClientData data,
                          @NotNull SocketChannel socketChannel,
                          @NotNull Selector outputWriterSelector,
                          @NotNull Lock outputWriterSelectorLock) {
        this.data = data;
        this.socketChannel = socketChannel;
        this.outputWriterSelector = outputWriterSelector;
        this.outputWriterSelectorLock = outputWriterSelectorLock;
    }

    /*
    * Parsed request will be stored in the client data
    */
    private void parseRequest() throws IOException {
        Server.LOGGER.info("parse");
        var inputStream = new DataInputStream(new ByteArrayInputStream(data.getRequest()));
        int bytesNumber = inputStream.readInt();
        byte requestType = inputStream.readByte();
        String path = inputStream.readUTF();
        data.setRequestType(ClientData.RequestType.get(requestType));
        data.setPath(path);
        data.processRequest();
        Server.LOGGER.info("bytesNumber = " + bytesNumber
                + ", requestType = " + requestType
                + ", path = " + path);
    }

    @Override
    public void run() {
        try {
            parseRequest();
        } catch (IOException ignored) {
            // TODO handle me
        }
        try {
            outputWriterSelectorLock.lock();
            Server.LOGGER.info("try to register");
            socketChannel.register(outputWriterSelector,
                    SelectionKey.OP_WRITE, data);
        } catch (ClosedChannelException ignore) {
            // TODO handle me
        } finally {
            outputWriterSelectorLock.unlock();
        }
    }
}