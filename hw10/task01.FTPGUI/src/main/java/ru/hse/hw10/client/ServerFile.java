package ru.hse.hw10.client;

import org.jetbrains.annotations.NotNull;

public class ServerFile {

    @NotNull private final String name;
    @NotNull private final String path;
    private final boolean isDirectory;

    public ServerFile(@NotNull String path, boolean isDirectory) {
        int index = path.lastIndexOf("/");
        this.name = path.substring(index + 1);
        System.out.println(name);
        this.path = path;
        this.isDirectory = isDirectory;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getPath() {
        return path;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public String toString() {
        return (isDirectory ? "Dir: " : "File: ") + name;
    }
}
