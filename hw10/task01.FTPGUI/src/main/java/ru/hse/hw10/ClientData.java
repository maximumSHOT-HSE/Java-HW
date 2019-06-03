package ru.hse.hw10;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

    @NotNull private List<Byte> request = new ArrayList<>();

    public void append(byte b) {
        request.add(b);
    }

    public boolean isFull() {
        // TODO
        return true;
    }

    public List<Byte> getRequest() {
        return request;
    }
}
