package ru.hse.surkov.hw04;

import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.Id;
import xyz.morphia.annotations.Indexed;

@Entity("user")
public class Record {

    @Id private ObjectId id;
    @Indexed @NotNull private String name;
    @Indexed @NotNull private String phoneNumber;

    public Record() {
        name = "name";
        phoneNumber = "phoneNumber";
    }

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
