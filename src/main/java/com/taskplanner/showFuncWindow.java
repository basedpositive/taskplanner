package com.taskplanner;

import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;

public class showFuncWindow {
    private ContextMenu contextMenu;

    public showFuncWindow(Connection connection, Stage primaryStage) {
        DatabaseConnector db = new DatabaseConnector();
        Main main = new Main();
        final Connection connect = db.connect_to_db("schema", "postgres", "#SHKM277");

        primaryStage.setTitle("Регистрация");

        Group root = new Group();
        Scene funcScene = new Scene(root, 1247, 558, Color.WHITE);

        SplitPane splitPane = new SplitPane();
        splitPane.setPrefSize(1247, 558);
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPosition(0, 0.3);

        BorderPane leftPane = new BorderPane();
        leftPane.setMinWidth(370);
        leftPane.setMaxWidth(370);
        BorderPane rightPane = new BorderPane();
        splitPane.getItems().addAll(leftPane, rightPane);

        Text textUsername = new Text("Username");
        textUsername.setLayoutX(29);
        textUsername.setLayoutY(43);
        textUsername.setStyle("-fx-font: 20 arial;");
        Text textUserEmail = new Text("sample@sample.com");
        textUserEmail.setLayoutX(29);
        textUserEmail.setLayoutY(73);
        textUserEmail.setStyle("-fx-font: 12 arial;");

        Button createTaskButton = new Button("CREATE TASK");
        createTaskButton.setLayoutX(134);
        createTaskButton.setLayoutY(252);
        createTaskButton.setStyle("-fx-background-color: #4a90e2; " +
                "-fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-padding: 5 10; " +
                "-fx-background-radius: 5; -fx-border-radius: 5");

        createTaskButton.setOnMouseEntered(event -> createTaskButton.setStyle("-fx-background-color: #357ae8; " + "-fx-text-fill: white; -fx-font-size: 14px; " + "-fx-padding: 5 10; " + "-fx-background-radius: 5; -fx-border-radius: 5"));
        createTaskButton.setOnMouseExited(event -> createTaskButton.setStyle("-fx-background-color: #4a90e2; " + "-fx-text-fill: white; -fx-font-size: 14px; " + "-fx-padding: 5 10; " + "-fx-background-radius: 5; -fx-border-radius: 5"));
        createTaskButton.setOnAction(e -> new showCreateTaskDialog(connection));

        Group elementsGroup = new Group(textUsername, textUserEmail, createTaskButton);

        // tasks view
        Button viewAllTaskButton = new Button("Все задачи");
        Button viewCurrentTaskButton = new Button("Текущие");
        Button viewPastTaskButton = new Button("Прошедшие");
        Button viewFutureTaskButton = new Button("Будущие");
        viewAllTaskButton.setLayoutX(14);
        viewAllTaskButton.setLayoutY(27);

        viewCurrentTaskButton.setLayoutX(128);
        viewCurrentTaskButton.setLayoutY(27);

        viewPastTaskButton.setLayoutX(205);
        viewPastTaskButton.setLayoutY(27);

        viewFutureTaskButton.setLayoutX(302);
        viewFutureTaskButton.setLayoutY(27);
        Group buttonGroup = new Group(viewAllTaskButton, viewCurrentTaskButton, viewPastTaskButton, viewFutureTaskButton);

        TableView<Task> tableTaskView = new TableView<>();
        tableTaskView.setLayoutX(3);
        tableTaskView.setLayoutY(69);
        tableTaskView.setPrefWidth(862);
        tableTaskView.setPrefHeight(484);

        TableColumn<Task, Integer> idColumn = new TableColumn<>("№");
        idColumn.setPrefWidth(80.0);

        TableColumn<Task, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setPrefWidth(150.0);

        TableColumn<Task, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setPrefWidth(150.0);

        TableColumn<Task, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setPrefWidth(80.0);

        TableColumn<Task, Timestamp> createdAtColumn = new TableColumn<>("CreatedAt");
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdAtColumn.setPrefWidth(150.0);

        TableColumn<Task, Timestamp> dueDateColumn = new TableColumn<>("DueDate");
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        dueDateColumn.setPrefWidth(150.0);

        List<TableColumn<Task, ?>> columns = new ArrayList<>();
        columns.add(idColumn);
        columns.add(titleColumn);
        columns.add(descriptionColumn);
        columns.add(statusColumn);
        columns.add(createdAtColumn);
        columns.add(dueDateColumn);

        tableTaskView.getColumns().addAll(columns);

        tableTaskView.setOnMousePressed(event -> {
            if (event.isSecondaryButtonDown()) {
                showContextMenu(tableTaskView, event.getScreenX(), event.getScreenY());
            }
        });


        ObservableList<Task> taskList = FXCollections.observableArrayList(fetchTasksFromDatabase(connection));

        viewAllTaskButton.setOnAction(event -> {
            ObservableList<Task> allTaskList = FXCollections.observableArrayList(fetchTasksFromDatabase(connection));
            tableTaskView.setItems(allTaskList);
        });

        viewCurrentTaskButton.setOnAction(event -> {
            ObservableList<Task> currentTasks = filterTasksByDate(taskList, LocalDate.now());
            tableTaskView.setItems(currentTasks);
        });

        viewPastTaskButton.setOnAction(event -> {
            ObservableList<Task> pastTasks = filterPastTasksByDate(taskList, LocalDate.now().minusDays(1));
            tableTaskView.setItems(pastTasks);
        });

        viewFutureTaskButton.setOnAction(event -> {
            ObservableList<Task> futureTasks = filterFutureTasksByDate(taskList, LocalDate.now().plusDays(1));
            tableTaskView.setItems(futureTasks);
        });

        tableTaskView.setItems(taskList);

        Group tableGroup = new Group(tableTaskView, buttonGroup);

        rightPane.getChildren().addAll(tableGroup);
        root.getChildren().addAll(splitPane, elementsGroup);

        primaryStage.setScene(funcScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private ObservableList<Task> fetchTasksFromDatabase(Connection connection) {
        ObservableList<Task> taskList = FXCollections.observableArrayList();
        Statement statement = null;
        ResultSet rs = null;

        try {
            String query = "SELECT id, title, description, status, createdAt, dueDate FROM task";
            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String status = rs.getString("status");
                Timestamp createdAt = rs.getTimestamp("createdAt");
                Timestamp dueDate = rs.getTimestamp("dueDate");
                taskList.add(new Task(id, title, description, status, createdAt, dueDate));
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

    private ObservableList<Task> filterTasksByDate(ObservableList<Task> taskList, LocalDate date) {
        return taskList.filtered(task -> {
            Timestamp dueDate = task.getDueDate();
            if (dueDate == null) {
                return false;
            }
            LocalDate taskDueDate = dueDate.toLocalDateTime().toLocalDate();
            return taskDueDate.equals(date);
        });
    }

    private ObservableList<Task> filterPastTasksByDate(ObservableList<Task> taskList, LocalDate date) {
        return taskList.filtered(task -> {
            Timestamp dueDate = task.getDueDate();
            if (dueDate == null) {
                return false;
            }
            LocalDate taskDueDate = dueDate.toLocalDateTime().toLocalDate();
            return taskDueDate.isBefore(date);
        });
    }
    private ObservableList<Task> filterFutureTasksByDate(ObservableList<Task> taskList, LocalDate date) {
        return taskList.filtered(task -> {
            Timestamp dueDate = task.getDueDate();
            if (dueDate == null) {
                return false;
            }
            LocalDate taskDueDate = dueDate.toLocalDateTime().toLocalDate();
            return taskDueDate.isAfter(date);
        });
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
