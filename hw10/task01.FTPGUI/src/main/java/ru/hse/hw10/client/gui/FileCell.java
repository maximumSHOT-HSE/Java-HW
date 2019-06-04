package ru.hse.hw10.client.gui;

import javafx.scene.control.ListCell;
import ru.hse.hw10.client.ServerFile;

// Representation of file cell in list view
class FileCell extends ListCell<ServerFile> {

    @Override
    public void updateItem(ServerFile item, boolean empty) {
        super.updateItem(item, empty);
        String representation = null;

        if (item != null && !empty) {
            representation = item.toString();
        }

        this.setText(representation);
        setGraphic(null);
    }
}
