package ru.hse.hw10.client.gui;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hse.hw10.client.Client;
import ru.hse.hw10.client.ServerFile;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Activity for walking the file tree and downloading files */
public class ClientGUI {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Button downloadButton = new Button("Download");
    private final Button enterButton = new Button("Enter");
    private final Button backButton = new Button("Back");
    private ObservableList<ServerFile> files;
    private Label filesLabel;
    private ListView<ServerFile> filesListView;
    private Client client;
    private ServerFile selectedFile;
    private Path currentPath;

    public ClientGUI(@NotNull Stage stage, @NotNull String ip, @NotNull String port) {
        try {
            client = getClient(ip, port);
        } catch (UnknownHostException | IllegalArgumentException exception) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error occurred while connecting to client");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
            return;
        }

        files = FXCollections.observableArrayList(client.executeList("."));
        currentPath = Paths.get(".");
        filesLabel = new Label("Current dir: \"\"");

        filesListView = new ListView<>(files);
        filesListView.setOrientation(Orientation.VERTICAL);
        filesListView.setPrefSize(200, 200);
        filesListView.setCellFactory(new FileCellFactory());
        filesListView.getSelectionModel().selectedItemProperty().addListener(this::fileChanged);

        VBox fileSelection = new VBox();
        fileSelection.setSpacing(10);
        fileSelection.getChildren().addAll(filesLabel, filesListView);

        downloadButton.setDisable(true);
        enterButton.setDisable(true);
        backButton.setDisable(true);

        enterButton.setOnAction(event -> onEnterButtonClicked());
        downloadButton.setOnAction(event -> onDownloadButtonClicked());
        backButton.setOnAction(event -> onBackButtonClicked());

        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(5);
        pane.addColumn(0, fileSelection);
        pane.addColumn(1, downloadButton);
        pane.addColumn(2, enterButton);
        pane.addColumn(3, backButton);

        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setTitle("FTPGUI");
        stage.show();
    }

    private Client getClient(@NotNull String ip, @NotNull String port) throws UnknownHostException {
        int portAsInt;
        try {
            portAsInt = Integer.valueOf(port);
            if (portAsInt >= 65536 || portAsInt < 0) {
                throw new IllegalArgumentException("port should be a number between 0 and 65536");
            }
        } catch (NumberFormatException exception) {
            throw new IllegalAccessError("port should be a number between 0 and 65536");
        }
        return new Client(ip, portAsInt);
    }

    private void fileChanged(ObservableValue<? extends ServerFile> observable, @Nullable ServerFile oldValue, @Nullable ServerFile newValue) {
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
        selectedFile = newValue;
    }

    private void onEnterButtonClicked() {
        assert selectedFile.isDirectory();

        currentPath = currentPath.resolve(selectedFile.getName());
        String path = currentPath.toString();
        List<ServerFile> result = client.executeList(path);
        filesLabel.setText("Current dir: " + path);
        backButton.setDisable(false);
        files.setAll(result);
    }

    private void onDownloadButtonClicked() {
        assert !selectedFile.isDirectory();

        executor.submit(() -> {
            Path path = currentPath.resolve(selectedFile.getName());
            byte[] fileBytes = client.executeGet(path.toString());
            try {
                if (path.getParent() != null) {
                    Files.createDirectories(path.getParent());
                }
                if (Files.exists(path)) {
                    Files.delete(path);
                }
                Files.createFile(path);
                Files.write(path, fileBytes);
            } catch (IOException exception) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error while downloading the file");
                    alert.setContentText(exception.getMessage());
                    alert.showAndWait();
                });
            }
        });
    }

    private void onBackButtonClicked() {
        assert currentPath != null && currentPath.getParent() != null;

        currentPath = currentPath.getParent();
        if (currentPath.getParent() == null) {
            backButton.setDisable(true);
        }

        filesLabel.setText("Current dir: " + currentPath.toString());
        files.setAll(client.executeList(currentPath.toString()));
    }
}
