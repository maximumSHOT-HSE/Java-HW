package ru.hse.hw10.client.gui;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import ru.hse.hw10.client.ServerFile;

// Factory for the files list view
class FileCellFactory implements Callback<ListView<ServerFile>, ListCell<ServerFile>> {
    @Override
    public ListCell<ServerFile> call(ListView<ServerFile> listView) {
        return new FileCell();
    }
}
