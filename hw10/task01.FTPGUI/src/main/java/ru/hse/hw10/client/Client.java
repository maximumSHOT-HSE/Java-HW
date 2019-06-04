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
import java.util.logging.Logger;

public class Client {

    private static final int BLOCK_SIZE = 4096;

    private final InetSocketAddress address;

    public Client(String hostname, int port) throws UnknownHostException {
        this.address = new InetSocketAddress(InetAddress.getByName(hostname), port);
    }

    public Client() throws UnknownHostException {
        this.address = new InetSocketAddress(InetAddress.getLocalHost(), 9999);
    }

    public static void main(String[] args) throws UnknownHostException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter hostname: ");
        String hostname = scanner.next();
        System.out.println("Enter port: ");
        int port = scanner.nextInt();
        var client = new Client(hostname, port);

        System.out.println(Arrays.toString(InetAddress.getLocalHost().getAddress()));

        while (true) {
            int type = scanner.nextInt();
            String path = scanner.nextLine().strip();

            System.out.println("type = " + type + ", path = " + path);

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
            }
        }
    }

    public byte[] executeGet(@NotNull String path) {
        try (SocketChannel socketChannel = SocketChannel.open(address)) {
            sendRequest(RequestType.GET_REQUEST, socketChannel, path);
            return receiveGetRequest(socketChannel);
        } catch (IOException exception) {
            throw new RuntimeException("AAA");
        }
    }

    private byte[] receiveGetRequest(SocketChannel socketChannel) throws IOException {
        var dataInputStream = getResponseStream(socketChannel);
        int bytesNumber = dataInputStream.readInt(); // TODO
        int size = dataInputStream.readInt();
        if (size < 0) {
            return new byte[0];
        }
        return dataInputStream.readAllBytes();
    }

    public List<ServerFile> executeList(@NotNull String path) {
        try (SocketChannel socketChannel = SocketChannel.open(address)) {
            sendRequest(RequestType.LIST_REQUEST, socketChannel, path);
            return receiveListRequest(socketChannel);
        } catch (IOException exception) {
            throw new RuntimeException("AAA");
        }
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

        System.out.println("DONE! rem bytes number = " + remainingBytesNumber + ", head = " + Arrays.toString(headBuffer.array()));

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

        System.out.println("Receive list request!!!");

        var dataInputStream = getResponseStream(socketChannel);
        int bytesNumber = dataInputStream.readInt();
        int size = dataInputStream.readInt();

        System.out.println("bytesNumber = " + bytesNumber);
        System.out.println("size = " + size);

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
        System.out.println("Send requestType = " + requestType.toCode() + ", path = " + path);
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

        System.out.println("path length = " + path.length());
        System.out.println("bytes number = " + byteOutputStream.toByteArray().length
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
