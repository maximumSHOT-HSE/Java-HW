package ru.hse.surkov.hw04;

import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.Id;

/**
 * Data structure, which consists of two fields:
 * user name and user's phone number. Record designed for
 * storing information in mongodb (morphia)
 */
@Entity
public class Record {

    @Id private ObjectId id;
    @NotNull private String name;
    @NotNull private String phoneNumber;

    // Morphia needs an explicit default constructor
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

    @NotNull public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "(name: " + name + ", phone: " + phoneNumber + ")";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;
        if (obj instanceof Record) {
            Record otherRecord = (Record) obj;
            result = otherRecord.canEqual(this) &&
                    getName().equals(otherRecord.getName()) &&
                    getPhoneNumber().equals(otherRecord.getPhoneNumber());
        }
        return result;
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ phoneNumber.hashCode();
    }

    public boolean canEqual(@Nullable Object other) {
        return (other instanceof Record);
    }
}
