package com.taskplanner;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.sql.*;

public class showCreateTaskDialog {
    public showCreateTaskDialog(Connection connection) {
        DatabaseConnector db = new DatabaseConnector();
        // Создайте новое окно для ввода данных
        Stage createTaskStage = new Stage();
        createTaskStage.setTitle("Создать задачу");

        // Создайте макет для формы ввода данных
        VBox createTaskLayout = new VBox();
        createTaskLayout.setSpacing(10);

        // Создайте текстовые поля для ввода title и description
        TextField titleField = new TextField();
        titleField.setText("Title");

        TextField descriptionField = new TextField();
        descriptionField.setText("Description");

        // Создайте кнопку "Сохранить" для сохранения задачи
        Button saveButton = new Button("Сохранить");
        saveButton.setOnAction(e -> {
            String title = titleField.getText();
            String description = descriptionField.getText();

            // Вызовите метод для добавления задачи в базу данных
            db.insert_row(connection, "task", title, description, true);

            // Закройте окно создания задачи после сохранения
            createTaskStage.close();
        });

        // Добавьте элементы на макет
        createTaskLayout.getChildren().addAll(titleField, descriptionField, saveButton);

        // Создайте сцену с макетом и установите ее в окно
        Scene createTaskScene = new Scene(createTaskLayout, 300, 200);
        createTaskStage.setScene(createTaskScene);

        // Покажите окно создания задачи
        createTaskStage.show();
    }
}