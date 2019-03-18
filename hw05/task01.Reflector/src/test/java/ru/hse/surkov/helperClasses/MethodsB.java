package ru.hse.surkov.helperClasses;

import java.io.IOException;
import java.util.NoSuchElementException;

public class MethodsB {

    private static int f() {
        return 1;
    }

    public int f1() {
        return 1;
    }

    public <T> T h() {
        return null;
    }

    public <B> B h2() {
        return null;
    }

    public void t() throws IOException {

    }

    public void t2() throws IOException, NoSuchElementException {

    }

    public void arguments(int x, int y) {

    }

    public void arguments1(int x, int y, int z) {

    }

    private void bbb() {

    }
}
