package ru.hse.hw10;

import org.jetbrains.annotations.NotNull;

import javax.print.DocFlavor;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
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
    private static final int ERROR_CODE = -1;

    public enum RequestType {
        UNDEFINED(0),
        LIST(1),
        GET(2);

        private int type;

        RequestType(int type) {
            this.type = type;
        }

        public static RequestType get(int type) {
            for (var value : RequestType.values()) {
                if (value.type == type) {
                    return value;
                }
            }
            return UNDEFINED;
        }
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
        byte[] helper = new byte[Integer.BYTES];
        for (int i = 0; i < Integer.BYTES; i++) {
            helper[i] = request.get(i);
        }
        var inputStream = new DataInputStream(new ByteArrayInputStream(helper));
        try {
            return inputStream.readInt();
        } catch (IOException e) {
            return 0;
        }
    }

    public boolean isFull() {
        System.out.println("is full.");
        System.out.println("sz = " + request.size());
        if (request.size() < 4) {
            return false;
        }
        int bytesNumber = getBytesNumber();
        System.out.println("bytesNumber = " + bytesNumber);
        return request.size() == bytesNumber + 4;
    }

    public byte[] getRequest() {
        // TODO modify code
        System.out.println("GET REQUEST : ");
        byte[] bytes = new byte[request.size()];
        for (int i = 0; i < request.size(); i++) {
            bytes[i] = request.get(i);
            System.out.print(bytes[i] + " ");
        }
        System.out.println();
        return bytes;
    }

    public void setRequestType(@NotNull RequestType requestType) {
        this.requestType = requestType;
    }

    public void setPath(@NotNull String path) {
        this.path = path;
    }

    @NotNull public RequestType getRequestType() {
        return requestType;
    }

    @NotNull public String getPath() {
        return path;
    }

    private void processError() {
        System.out.println("Client. process error !!!");
        buffer.clear();
        buffer.putInt(Integer.BYTES);
        buffer.putInt(ERROR_CODE);
        buffer.flip();
        remainingBytesNumber = 0;
    }

    public void processRequest() {
        switch (requestType) {
            case LIST:
                processList();
                break;
            case GET:
                processGet();
                break;
        }
    }

    private void processList() {
        System.out.println("Client.processList()");
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            processError();
            return;
        }
        var byteArrayOutputStream = new ByteArrayOutputStream();
        var dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        var childFiles = file.listFiles();
        for (var child : childFiles) {
            System.out.println(child.getName() + " is dir = " + child.isDirectory());
        }
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
        System.out.println("total number of useful bytes = " + remainingBytesNumber);
        answerInputStream = new DataInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        buffer.putInt(remainingBytesNumber);
        buffer.flip();
    }

    public void processGet() {
        // TODO finish it
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    @NotNull public DataInputStream getAnswerInputStream() {
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
//        System.out.println("rem = " + remainingBytesNumber + " had rem = " + buffer.hasRemaining());
        if (!buffer.hasRemaining()) {
            if (remainingBytesNumber == 0) {
                return true;
            }
            buffer.clear();
            while (remainingBytesNumber > 0 && buffer.position() < buffer.limit()) {
                try {
                    byte b = answerInputStream.readByte();
                    System.out.println("b = " + b + ", rem = " + remainingBytesNumber);
                    buffer.put(b);
//                    buffer.put(answerInputStream.readByte());
                    remainingBytesNumber--;
                } catch (IOException ignore) {
                    // TODO handle me
                }
            }
            buffer.flip();
        }
        return false;
    }
}
