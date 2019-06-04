package ru.hse.hw10.client;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
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

    /** Starts console client */
    public static void main(String[] args) throws UnknownHostException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter hostname: ");
        String hostname = scanner.next();
        System.out.println("Enter port: ");
        int port = scanner.nextInt();
        var client = new Client(hostname, port);

        while (true) {
            int type = scanner.nextInt();
            String path = scanner.nextLine().strip();

            if (type == RequestType.LIST_REQUEST.toCode()) {
                var list = client.executeList(path);
                System.out.println("List: size = " + list.size());
                for (var serverFile : list) {
                    System.out.println(serverFile.getName() + " | " + (serverFile.isDirectory() ? "d" : "f"));
                }
                continue;
            }
            if (type == RequestType.GET_REQUEST.toCode()) {
                var file = client.executeGet(path);
                System.out.println("Get: size = " + file.length);
                System.out.println(Arrays.toString(file));
                System.out.println(new String(file));
                continue;
            }
            if (type == 0) {
                break;
            }
        }
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
     * Executes get file by specified path request
     *
     * @param path path to get file from
     * @return file content represented by bytes
     */
    public byte[] executeGet(@NotNull String path) {
        try (SocketChannel socketChannel = SocketChannel.open(address)) {
            sendRequest(RequestType.GET_REQUEST, socketChannel, path);
            return receiveGetRequest(socketChannel);
        } catch (IOException exception) {
            logger.severe("Unable to execute get request: " + exception.getMessage());
            return new byte[0];
        }
    }

    /**
     * Executes list directory request
     *
     * @param path path of the directory to list files from
     * @return list of the files
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

    private byte[] receiveGetRequest(SocketChannel socketChannel) throws IOException {
        var dataInputStream = getResponseStream(socketChannel);
        int bytesNumber = dataInputStream.readInt();
        int size = dataInputStream.readInt();
        if (size < 0) {
            return new byte[0];
        }
        byte[] fileContent = dataInputStream.readAllBytes();
        if (Integer.BYTES + fileContent.length != bytesNumber) {
            throw new IOException("Corrupted package");
        }
        return fileContent;
    }

    private DataInputStream getResponseStream(SocketChannel socketChannel) throws IOException {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        var dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        var headBuffer = ByteBuffer.allocate(Integer.BYTES);
        var bodyBuffer = ByteBuffer.allocate(BLOCK_SIZE);

        while (headBuffer.position() < headBuffer.limit()) {
            socketChannel.read(headBuffer);
        }

        headBuffer.flip();
        int remainingBytesNumber = headBuffer.getInt();

        logger.info("rem bytes number = " + remainingBytesNumber + ", head = " + Arrays.toString(headBuffer.array()));

        dataOutputStream.writeInt(remainingBytesNumber);
        while (remainingBytesNumber > 0) {
            bodyBuffer.clear();
            socketChannel.read(bodyBuffer);
            bodyBuffer.flip();
            while (bodyBuffer.hasRemaining()) {
                int b = bodyBuffer.get();
                dataOutputStream.writeByte(b);
                remainingBytesNumber--;
            }
        }
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
            return new ArrayList<>();
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
