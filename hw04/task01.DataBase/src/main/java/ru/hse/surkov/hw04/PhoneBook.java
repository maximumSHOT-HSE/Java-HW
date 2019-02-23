package ru.hse.surkov.hw04;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PhoneBook {

    private List<Record> l = new ArrayList<>();

    public void setPhoneBookName(@NotNull String phoneBookName) {
    }

    public void addRecord(@NotNull String name, @NotNull String phoneNumber) {
        l.add(new Record(name, phoneNumber));
    }

    @NotNull public List<String> getPhoneNumbersByName(@NotNull String name) {
        return null;
    }

    @NotNull public List<String> getNamesByPhoneNumber(@NotNull String phoneNumber) {
        return null;
    }

    public void deleteRecord(@NotNull String name, @NotNull String phoneNumber) {
    }

    public void changeName(@NotNull String name, @NotNull String phoneNumber, @NotNull String newName) {
    }

    public void changePhoneNumber(@NotNull String name, @NotNull String phoneNumber, @NotNull String newPhoneNumber) {
    }

    @NotNull public List<Record> getAllRecords() {
        return l;
    }
}
