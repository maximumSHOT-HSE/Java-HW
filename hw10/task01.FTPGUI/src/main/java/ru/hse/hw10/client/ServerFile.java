package ru.hse.hw10.client;

import org.jetbrains.annotations.NotNull;

/** Representation of a file stored on a server */
public class ServerFile {
    @NotNull private final String name;
    private final boolean isDirectory;

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

    @Override
    public String toString() {
        return (isDirectory ? "Dir: " : "File: ") + name;
    }
}
