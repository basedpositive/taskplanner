package com.taskplanner;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.text.Text;
import java.sql.*;

public class Main extends Application {
    private Stage primaryStage;

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
    public void start(Stage primaryStage) {

        DatabaseConnector db = new DatabaseConnector();
        final Connection connect = db.connect_to_db("schema", "postgres", "#SHKM277"); // connect mozhno vverh

        primaryStage.setTitle("Task Manager");

        Group root = new Group();
        Scene signInScene = new Scene(root, 1247, 558, Color.WHITE);

        SplitPane splitPane = new SplitPane();
        splitPane.setPrefSize(1247, 558);
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPosition(0, 0.3);

        BorderPane leftPane = new BorderPane();
        leftPane.setMinWidth(370);
        leftPane.setMaxWidth(370);
        BorderPane rightPane = new BorderPane();
        splitPane.getItems().addAll(leftPane, rightPane);


        Text textLogin = new Text("Sign in");
        textLogin.setLayoutX(47);
        textLogin.setLayoutY(104);
        textLogin.setStyle("-fx-font: 38 arial;");

        Text textUsername = new Text("Username");
        textUsername.setLayoutX(47);
        textUsername.setLayoutY(168);
        TextField usernameField = new TextField();
        usernameField.setLayoutX(111);
        usernameField.setLayoutY(151);

        Text textPassword = new Text("Password");
        textPassword.setLayoutX(47);
        textPassword.setLayoutY(218);
        PasswordField passwordField = new PasswordField();
        passwordField.setLayoutX(111);
        passwordField.setLayoutY(201);

        Button signInButton = new Button("SIGN IN");
        signInButton.setLayoutX(151);
        signInButton.setLayoutY(267);
        signInButton.setStyle("-fx-background-color: #4a90e2; " +
                "-fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-padding: 5 10; " +
                "-fx-background-radius: 5; -fx-border-radius: 5");

        Label signUpText = new Label("CREATE ACCOUNT >>>");
        signUpText.setLayoutX(125);
        signUpText.setLayoutY(323);
        signUpText.setStyle("-fx-font: 12 arial");

        signUpText.setOnMouseEntered(event -> signUpText.setStyle("-fx-font: 12 arial; -fx-text-fill: #357ae8"));
        signUpText.setOnMouseExited(event -> signUpText.setStyle("-fx-font: 12 arial"));
        signUpText.setOnMouseClicked(event -> new openSignUpWindow(connect, primaryStage));

        signInButton.setOnMouseEntered(event -> signInButton.setStyle("-fx-background-color: #357ae8; " + "-fx-text-fill: white; -fx-font-size: 14px; " + "-fx-padding: 5 10; " + "-fx-background-radius: 5; -fx-border-radius: 5"));
        signInButton.setOnMouseExited(event -> signInButton.setStyle("-fx-background-color: #4a90e2; " + "-fx-text-fill: white; -fx-font-size: 14px; " + "-fx-padding: 5 10; " + "-fx-background-radius: 5; -fx-border-radius: 5"));
        signInButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка входа");
                alert.setHeaderText("Имя пользователя и пароль не могут быть пустыми.");
                alert.showAndWait();
                return;
            }

            try {
                String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

                try (PreparedStatement preparedStatement = connect.prepareStatement(sql)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {

                            primaryStage.close();
                            new showFuncWindow(connect, primaryStage);
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

        Group elementsGroup = new Group(textLogin, textUsername, usernameField, textPassword, passwordField, signInButton, signUpText);

        root.getChildren().addAll(splitPane, elementsGroup);
        primaryStage.setScene(signInScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}