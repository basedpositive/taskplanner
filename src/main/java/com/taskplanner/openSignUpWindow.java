package com.taskplanner;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.util.Properties;
import java.util.regex.Pattern;

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

            if (email == null || email.isEmpty() || username == null || username.isEmpty() || password == null || password.isEmpty()) {
                showErrorDialog("Поля не могут быть пустыми!");
            } else {
                String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                        "[a-zA-Z0-9_+&*-]+)*@" +
                        "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
                Pattern pattern = Pattern.compile(emailRegex);
            if (!pattern.matcher(email).matches()) {
                showErrorDialog("Email не соответствует формату");
            } else {
                db.insert_registration_row(connection, "users", email, username, password);
                sendWelcomeEmail(username, email);
                }
            }
        });

        Group testGroup = new Group(labelLogin, textEmail, emailField, textUsername, usernameField, textPassword, passwordField, signUpButton);

        signInLayout.getChildren().addAll(testGroup);

        signInStage.show();
    }

    private void sendWelcomeEmail(String username, String email) {
        final String host = "smtp.gmail.com";
        final String port = "587";
        final String userName = "keplop001@gmail.com";
        final String password = "fstvbuudzaiaqrlp";
        final boolean useTLS = true;

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", String.valueOf(useTLS));
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Приветствие");
            message.setText("Здравствуйте, дорогой " + username + "! Благодарим вас за регистрацию!");

            Transport.send(message);
            System.out.println("Приветственное письмо отправлено успешно.");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Ошибка при отправке приветственного письма: " + e.getMessage());
        }
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
