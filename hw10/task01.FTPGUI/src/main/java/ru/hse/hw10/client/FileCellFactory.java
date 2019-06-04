package ru.hse.hw10.client;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class FileCellFactory implements Callback<ListView<ServerFile>, ListCell<ServerFile>> {
    @Override
    public ListCell<ServerFile> call(ListView<ServerFile> listview) {
        return new FileCell();
    }
}
