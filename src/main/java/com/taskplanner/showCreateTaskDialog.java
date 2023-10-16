package com.taskplanner;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class showCreateTaskDialog {
    public showCreateTaskDialog(Connection connection) {
        DatabaseConnector db = new DatabaseConnector();

        Stage createTaskStage = new Stage();
        createTaskStage.setTitle("Создать задачу");

        VBox createTaskLayout = new VBox();
        createTaskLayout.setSpacing(10);

        Scene createTaskScene = new Scene(createTaskLayout, 300, 200);
        createTaskStage.setScene(createTaskScene);

        TextField titleField = new TextField();
        titleField.setText("Title");

        TextField descriptionField = new TextField();
        descriptionField.setText("Description");

        DatePicker dueDatePicker = new DatePicker();

        ChoiceBox<String> choiceStatus = new ChoiceBox<>();
        String[] isStatus = {"Завершено", "Планирую", "Не завершено"};
        choiceStatus.getItems().addAll(isStatus);

        Button saveButton = new Button("Сохранить");
        saveButton.setOnAction(e -> {
            String title = titleField.getText();
            String description = descriptionField.getText();
            String status = choiceStatus.getValue();
            Timestamp createdAt = Timestamp.valueOf(LocalDateTime.now());
            Timestamp dueDate = Timestamp.valueOf(dueDatePicker.getValue().atStartOfDay());

            db.insert_row(connection, "task", title, description, status, createdAt, dueDate);

            createTaskStage.close();
        });

        createTaskLayout.getChildren().addAll(titleField, descriptionField, dueDatePicker, choiceStatus, saveButton);

        createTaskStage.show();
    }
}