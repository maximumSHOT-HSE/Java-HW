package ru.hse.hw10.client;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FakeClient {
    byte[] executeGet(@NotNull String path) {
        return "String".getBytes();
    }


    List<ServerFile> executeList(@NotNull String path) {
        List<ServerFile> serverFiles = new ArrayList<>();
        serverFiles.add(new ServerFile("dir", true));
        serverFiles.add(new ServerFile("file1", false));
        serverFiles.add(new ServerFile("file2", false));
        return serverFiles;
    }
}
