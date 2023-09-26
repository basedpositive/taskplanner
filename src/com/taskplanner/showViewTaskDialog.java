package com.taskplanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class showViewTaskDialog {
    private ContextMenu contextMenu;

    protected showViewTaskDialog(Connection connection) {

        Stage taskStage = new Stage();
        taskStage.setTitle("Список задач");

        taskStage.setWidth(516);
        taskStage.setHeight(516);

        TableView<Task> tableTaskView = new TableView<>();
        tableTaskView.setLayoutX(12.0);
        tableTaskView.setLayoutY(62.0);
        tableTaskView.setPrefWidth(450.0);
        tableTaskView.setPrefHeight(300.0);

        TableColumn<Task, Integer> idColumn = new TableColumn<>("№");
        idColumn.setPrefWidth(80.0);

        TableColumn<Task, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setPrefWidth(150.0);

        TableColumn<Task, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setPrefWidth(150.0);

        TableColumn<Task, Boolean> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setPrefWidth(80.0);

        List<TableColumn<Task, ?>> columns = new ArrayList<>();
        columns.add(idColumn);
        columns.add(titleColumn);
        columns.add(descriptionColumn);
        columns.add(statusColumn);

        tableTaskView.getColumns().addAll(columns);

        tableTaskView.setOnMousePressed(event -> {
            if (event.isSecondaryButtonDown()) {
                // Если нажата правая кнопка мыши, отобразите контекстное меню
                showContextMenu(tableTaskView, event.getScreenX(), event.getScreenY());
            }
        });

        Label label = new Label("Task List:");
        label.setLayoutX(14.0);
        label.setLayoutY(18.0);

        ObservableList<Task> taskList = FXCollections.observableArrayList(fetchTasksFromDatabase(connection));

        tableTaskView.setItems(taskList);
        Group taskGroup = new Group(tableTaskView, label);
        Scene taskScene = new Scene(taskGroup, 600, 400);
        taskStage.setScene(taskScene);

        taskStage.show();
    }

    private ObservableList<Task> fetchTasksFromDatabase(Connection connection) {
        ObservableList<Task> taskList = FXCollections.observableArrayList();
        Statement statement = null;
        ResultSet rs = null;

        try {
            String query = "SELECT id, title, description, status FROM task";
            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                boolean status = rs.getBoolean("status");
                taskList.add(new Task(id, title, description, status));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return taskList;
    }

    private void showContextMenu(TableView<Task> tableView, double x, double y) {
        if (contextMenu == null) {
            contextMenu = new ContextMenu(); // Создать контекстное меню только один раз
            MenuItem editMenuItem = new MenuItem("Редактировать задачу");
            editMenuItem.setOnAction(event -> {
                Task selectedTask = tableView.getSelectionModel().getSelectedItem();
                if (selectedTask != null) {
                    editAndDelete.editTask(selectedTask);
                }
            });
            MenuItem deleteMenuItem = new MenuItem("Удалить задачу");
            deleteMenuItem.setOnAction(event -> {
                Task selectedTask = tableView.getSelectionModel().getSelectedItem();
                if (selectedTask != null) {
                    editAndDelete.deleteTask(selectedTask);
                }
            });
            contextMenu.getItems().addAll(editMenuItem, deleteMenuItem);
        }

        contextMenu.show(tableView, x, y);
    }
}