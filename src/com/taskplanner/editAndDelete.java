package com.taskplanner;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;

public class editAndDelete {
    static void editTask(Task selectedTask) {
        DatabaseConnector db = new DatabaseConnector();
        Connection connect = db.connect_to_db("schema", "postgres", "#SHKM277");
        Stage createTaskStage = new Stage();
        createTaskStage.setTitle("Изменить задачу");
        VBox createTaskLayout = new VBox();
        createTaskLayout.setSpacing(10);

        TextField titleField = new TextField();
        titleField.setText("Update Title...");
        TextField descriptionField = new TextField();
        descriptionField.setText("Update Description...");

        Button saveButton = new Button("Сохранить");
        saveButton.setOnAction(e -> {
            String new_title = titleField.getText();
            String new_description = descriptionField.getText();

            db.update_title(connect, "task", selectedTask.getId(), new_title);
            db.update_description(connect, "task", selectedTask.getId(), new_description);

            createTaskStage.close();
        });

        createTaskLayout.getChildren().addAll(titleField, descriptionField, saveButton);
        Scene createTaskScene = new Scene(createTaskLayout, 300, 200);
        createTaskStage.setScene(createTaskScene);

        createTaskStage.show();
    }
    static void deleteTask(Task selectedTask) {
        DatabaseConnector db = new DatabaseConnector();
        Connection connect = db.connect_to_db("schema", "postgres", "#SHKM277");

        int taskId = selectedTask.getId();

        db.delete_row_by_id(connect, "task", taskId);
    }
}
