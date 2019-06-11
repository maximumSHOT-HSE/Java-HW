package ru.hse.hw10.client;

import org.jetbrains.annotations.NotNull;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/** Implementation of ftp client */
public class Client {
    private static final int BLOCK_SIZE = 4096;
    private final Logger logger = Logger.getLogger("ClientLogger");
    private final InetSocketAddress address;

    /**
     * Creates a client for ftp
     *
     * @param hostname hostname or ip to connect
     * @param port     server port to connect
     * @throws UnknownHostException if hostname is not valid
     */
    public Client(String hostname, int port) throws UnknownHostException {
        this.address = new InetSocketAddress(InetAddress.getByName(hostname), port);
        setupLogger();
    }

    private void setupLogger() {
        if (logger.getHandlers().length == 0) {
            try {
                logger.setUseParentHandlers(false);
                logger.addHandler(new FileHandler("clientLogs"));
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Executes get request. In case of success (i.e. file exists on the server side
     * and there were not any problem with reading) file will be downloaded
     * in the given downloadFilePath. Otherwise nothing will happen.
     *
     * @param sourceFilePath is the file on the server side
     * @param downloadFilePath is the file on the client side
     * @return true in case of success and false otherwise
     */
    public boolean executeGet(@NotNull Path sourceFilePath, @NotNull Path downloadFilePath) {
        try (SocketChannel socketChannel = SocketChannel.open(address)) {
            sendRequest(RequestType.GET_REQUEST, socketChannel, sourceFilePath.toString());
            return receiveGetRequest(socketChannel, downloadFilePath);
        } catch (IOException exception) {
            logger.severe("Unable to execute get request: " + exception.getMessage());
            return false;
        }
    }

    /**
     * Executes list directory request
     *
     * @param path path of the directory to list files from
     * @return list of the files or null if given path associated
     * with nonexistent directory or path not pointing to a directory
     * which can listed
     */
    public List<ServerFile> executeList(@NotNull String path) {
        try (SocketChannel socketChannel = SocketChannel.open(address)) {
            sendRequest(RequestType.LIST_REQUEST, socketChannel, path);
            return receiveListRequest(socketChannel);
        } catch (IOException exception) {
            logger.severe("Unable to execute get request: " + exception.getMessage());
            return new ArrayList<>();
        }
    }

    private int readBytesNumber(@NotNull SocketChannel socketChannel) throws IOException {
        var headBuffer = ByteBuffer.allocate(Integer.BYTES);
        while (headBuffer.position() < headBuffer.limit()) {
            socketChannel.read(headBuffer);
        }
        headBuffer.flip();
        return headBuffer.getInt();
    }

    private void writeBytesFromSocketToStream(@NotNull SocketChannel socketChannel,
                                              @NotNull DataOutputStream dataOutputStream,
                                              int bytesNumber) throws IOException {
        var buffer = ByteBuffer.allocate(BLOCK_SIZE);
        while (bytesNumber > 0) {
            buffer.clear();
            socketChannel.read(buffer);
            buffer.flip();
            while (buffer.hasRemaining()) {
                byte b = buffer.get();
                dataOutputStream.writeByte(b);
                bytesNumber--;
            }
        }
    }

    private boolean receiveGetRequest(@NotNull SocketChannel socketChannel,
                                      @NotNull Path downloadFilePath) throws IOException {
        readBytesNumber(socketChannel);
        int bytesNumber = readBytesNumber(socketChannel);
        if (bytesNumber < 0) {
            return false;
        }
        var fileOutputStream = new FileOutputStream(downloadFilePath.toFile());
        var dataOutputStream = new DataOutputStream(fileOutputStream);
        writeBytesFromSocketToStream(socketChannel, dataOutputStream, bytesNumber);
        return true;
    }

    /*
    * In case of success stream will be returned and null otherwise
    */
    private DataInputStream getResponseStream(SocketChannel socketChannel) throws IOException {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        var dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        int remainingBytesNumber = readBytesNumber(socketChannel);
        dataOutputStream.writeInt(remainingBytesNumber);
        writeBytesFromSocketToStream(socketChannel, dataOutputStream, remainingBytesNumber);
        return new DataInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
    }

    private List<ServerFile> receiveListRequest(SocketChannel socketChannel) throws IOException {
        logger.info("Receive list request");

        var dataInputStream = getResponseStream(socketChannel);
        int bytesNumber = dataInputStream.readInt();
        int size = dataInputStream.readInt();

        logger.info("bytesNumber = " + bytesNumber);
        logger.info("size = " + size);

        if (size < 0) {
            return null;
        }
        List<ServerFile> serverFiles = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            var name = dataInputStream.readUTF();
            boolean isDirectory = dataInputStream.readBoolean();
            serverFiles.add(new ServerFile(name, isDirectory));
        }
        return serverFiles;
    }

    private void sendRequest(RequestType requestType, SocketChannel socketChannel, String path) throws IOException {
        logger.info("Send requestType = " + requestType.toCode() + ", path = " + path);
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        var dataOutputStream = new DataOutputStream(byteOutputStream);

        dataOutputStream.writeByte(requestType.toCode());
        dataOutputStream.writeUTF(path);

        int bytesNumber = byteOutputStream.toByteArray().length;

        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + bytesNumber);
        buffer.putInt(bytesNumber);

        for (var b : byteOutputStream.toByteArray()) {
            buffer.put(b);
        }

        logger.info("path length = " + path.length());
        logger.info("bytes number = " + byteOutputStream.toByteArray().length
                + ", but found = " + bytesNumber);

        buffer.flip();

        while (buffer.hasRemaining()) {
            socketChannel.write(buffer);
        }
    }

    private enum RequestType {
        LIST_REQUEST(1),
        GET_REQUEST(2);

        private final int code;

        RequestType(int code) {
            this.code = code;
        }

        int toCode() {
            return code;
        }
    }
}