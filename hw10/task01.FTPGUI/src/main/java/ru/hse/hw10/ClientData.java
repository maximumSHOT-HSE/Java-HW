package ru.hse.hw10;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Storage for client's request and
 * answer for it: file path on server's side
 * which should be downloaded or listing of
 * current server's directory.
 *
 * Request format in bytes
 * Firstly, the number of byres is int variable
 * Secondly, one byte for request type
 * Finally, the sequence of bytes associated with
 * string provided by client.
 * */
public class ClientData {

    private static final int BLOCK_SIZE = 4096;

    public enum RequestType {
        UNDEFINED,
        LIST,
        GET
    }

    @NotNull private List<Byte> request = new ArrayList<>();
    @NotNull private RequestType requestType = RequestType.UNDEFINED;
    @NotNull private String path;
    @NotNull private ByteBuffer buffer = ByteBuffer.allocate(BLOCK_SIZE);

    @NotNull private DataInputStream answerInputStream;
    private int remainingBytesNumber;

    public void append(byte b) {
        request.add(b);
    }

    private int getBytesNumber() { // TODO develop adequate converting from bytes to int
        byte[] helper = new byte[4];
        var inputStream = new DataInputStream(new ByteArrayInputStream(helper));
        try {
            return inputStream.readInt();
        } catch (IOException e) {
            return 0;
        }
    }

    public boolean isFull() {
        if (request.size() < 4) {
            return false;
        }
        int bytesNumber = getBytesNumber();
        return request.size() == bytesNumber + 4;
    }

    public byte[] getRequest() {
        // TODO modify code
        byte[] bytes = new byte[request.size()];
        for (int i = 0; i < request.size(); i++) {
            bytes[i] = request.get(i);
        }
        return bytes;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public String getPath() {
        return path;
    }

    private void processError() {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        var dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeInt(-1);
        } catch (IOException ignored) {
            // TODO handle me
        }
    }

    public void processRequest() {
        switch (requestType) {
            case GET:
                processGet();
                break;
            case LIST:
                processList();
                break;
        }
    }

    private void processList() {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            processError();
            return;
        }
        var byteArrayOutputStream = new ByteArrayOutputStream();
        var dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        var childFiles = file.listFiles();
        try {
            dataOutputStream.writeInt(Objects.requireNonNull(childFiles).length);
            for (var childFile : childFiles) {
                dataOutputStream.writeUTF(childFile.getName());
                dataOutputStream.writeBoolean(childFile.isDirectory());
            }
        } catch (IOException ignored) {
            // TODO handle me
        }
        remainingBytesNumber = byteArrayOutputStream.toByteArray().length;
        answerInputStream = new DataInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        buffer.putInt(remainingBytesNumber);
    }

    public void processGet() {
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public DataInputStream getAnswerInputStream() {
        return answerInputStream;
    }

    public int getRemainingBytesNumber() {
        return remainingBytesNumber;
    }

    /**
     * Checks whether there exists information to work with.
     * If there is no such information then true will be returned.
     * Otherwise, true will be returned. Moreover, in last case
     * if buffer has not any byte write to a channel then
     * extra bytes will be moved to the buffer. After that
     * buffer will be in correct state.
     * */
    public boolean isFinished() {
        if (!buffer.hasRemaining()) {
            if (remainingBytesNumber == 0) {
                return true;
            }
            buffer.clear();
            while (remainingBytesNumber > 0 && buffer.position() < buffer.limit()) {
                try {
                    buffer.put(answerInputStream.readByte());
                    remainingBytesNumber--;
                } catch (IOException ignore) {
                    // TODO handle me
                }
            }
        }
        return false;
    }
}
