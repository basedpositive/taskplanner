package com.taskplanner;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class openSignUpWindow {
    public openSignUpWindow(Connection connection) {
        DatabaseConnector db = new DatabaseConnector();

        Stage signInStage = new Stage();
        signInStage.setTitle("Регистрация");

        VBox signInLayout = new VBox();
        signInLayout.setSpacing(10);

        Scene signInScene = new Scene(signInLayout, 516, 516);
        signInStage.setScene(signInScene);

        Label labelLogin = new Label("SIGN UP");
        labelLogin.setLayoutX(12);
        labelLogin.setLayoutY(10);

        Text textEmail = new Text("Email");
        textEmail.setLayoutX(100);
        textEmail.setLayoutY(100);
        TextField emailField = new TextField();
        emailField.setLayoutX(180);
        emailField.setLayoutY(85);

        Text textUsername = new Text("Username");
        textUsername.setLayoutX(100);
        textUsername.setLayoutY(160);
        TextField usernameField = new TextField();
        usernameField.setLayoutX(180);
        usernameField.setLayoutY(145);

        Text textPassword = new Text("Password");
        textPassword.setLayoutX(100);
        textPassword.setLayoutY(203);
        PasswordField passwordField = new PasswordField ();
        passwordField.setLayoutX(180);
        passwordField.setLayoutY(185);

        Button signUpButton = new Button("SIGN UP");
        signUpButton.setLayoutX(180);
        signUpButton.setLayoutY(235);
        signUpButton.setOnAction(e -> {
            String email = emailField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();

            db.insert_registration_row(connection, "users", email, username, password);
        });

        Group testGroup = new Group(labelLogin, textEmail, emailField, textUsername, usernameField, textPassword, passwordField, signUpButton);

        signInLayout.getChildren().addAll(testGroup);

        signInStage.show();
    }
}
