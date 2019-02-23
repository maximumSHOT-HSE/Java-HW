package ru.hse.surkov.hw04;

import com.mongodb.MongoClient;
import org.jetbrains.annotations.NotNull;
import xyz.morphia.Datastore;
import xyz.morphia.Morphia;
import xyz.morphia.query.Query;
import xyz.morphia.query.UpdateOperations;

import java.util.List;

public class PhoneBook {

    @NotNull private Morphia morphia = new Morphia();
    @NotNull private final Datastore datastore;

    public PhoneBook(@NotNull String phoneBookName) {
        morphia.mapPackage(Record.class.getPackageName());
        datastore = morphia.createDatastore(new MongoClient(), phoneBookName);
        datastore.ensureIndexes();
    }

    @NotNull private Query<Record> getFindQuery(@NotNull String name, @NotNull String phoneNumber) {
        Query<Record> query = datastore.createQuery(Record.class);
        query.and(
                query.criteria("name").equal(name),
                query.criteria("phoneNumber").equal(phoneNumber)
        );
        return query;
    }

    public boolean contains(@NotNull String name, @NotNull String phoneNumber) {
        return !getFindQuery(name, phoneNumber).asList().isEmpty();
    }

    public boolean addRecord(@NotNull String name, @NotNull String phoneNumber) {
        if (contains(name, phoneNumber)) {
            return false;
        }
        Record record = new Record(name, phoneNumber);
        datastore.save(record);
        return true;
    }

    @NotNull public List<Record> getPhoneNumbersByName(@NotNull String name) {
        return datastore
                .createQuery(Record.class)
                .field("name")
                .equal(name)
                .asList();
    }

    @NotNull public List<Record> getNamesByPhoneNumber(@NotNull String phoneNumber) {
        return datastore
                .createQuery(Record.class)
                .field("phoneNumber")
                .equal(phoneNumber)
                .asList();
    }

    public boolean deleteRecord(@NotNull String name, @NotNull String phoneNumber) {
        if (!contains(name, phoneNumber)) {
            return false;
        }
        datastore.delete(getFindQuery(name, phoneNumber));
        return true;
    }

    public boolean changeName(@NotNull String name, @NotNull String phoneNumber, @NotNull String newName) {
        if (contains(newName, phoneNumber)) {
            return false;
        }
        UpdateOperations<Record> updateOperations = datastore
                .createUpdateOperations(Record.class)
                .set("name", newName);
        datastore.update(getFindQuery(name, phoneNumber), updateOperations);
        return true;
    }

    public boolean changePhoneNumber(@NotNull String name, @NotNull String phoneNumber, @NotNull String newPhoneNumber) {
        if (contains(name, newPhoneNumber)) {
            return false;
        }
        UpdateOperations<Record> updateOperations = datastore
                .createUpdateOperations(Record.class)
                .set("phoneNumber", newPhoneNumber);
        datastore.update(getFindQuery(name, phoneNumber), updateOperations);
        return true;
    }

    @NotNull public List<Record> getAllRecords() {
        return datastore.createQuery(Record.class).asList();
    }
}
