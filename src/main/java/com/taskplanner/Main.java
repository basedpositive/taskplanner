package com.taskplanner;

import javafx.application.Application;

// JavaFX
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.text.Text;

// SQL
import java.sql.*;

public class Main extends Application {

    DatabaseConnector db = new DatabaseConnector();
    final Connection connect = db.connect_to_db("schema", "postgres", "#SHKM277");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Task Planner");

        Group root = new Group(); // Корневой контейнер, который будет содержать все элементы, отображаемые на сцене
        Scene signInScene = new Scene(root, 1247, 558, Color.WHITE);


        // UI (Линий)
        Line vLineMain = new Line();
        vLineMain.setStroke(Paint.valueOf("#002bff"));
        vLineMain.setLayoutX(352);
        vLineMain.setEndY(558);
        Line hLineMain = new Line();
        hLineMain.setStroke(Paint.valueOf("#002bff"));
        hLineMain.setLayoutY(1);
        hLineMain.setEndX(1247);
        Group groupLine = new Group(vLineMain, hLineMain);


        // Надписи для авторизаций
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


        // Кнопка входа
        Button signInButton = new Button("SIGN IN");
        signInButton.setLayoutX(151);
        signInButton.setLayoutY(267);
        signInButton.setStyle("-fx-background-color: #4a90e2; " +
                "-fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-padding: 5 10; " +
                "-fx-background-radius: 5; -fx-border-radius: 5");


        // Кликабельная надпись для регистраций
        Label signUpText = new Label("CREATE ACCOUNT >>>");
        signUpText.setLayoutX(125);
        signUpText.setLayoutY(323);
        signUpText.setStyle("-fx-font: 12 arial");
            // UI
        signUpText.setOnMouseEntered(event -> signUpText.setStyle("-fx-font: 12 arial; -fx-text-fill: #357ae8"));
        signUpText.setOnMouseExited(event -> signUpText.setStyle("-fx-font: 12 arial"));
            // Переход в страницу регистраций(openSignUpWindow)
        signUpText.setOnMouseClicked(event -> new openSignUpWindow(connect, primaryStage));


        // Кнопка входа
            // UI при наведениях
        signInButton.setOnMouseEntered(event -> signInButton.setStyle("-fx-background-color: #357ae8; " + "-fx-text-fill: white; -fx-font-size: 14px; " + "-fx-padding: 5 10; " + "-fx-background-radius: 5; -fx-border-radius: 5"));
        signInButton.setOnMouseExited(event -> signInButton.setStyle("-fx-background-color: #4a90e2; " + "-fx-text-fill: white; -fx-font-size: 14px; " + "-fx-padding: 5 10; " + "-fx-background-radius: 5; -fx-border-radius: 5"));

        // Кнопка входа
        signInButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Alert
                // Имя или пароль не должны быть пустыми
            if (username.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка входа");
                alert.setHeaderText("Имя пользователя и пароль не могут быть пустыми.");
                alert.showAndWait();
                return;
            }

            // Проверка имени и пароля для авторизаций
                // sql-запрос для выборки данных из таблицы (users)
            try {
                String query = "SELECT * FROM users WHERE username = ? AND password = ?";

                // Подготовка sql-запроса с параметрами для поиска записей в бд
                try (PreparedStatement preparedStatement = connect.prepareStatement(query)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);

                    // Результат запроса
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        // Если УСПЕШНО
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
                e.printStackTrace(); // stackoverflow q/1486014
            }
        });

        Group elementsGroup = new Group(textLogin, textUsername, textPassword,
                usernameField, passwordField,
                signInButton, signUpText);

        // Отображение элементов
        root.getChildren().addAll(groupLine, elementsGroup);

        // Настройка
        primaryStage.setScene(signInScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}