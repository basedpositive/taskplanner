package com.taskplanner;

// JavaFX
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.util.Properties;
import java.util.regex.Pattern;

public class openSignUpWindow {

    public openSignUpWindow(Connection connection, Stage primaryStage) {
        DatabaseConnector db = new DatabaseConnector();
        Main main = new Main();

        primaryStage.setTitle("Регистрация");

        Group root = new Group();
        Scene signUpScene = new Scene(root, 1247, 558, Color.WHITE);


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


        // Надписи
        Text textReg = new Text("Sign up");
        textReg.setLayoutX(47);
        textReg.setLayoutY(104);
        textReg.setStyle("-fx-font: 38 arial;");

        Text textEmail = new Text("Email");
        textEmail.setLayoutX(47);
        textEmail.setLayoutY(184);
        TextField emailField = new TextField();
        emailField.setLayoutX(111);
        emailField.setLayoutY(167);

        Text textUsername = new Text("Username");
        textUsername.setLayoutX(47);
        textUsername.setLayoutY(232);
        TextField usernameField = new TextField();
        usernameField.setLayoutX(111);
        usernameField.setLayoutY(215);

        Text textPassword = new Text("Password");
        textPassword.setLayoutX(47);
        textPassword.setLayoutY(282);
        PasswordField passwordField = new PasswordField();
        passwordField.setLayoutX(111);
        passwordField.setLayoutY(265);


        // Кликабельная надпись (UI)
        Label backText = new Label("<<< BACK");
        backText.setLayoutX(157);
        backText.setLayoutY(387);
        backText.setStyle("-fx-font: 12 arial");
            // UI при наведениях
        backText.setOnMouseEntered(event -> backText.setStyle("-fx-font: 12 arial; -fx-text-fill: #357ae8"));
        backText.setOnMouseExited(event -> backText.setStyle("-fx-font: 12 arial"));
            // Возврат в (main)
        backText.setOnMouseClicked(event -> main.start(primaryStage));


        // Кнопка регистрации (UI)
        Button signUpButton = new Button("SIGN UP");
        signUpButton.setLayoutX(151);
        signUpButton.setLayoutY(331);
        signUpButton.setStyle("-fx-background-color: #4a90e2; " +
                "-fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-padding: 5 10; " +
                "-fx-background-radius: 5; -fx-border-radius: 5");

            // UI при наведениях
        signUpButton.setOnMouseEntered(event -> signUpButton.setStyle("-fx-background-color: #357ae8; " + "-fx-text-fill: white; -fx-font-size: 14px; " + "-fx-padding: 5 10; " + "-fx-background-radius: 5; -fx-border-radius: 5"));
        signUpButton.setOnMouseExited(event -> signUpButton.setStyle("-fx-background-color: #4a90e2; " + "-fx-text-fill: white; -fx-font-size: 14px; " + "-fx-padding: 5 10; " + "-fx-background-radius: 5; -fx-border-radius: 5"));
            // Регистрация пользователя
        signUpButton.setOnAction(e -> {
            String email = emailField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Валидация
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

        Group elementsGroup = new Group(textReg, textEmail, emailField, textUsername, usernameField, textPassword, passwordField, signUpButton, backText);
        root.getChildren().addAll(groupLine, elementsGroup);

        // Настройка
        primaryStage.setScene(signUpScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    // Реализация отправки электронного письма после регистрации
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


    // В случае ошибки
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
