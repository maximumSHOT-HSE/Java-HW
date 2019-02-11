package ru.hse.surkov.hw02;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Serializable {

    /**
     * Transfers representative of the class to the output stream
     * @throws IOException if there is IO problems during transferring
     * */
    void serialize(OutputStream out) throws IOException;


    /**
     * Replaces current representative of the class with data from input stream
     * @throws IOException if there is IO problems during retrieving
     * */
    void deserialize(InputStream in) throws IOException;
}
