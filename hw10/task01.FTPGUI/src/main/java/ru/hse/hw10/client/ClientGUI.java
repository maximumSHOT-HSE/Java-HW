package ru.hse.hw10.client;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ClientGUI extends Application {
    private FakeClient client = new FakeClient();
    private ObservableList<ServerFile> files;
    private Button downloadButton = new Button("Download");
    private Button enterButton = new Button("Enter");
    private Button backButton = new Button("Back");
    private ServerFile currentFile;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label filesLabel = new Label("List files: ");

        files = FXCollections.observableArrayList(client.executeList(""));

        ListView<ServerFile> filesListView = new ListView<>(files);
        filesListView.setOrientation(Orientation.VERTICAL);
        filesListView.setPrefSize(200, 200);
        filesListView.setCellFactory(new FileCellFactory());

        filesListView.getSelectionModel().selectedItemProperty().addListener(this::fileChanged);

        VBox fileSelection = new VBox();
        fileSelection.setSpacing(10);
        fileSelection.getChildren().addAll(filesLabel, filesListView);

        downloadButton.setDisable(true);
        enterButton.setDisable(true);

        enterButton.setOnAction(event -> onEnterButtonClicked());
        downloadButton.setOnAction(event -> onDownloadButtonClicked());

        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(5);
        pane.addColumn(0, fileSelection);
        pane.addColumn(1, downloadButton);
        pane.addColumn(2, enterButton);
        pane.addColumn(3, backButton);

        pane.setStyle("-fx-padding: 10;" +
                              "-fx-border-style: solid inside;" +
                              "-fx-border-width: 2;" +
                              "-fx-border-insets: 5;" +
                              "-fx-border-radius: 5;" +
                              "-fx-border-color: blue;");

        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setTitle("FTPGUI");
        stage.show();
    }

    public void fileChanged(ObservableValue<? extends ServerFile> observable, ServerFile oldValue, ServerFile newValue) {
        if (newValue == null) {
            return;
        }
        if (newValue.isDirectory()) {
            downloadButton.setDisable(true);
            enterButton.setDisable(false);
        } else {
            downloadButton.setDisable(false);
            enterButton.setDisable(true);
        }
        currentFile = newValue;
    }

    private void onEnterButtonClicked() {
        assert currentFile.isDirectory();

        List<ServerFile> result = client.executeList(currentFile.getPath());
        files.setAll(result);
    }

    private void onDownloadButtonClicked() {
        Thread thread = new Thread(() -> {
            Path path = Paths.get(currentFile.getPath());
            byte[] result = client.executeGet(currentFile.getPath());
            try {
                Files.write(path, result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}
