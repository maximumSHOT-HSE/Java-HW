package ru.hse.surkov.hw02;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface, that provides the contracts
 * (methods) for serializability:
 * object state conversion
 * into a sequence of bytes
 * and Vice versa.
 * */
public interface Serializable {

    /**
     * Transfers representative of the class to the output stream.
     * In case of out == null nothing will happen.
     * @throws IOException if there is IO problems during transferring
     * */
    void serialize(OutputStream out) throws IOException;
    
    /**
     * Replaces current representative of the class with data from input stream.
     * In case of input stream is null nothing will happen.
     * @throws IOException if there is IO problems during retrieving
     * */
    void deserialize(InputStream in) throws IOException;
}
