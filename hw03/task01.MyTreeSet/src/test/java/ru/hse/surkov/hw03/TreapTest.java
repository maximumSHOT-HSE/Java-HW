package ru.hse.surkov.hw03;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class TreapTest {

    MyTreeSet<Integer> set;

    @BeforeEach
    void setUp() {
        set = new Treap<>();
    }


}