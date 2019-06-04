package ru.hse.hw10.client;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientGUI extends Application {
    TextArea logging;
    FakeClient client = new FakeClient();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Create the TextArea
        logging = new TextArea();
        logging.setMaxWidth(300);
        logging.setMaxHeight(150);

        // Create the Labels
        Label filesLabel = new Label("List files: ");

        // Create the Lists for the ListViews
        ObservableList<ServerFile> files = FXCollections.observableArrayList(client.executeList(""));

        // Create the ListView for the seasons
        ListView<ServerFile> filesListView = new ListView<>(files);
        // Set the Orientation of the ListView
        filesListView.setOrientation(Orientation.VERTICAL);
        // Set the Size of the ListView
        filesListView.setPrefSize(120, 100);
        filesListView.setCellFactory(new FileCellFactory());

        // Update the TextArea when the selected season changes
        filesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ServerFile>()
        {
            public void changed(ObservableValue<? extends ServerFile> ov,
                                final ServerFile oldvalue, final ServerFile newvalue)
            {
                fileChanged(ov, oldvalue, newvalue);
            }});


        // Create the Season VBox
        VBox seasonSelection = new VBox();
        // Set Spacing to 10 pixels
        seasonSelection.setSpacing(10);
        // Add the Label and the List to the VBox
        seasonSelection.getChildren().addAll(filesLabel,filesListView);

        // Create the GridPane
        GridPane pane = new GridPane();
        // Set the horizontal and vertical gaps between children
        pane.setHgap(10);
        pane.setVgap(5);
        // Add the Season List at position 0
        pane.addColumn(0, seasonSelection);
        // Add the TextArea at position 2
        pane.addColumn(1, logging);

        // Set the Style-properties of the GridPane
        pane.setStyle("-fx-padding: 10;" +
                              "-fx-border-style: solid inside;" +
                              "-fx-border-width: 2;" +
                              "-fx-border-insets: 5;" +
                              "-fx-border-radius: 5;" +
                              "-fx-border-color: blue;");

        // Create the Scene
        Scene scene = new Scene(pane);
        // Add the Scene to the Stage
        stage.setScene(scene);
        // Set the Title
        stage.setTitle("FTPGUI");
        // Display the Stage
        stage.show();
    }

    // Method to display the Season, which has been changed
    public void fileChanged(ObservableValue<? extends ServerFile> observable, ServerFile oldValue, ServerFile newValue)
    {
        String oldText = oldValue == null ? "null" : oldValue.getName();
        String newText = newValue == null ? "null" : newValue.getName();

        logging.appendText("Season changed: old = " + oldText + ", new = " + newText + "\n");
    }
}
