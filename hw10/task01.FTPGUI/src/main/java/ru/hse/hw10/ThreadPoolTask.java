package ru.hse.hw10;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Task for thread pool is parse client's
 * request and provide processed data for user.
 * OutputWriter will send right answer for user
 * using processed request.
 * */
public class ThreadPoolTask implements Runnable {

    @NotNull private ClientData data;
    @NotNull private SocketChannel socketChannel;
    @NotNull private Selector outputWriterSelector;

    public ThreadPoolTask(@NotNull ClientData data,
                          @NotNull SocketChannel socketChannel,
                          @NotNull Selector outputWriterSelector) {
        this.data = data;
        this.socketChannel = socketChannel;
        this.outputWriterSelector = outputWriterSelector;
    }

    /*
    * Parsed request will be stored in the client data
    * */
    private void parseRequest() throws IOException {
        var inputStream = new DataInputStream(new ByteArrayInputStream(data.getRequest()));
        int byteNumber = inputStream.readInt();
        byte requestType = inputStream.readByte();
        String path = inputStream.readUTF();
        data.setRequestType(requestType == 1 ? ClientData.RequestType.LIST : ClientData.RequestType.GET);
        data.setPath(path);
        data.processRequest();
    }

    @Override
    public void run() {
        try {
            parseRequest();
        } catch (IOException ignored) {
            // TODO handle me
        }
        try {
            socketChannel.register(outputWriterSelector, SelectionKey.OP_WRITE, data);
        } catch (ClosedChannelException ignore) {
            // TODO handle me
        }
    }
}