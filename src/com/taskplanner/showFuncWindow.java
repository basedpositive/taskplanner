package com.taskplanner;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;

public class showFuncWindow {
    private Connection connection;

    public showFuncWindow(Connection connection) {
        DatabaseConnector db = new DatabaseConnector();

        Stage stage = new Stage();
        stage.setTitle("Task Manager");

        VBox layout = new VBox();
        layout.setSpacing(10);

        Scene signInScene = new Scene(layout, 516, 516);
        stage.setScene(signInScene);

        Label label = new Label("Task Manager");
        label.setLayoutX(12);
        label.setLayoutY(10);

        Button createTaskButton = new Button("Создать задачу");
        Button viewTaskButton = new Button("Посмотреть задачи");
        // Button signInButton = new Button("SIGN UP");

        Group buttonGroup = new Group(createTaskButton, viewTaskButton); // signInButton

        createTaskButton.setLayoutX(90);
        createTaskButton.setLayoutY(320);

        viewTaskButton.setLayoutX(290);
        viewTaskButton.setLayoutY(320);

        /* signInButton.setLayoutY(158); signInButton.setLayoutX(218); */

        Group group = new Group(buttonGroup);

        createTaskButton.setOnAction(e -> new showCreateTaskDialog(connection));
        viewTaskButton.setOnAction(e -> new showViewTaskDialog(connection));

        // signInButton.setOnAction(e -> new showSignInTaskDialog(connect));

        Group testGroup = new Group(label, group);
        layout.getChildren().addAll(testGroup);
        stage.show();
    }
}
