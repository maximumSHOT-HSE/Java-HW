package ru.hse.hw10.client;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class ClientGUI {
    private Client client;
    private ObservableList<ServerFile> files;
    private Button downloadButton = new Button("Download");
    private Button enterButton = new Button("Enter");
    private Button backButton = new Button("Back");
    private ServerFile selectedFile;
    private Label filesLabel;
    private LinkedList<ServerFile> directoryPath = new LinkedList<>();
    private Path currentPath;

    public ClientGUI(Stage stage, String ip, String port) throws UnknownHostException {
        int intPort;
        try {
            intPort = Integer.valueOf(port);
            if (intPort >= 65536 || intPort < 0) {
                throw new RuntimeException("wrong port");
            }
        } catch (NumberFormatException exception) {
            throw new RuntimeException("number format exc");
        }
        client = new Client();

        files = FXCollections.observableArrayList(client.executeList("."));
        currentPath = Paths.get("");
        directoryPath.add(new ServerFile("", true));
        filesLabel = new Label("Current dir: \"\"");

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
        backButton.setOnAction(event -> onBackButtonClicked());

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

    public void fileChanged(ObservableValue<? extends ServerFile> observable, @Nullable ServerFile oldValue, @Nullable ServerFile newValue) {
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
        List<ServerFile> result = client.executeList(path); // TODO threadpool
        filesLabel.setText("Current dir: " + path);
        directoryPath.add(selectedFile);
        files.setAll(result);
    }

    private void onDownloadButtonClicked() {
        Thread thread = new Thread(() -> {
            Path path = currentPath.resolve(selectedFile.getName());
            System.out.println("Path " + path.toString());
            byte[] result = client.executeGet(path.toString());
            try {
                if (path.getParent() != null) {
                    Files.createDirectories(path.getParent());
                }
                if (Files.exists(path)) {
                    Files.delete(path);
                }
                Files.createFile(path);

                Files.write(path, result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private void onBackButtonClicked() {
        if (currentPath.getParent() == null) {
            return; // TODO
        }
        directoryPath.pollLast();
        currentPath = currentPath.getParent();
       // String path = directoryPath.peekLast().getPath();
        filesLabel.setText("Current dir: " + currentPath.toString());
        files.setAll(client.executeList(currentPath.toString()));
    }
}
