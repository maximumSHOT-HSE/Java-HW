package ru.hse.hw10.client;

import org.jetbrains.annotations.NotNull;

public class ServerFile {

    @NotNull private String name;
    private boolean isDirectory;

    public ServerFile(@NotNull String name, boolean isDirectory) {
        this.name = name;
        this.isDirectory = isDirectory;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public boolean isDirectory() {
        return isDirectory;
    }
}