package ru.hse.hw10;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.channels.Selector;

/**
 * Task for thread pool is parse client's
 * request and provide processed data for user.
 * OutputWriter will send right answer for user
 * using processed request.
 * */
public class ThreadPoolTask implements Runnable {

    private static final int BYTES_NUMBER_BLOCK_SIZE = 4;
    private static final int REQUEST_TYPE_BLOCK_SIZE = 1;
    private static final int BYTE_SEQUENCE_BLOCK_SIZE = 4096;

    @NotNull private ByteBuffer bytesNumberBuffer = ByteBuffer.allocate(BYTES_NUMBER_BLOCK_SIZE);
    @NotNull private ByteBuffer requestTypeBuffer = ByteBuffer.allocate(REQUEST_TYPE_BLOCK_SIZE);
    @NotNull private ByteBuffer byteSequenceBuffer = ByteBuffer.allocate(BYTE_SEQUENCE_BLOCK_SIZE);

    @NotNull private ClientData data;
    @NotNull private Selector outputWriterSelector;

    public ThreadPoolTask(@NotNull ClientData data, @NotNull Selector outputWriterSelector) {
        this.data = data;
        this.outputWriterSelector = outputWriterSelector;
    }

    /*
    * Parsed request will be stored in the client data
    * */
    private void parseRequest() {
        var request = data.getRequest();
        // TODO
    }

    @Override
    public void run() {
        parseRequest();

    }
}