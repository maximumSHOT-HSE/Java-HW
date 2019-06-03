package ru.hse.hw10.client;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Client {
    private final InetSocketAddress address;

    public Client(String hostname, int port) {
        this.address = new InetSocketAddress(hostname, port);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter hostname: ");
        String hostname = scanner.next();
        System.out.println("Enter port: ");
        int port = scanner.nextInt();

        var client = new Client(hostname, port);

        while (true) {
            int type = scanner.nextInt();
            String path = scanner.nextLine();

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
            }
        }

    }

    private byte[] executeGet(@NotNull String path) {
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
        long size = dataInputStream.readLong();
        if (size < 0) {
            return new byte[0];
        }
        byte[] fileContent = dataInputStream.readAllBytes();
        return fileContent;
    }

    private List<ServerFile> executeList(@NotNull String path) {
        try (SocketChannel socketChannel = SocketChannel.open(address)) {
            sendRequest(RequestType.LIST_REQUEST, socketChannel, path);
            return receiveListRequest(socketChannel);
        } catch (IOException exception) {
            throw new RuntimeException("AAA");
        }
    }

    private DataInputStream getResponseStream(SocketChannel socketChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        while (socketChannel.read(buffer) != -1) { // TODO check if this works
            stream.writeBytes(buffer.array());
        }

        return new DataInputStream(new ByteArrayInputStream(stream.toByteArray()));
    }

    private List<ServerFile> receiveListRequest(SocketChannel socketChannel) throws IOException {
        var dataInputStream = getResponseStream(socketChannel);
        int bytesNumber = dataInputStream.readInt(); // TODO check this
        int size = dataInputStream.readInt();

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
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        var dataOutputStream = new DataOutputStream(byteOutputStream);
        dataOutputStream.writeInt(Integer.BYTES + path.length() * Character.BYTES);
        dataOutputStream.writeByte(requestType.toCode());
        dataOutputStream.writeUTF(path);
        ByteBuffer buffer = ByteBuffer.wrap(byteOutputStream.toByteArray());

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
