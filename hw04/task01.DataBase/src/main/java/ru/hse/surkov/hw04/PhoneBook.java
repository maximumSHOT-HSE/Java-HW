package ru.hse.surkov.hw04;

import com.mongodb.MongoClient;
import org.jetbrains.annotations.NotNull;
import xyz.morphia.Datastore;
import xyz.morphia.Morphia;
import xyz.morphia.query.Query;
import xyz.morphia.query.UpdateOperations;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Data structure developed as
 * a shell with user friendly interface,
 * which encapsulates work related with
 * storing {@link Record}.
 * Two records are equal if and only if
 * their names and phone numbers are equal.
 * Data structure does not support nulls
 * as name or phone number values.
 * */
public class PhoneBook {

    @NotNull private final Datastore datastore;

    /*
    * Initializing data base by its name.
    * If such data base has already been,
    * then no new copy will be created.
    * Otherwise, new instance will be created
    * and will be initialized with given phone
    * book name. Second argument is the flag which
    * indicates whether to clear data base
    * during phone book initialization or not.
     * */
    public PhoneBook(@NotNull String phoneBookName, boolean clearDataBase) {
        Morphia morphia = new Morphia();
        morphia.mapPackage("ru.hse.surkov.hw04");
        datastore = morphia.createDatastore(new MongoClient(), phoneBookName);
        if (clearDataBase) {
            this.clearDataBase();
        }
        datastore.ensureIndexes();
    }

    // Constructs query for finding Record in data base by name and phone number
    @NotNull private Query<Record> getFindQuery(@NotNull String name, @NotNull String phoneNumber) {
        Query<Record> query = datastore.createQuery(Record.class);
        query.and(
                query.criteria("name").equal(name),
                query.criteria("phoneNumber").equal(phoneNumber)
        );
        return query;
    }

    /** Method checks if there exists record with given name and phone numbers */
    public boolean contains(@NotNull String name, @NotNull String phoneNumber) {
        return !getFindQuery(name, phoneNumber).asList().isEmpty();
    }

    /**
     * Method checks if there exists record with given number and if there was no
     * such record then it will be created and added to the data base.
     * @return true if there was no such record and new record has been created successfully,
     * otherwise false will be returned.
     * */
    public boolean addRecord(@NotNull String name, @NotNull String phoneNumber) {
        if (contains(name, phoneNumber)) {
            return false;
        }
        Record record = new Record(name, phoneNumber);
        datastore.save(record);
        return true;
    }

    /**
     * Method finds all records with given name
     * @return List of retrieved records
     * */
    @NotNull public List<Record> getPhoneNumbersByName(@NotNull String name) {
        return datastore
                .createQuery(Record.class)
                .field("name")
                .equal(name)
                .asList();
    }

    /**
     * Method finds all records with given phone number
     * @return List of retrieved records
     * */
    @NotNull public List<Record> getNamesByPhoneNumber(@NotNull String phoneNumber) {
        return datastore
                .createQuery(Record.class)
                .field("phoneNumber")
                .equal(phoneNumber)
                .asList();
    }

    /**
     * Method checks if there exists record with given number and if there was
     * such record then it will be deleted from data base.
     * @return true if there was such record and it has been deleted successfully,
     * otherwise false will be returned.
     * */
    public boolean deleteRecord(@NotNull String name, @NotNull String phoneNumber) {
        if (!contains(name, phoneNumber)) {
            return false;
        }
        datastore.delete(getFindQuery(name, phoneNumber));
        return true;
    }

    /*
    * Method checks if there exists record with given
    * name and phoneNumber and if there is no such record then
    * NoSuchElementException will be thrown.
    * Otherwise method will return true if there is no record with
    * newName and newPhoneNumber and false otherwise.
    * */
    private boolean isValid(@NotNull String name, @NotNull String phoneNumber, @NotNull String newName, @NotNull String newPhoneNumber) throws NoSuchElementException {
        if (!contains(name, phoneNumber)) {
            throw new NoSuchElementException("Record (" + name + ", " + phoneNumber + ") does not exists in data base");
        }
        return !contains(newName, newPhoneNumber);
    }

    /**
     * Method checks if there exists record with given number and if there was
     * such record then it's name will be updated and changes will be stored in data base.
     * @return true if there was such record and it has been updated successfully,
     * otherwise false will be returned.
     * @throws NoSuchElementException if there was no record in data base to modify
     * */
    public boolean changeName(@NotNull String name, @NotNull String phoneNumber, @NotNull String newName) throws NoSuchElementException {
        if (!isValid(name, phoneNumber, newName, phoneNumber)) {
            return false;
        }
        UpdateOperations<Record> updateOperations = datastore
                .createUpdateOperations(Record.class)
                .set("name", newName);
        datastore.update(getFindQuery(name, phoneNumber), updateOperations);
        return true;
    }

    /**
     * Method checks if there exists record with given number and if there was
     * such record then it's phone number will be updated and changes will be stored in data base.
     * @return true if there was such record and it has been updated successfully,
     * otherwise false will be returned.
     * @throws NoSuchElementException if there was no record in data base to modify
     * */
    public boolean changePhoneNumber(@NotNull String name, @NotNull String phoneNumber, @NotNull String newPhoneNumber) throws NoSuchElementException {
        if (!isValid(name, phoneNumber, name, newPhoneNumber)) {
            return false;
        }
        UpdateOperations<Record> updateOperations = datastore
                .createUpdateOperations(Record.class)
                .set("phoneNumber", newPhoneNumber);
        datastore.update(getFindQuery(name, phoneNumber), updateOperations);
        return true;
    }

    /**
     * Method finds all records in data base
     * @return List of retrieved records
     * */
    @NotNull public List<Record> getAllRecords() {
        return datastore.createQuery(Record.class).asList();
    }

    // Delete all records from data base (package private for testing)
    void clearDataBase() {
        datastore.getDB().dropDatabase();
    }
}
