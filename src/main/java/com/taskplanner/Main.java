package com.taskplanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.text.Text;
import java.sql.*;

public class Main extends Application{

    public static void main(String[] args) {
        // DatabaseConnector db = new DatabaseConnector();
        // Connection connect = db.connect_to_db("schema", "postgres", "#SHKM277");
        // db.createTable(connect, "task");
        // db.insert_row(connect,"task","","test length",true);
        // db.update_title(connect,"task","Some title","Car");
        // db.update_description(connect,"task","Some description","wash");
        // db.delete_row_by_id(connect, "task", 14);
        // db.read_data(connect,"task");

        launch(args);
    }

    @Override
    public void start(Stage signInStage) {
        DatabaseConnector db = new DatabaseConnector();
        final Connection connect = db.connect_to_db("schema", "postgres", "#SHKM277");

        Label labelLogin = new Label("SIGN IN");
        labelLogin.setLayoutX(12);
        labelLogin.setLayoutY(10);

        Text textUsername = new Text("Username");
        textUsername.setLayoutX(100);
        textUsername.setLayoutY(100);
        TextField usernameField = new TextField();
        usernameField.setLayoutX(180);
        usernameField.setLayoutY(85);

        Text textPassword = new Text("Password");
        textPassword.setLayoutX(100);
        textPassword.setLayoutY(160);
        PasswordField passwordField = new PasswordField();
        passwordField.setLayoutX(180);
        passwordField.setLayoutY(145);

        Button signInButton = new Button("SIGN IN");
        signInButton.setLayoutX(180);
        signInButton.setLayoutY(190);

        Label signUpLabel = new Label("Регистрация.");
        signUpLabel.setLayoutX(180);
        signUpLabel.setLayoutY(220);
        signUpLabel.setOnMouseClicked(e -> new openSignUpWindow(connect));

        signInButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            try {
                String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

                try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {

                            signInStage.close();
                            new showFuncWindow(connect);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Ошибка входа");
                            alert.setHeaderText("Неверное имя пользователя или пароль.");
                            alert.showAndWait();
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        Group fieldGroup = new Group(labelLogin, textUsername, usernameField, textPassword, passwordField, signInButton, signUpLabel);
        Group group = new Group(fieldGroup);

        Scene scene = new Scene(group);
        signInStage.setScene(scene);
        signInStage.setTitle("Task Manager");
        signInStage.setWidth(516);
        signInStage.setHeight(516);
        signInStage.setResizable(false);
        signInStage.show();
    }
}