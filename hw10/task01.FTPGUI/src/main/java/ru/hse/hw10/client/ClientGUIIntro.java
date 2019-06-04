package ru.hse.hw10.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ClientGUIIntro extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        var pane = new GridPane();
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

        var button = new Button("Connect");
        GridPane.setConstraints(button, 0, 3);
        pane.getChildren().add(button);

        button.setOnAction((e) -> {
            String ip = ipTextField.getText();
            String port = portTextField.getText();

            var clientGUI = new ClientGUI(primaryStage, ip, port);
        });

        Scene scene = new Scene(pane);
        primaryStage.setTitle("FTPGUI");
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(200);
        primaryStage.setMinWidth(200);
        primaryStage.show();
    }
}
