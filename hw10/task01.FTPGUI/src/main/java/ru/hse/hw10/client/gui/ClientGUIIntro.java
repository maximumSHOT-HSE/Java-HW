package ru.hse.hw10.client.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/** Start activity of ftp client that asks of the server ip and port */
public class ClientGUIIntro extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        final var pane = new GridPane();

        final var ipLabel = new Label("Enter server IP: ");
        GridPane.setConstraints(ipLabel, 0, 1);
        pane.getChildren().add(ipLabel);

        final var ipTextField = new TextField();
        GridPane.setConstraints(ipTextField, 1, 1);
        pane.getChildren().add(ipTextField);

        final var portLabel = new Label("Enter port: ");
        GridPane.setConstraints(portLabel, 0, 2);
        pane.getChildren().add(portLabel);

        final var portTextField = new TextField();
        GridPane.setConstraints(portTextField, 1, 2);
        pane.getChildren().add(portTextField);

        final var connectButton = new Button("Connect");
        GridPane.setConstraints(connectButton, 0, 3);
        pane.getChildren().add(connectButton);

        connectButton.setOnAction((e) -> {
            String ip = ipTextField.getText();
            String port = portTextField.getText();

            var clientGUI = new ClientGUI(primaryStage, ip, port);
        });

        Scene scene = new Scene(pane);
        primaryStage.setTitle("FTP GUI");
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(125);
        primaryStage.setMinWidth(300);
        primaryStage.show();
    }
}
