package ru.hse.hw10;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Storage for client's request and
 * answer for it: input steam
 * associated with the file on server's side
 * which should be downloaded or listing of
 * current server's directory.
 *
 * Request format in bytes
 * Firstly, the number of bytes in package
 * is integer variable
 * Secondly, one byte for request type
 * Finally, the sequence of bytes associated with
 * string provided by client.
 */
public class ClientData {
    private static final int ERROR_CODE = -1;

    /**
     * Type of request to the server.
     */
    public enum RequestType {
        UNDEFINED(0),
        LIST(1),
        GET(2);

        private int type;

        RequestType(int type) {
            this.type = type;
        }

        /**
         * Determines request type to the server
         * by it's integer value.
         */
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
    @Nullable private String pathString;
    @NotNull private ByteBuffer buffer = ByteBuffer.allocate(Server.getBlockSize());

    @NotNull private DataInputStream answerInputStream;
    private int remainingBytesNumber;

    /**
     * Adds one byte to the request byte sequence
     */
    public void append(byte b) {
        request.add(b);
    }

    /*
    * Determines the number of bytes in request package
    */
    private int getBytesNumber() {
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

    /**
     * Determines whether request package has been received fully or not
     *
     * @return true if package has been received fully and false otherwise
     */
    public boolean isFull() {
        Server.LOGGER.info("is full.");
        Server.LOGGER.info("sz = " + request.size());
        if (request.size() < Integer.BYTES) {
            return false;
        }
        int bytesNumber = getBytesNumber();
        Server.LOGGER.info("bytesNumber = " + bytesNumber);
        return request.size() == bytesNumber + Integer.BYTES;
    }

    /**
     * Converts the request package to the byte array
     * and returns it.
     *
     * @return the request package in form of byte array.
     */
    @NotNull public byte[] getRequest() {
        Server.LOGGER.info("get request : ");
        byte[] bytes = new byte[request.size()];
        for (int i = 0; i < request.size(); i++) {
            bytes[i] = request.get(i);
            Server.LOGGER.info(bytes[i] + " ");
        }
        return bytes;
    }

    public void setRequestType(@NotNull RequestType requestType) {
        this.requestType = requestType;
    }

    public void setPathString(@NotNull String pathString) {
        this.pathString = pathString;
    }

    /*
    * Forms answer for the invalid request.
    */
    private void processError() {
        Server.LOGGER.info("Client process error");
        buffer.clear();
        buffer.putInt(Integer.BYTES);
        buffer.putInt(ERROR_CODE);
        buffer.flip();
        remainingBytesNumber = 0;
    }


    /**
     * Forms the answer for the request.
     * answerInputStream has the remaining part of answer
     * for the request while buffer has the part of the answer
     * which can be provided for the socket directly.
     */
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

    /*
     * Checks whether pathString is correct or not.
     * If pathString is correct then appropriate file
     * will be returned, otherwise null will be returned.
     */
    @Nullable private File getFileByPath() {
        if (pathString == null) {
            return null;
        }
        File file = new File(pathString);
        return file.exists() ? file : null;
    }

    private void processList() {
        Server.LOGGER.info("Client.processList()");
        File file = getFileByPath();
        if (file == null || !file.isDirectory()) {
            processError();
            return;
        }
        var byteArrayOutputStream = new ByteArrayOutputStream();
        var dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        var childFiles = file.listFiles();
        logFiles(childFiles);
        try {
            dataOutputStream.writeInt(Objects.requireNonNull(childFiles).length);
            for (var childFile : childFiles) {
                dataOutputStream.writeUTF(childFile.getName());
                dataOutputStream.writeBoolean(childFile.isDirectory());
            }
        } catch (IOException ignored) {
        }
        remainingBytesNumber = byteArrayOutputStream.toByteArray().length;
        Server.LOGGER.info("total number of useful bytes = " + remainingBytesNumber);
        answerInputStream = new DataInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        buffer.putInt(remainingBytesNumber);
        buffer.flip();
    }

    private void processGet() {
        Server.LOGGER.info("Client.processGet()");
        File file = getFileByPath();
        if (file == null || file.isDirectory()) {
            processError();
            return;
        }
        try {
            answerInputStream = new DataInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            processError();
            return;
        }
        /*
         * to simplify task we will assume that file is not too long,
         * hence int variable can be used to store file size.
         */
        remainingBytesNumber = (int) file.length();
        Server.LOGGER.info("File size = " + file.length());
        buffer.putInt(remainingBytesNumber);
        buffer.putInt(remainingBytesNumber);
        buffer.flip();
    }

    @NotNull public ByteBuffer getBuffer() {
        return buffer;
    }

    /**
     * if buffer has not any byte for writing to a channel then
     * extra bytes will be moved to the buffer from answer
     * input stream. After that buffer will be in a correct state.
     * Otherwise, nothing will happen.
     */
    public void fillBuffer() {
        if (buffer.hasRemaining()) {
            return;
        }
        buffer.clear();
        while (remainingBytesNumber > 0 && buffer.position() < buffer.limit()) {
            try {
                byte b = answerInputStream.readByte();
                Server.LOGGER.info("byte = " + b + ", remaining bytes number = " + remainingBytesNumber);
                buffer.put(b);
                remainingBytesNumber--;
            } catch (IOException ignore) {
            }
        }
        buffer.flip();
    }

    /**
     * Checks whether there exists information to work with.
     * If there is no such information then true will be returned.
     * Otherwise, false will be returned. Must be executed after
     * {@link ClientData#fillBuffer()}
     *
     * @return true if answer for the request has been provided fully
     * and false otherwise.
     */
    public boolean isFinished() {
        return !buffer.hasRemaining();
    }

    private void logFiles(File[] childFiles) {
        if (childFiles != null) {
            for (var child : childFiles) {
                Server.LOGGER.info(child.getName() + " is dir = " + child.isDirectory());
            }
        }
    }
}
