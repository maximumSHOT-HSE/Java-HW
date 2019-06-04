package ru.hse.hw10.client;

import javafx.scene.control.ListCell;

public class FileCell extends ListCell<ServerFile> {

    @Override
    public void updateItem(ServerFile item, boolean empty) {
        super.updateItem(item, empty);

        int index = this.getIndex();
        String representation = null;

        if (item != null && !empty) {

        }

        if (item == null || empty) {
        } else {
            representation = item.toString();
        }

        this.setText(representation);
        setGraphic(null);
    }
}
