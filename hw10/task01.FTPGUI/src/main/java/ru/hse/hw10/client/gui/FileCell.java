package ru.hse.hw10.client.gui;

import javafx.scene.control.ListCell;
import ru.hse.hw10.client.ServerFile;

// Representation of the file in the list view
class FileCell extends ListCell<ServerFile> {
    @Override
    public void updateItem(ServerFile item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            return;
        }
        this.setText(item.toString());
        setGraphic(null);
    }
}
