package ru.hse.surkov.hw04;

import org.jetbrains.annotations.NotNull;

public class Record {

    @NotNull private String name;
    @NotNull private String phoneNumber;

    public Record(@NotNull String name, @NotNull String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    @NotNull public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@NotNull String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "(name: " + name + ", phone: " + phoneNumber + ")";
    }
}
