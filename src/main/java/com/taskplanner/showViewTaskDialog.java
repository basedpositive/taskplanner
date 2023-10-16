package com.taskplanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class showViewTaskDialog {
    private ContextMenu contextMenu;

    protected showViewTaskDialog(Connection connection, Stage primaryStage) {

        DatabaseConnector db = new DatabaseConnector();
        Main main = new Main();
        final Connection connect = db.connect_to_db("schema", "postgres", "#SHKM277");

        primaryStage.setTitle("Регистрация");

        Group root = new Group();
        Scene signUpScene = new Scene(root, 1247, 558, Color.WHITE);

        SplitPane splitPane = new SplitPane();
        splitPane.setPrefSize(1247, 558);
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPosition(0, 0.3);

        BorderPane leftPane = new BorderPane();
        leftPane.setMinWidth(370);
        leftPane.setMaxWidth(370);
        BorderPane rightPane = new BorderPane();
        splitPane.getItems().addAll(leftPane, rightPane);

        Button viewAllTaskButton = new Button("Все задачи");
        Button viewCurrentTaskButton = new Button("Текущие");
        Button viewPastTaskButton = new Button("Прошедшие");
        Button viewFutureTaskButton = new Button("Будущие");
        viewAllTaskButton.setLayoutX(380);
        viewAllTaskButton.setLayoutY(25);

        viewCurrentTaskButton.setLayoutX(494);
        viewCurrentTaskButton.setLayoutY(25);

        viewPastTaskButton.setLayoutX(571);
        viewPastTaskButton.setLayoutY(25);

        viewFutureTaskButton.setLayoutX(668);
        viewFutureTaskButton.setLayoutY(25);
        Group buttonGroup = new Group(viewAllTaskButton, viewCurrentTaskButton, viewPastTaskButton, viewFutureTaskButton);

        TableView<Task> tableTaskView = new TableView<>();
        tableTaskView.setLayoutX(380);
        tableTaskView.setLayoutY(63);
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

        Group elementsGroup = new Group(tableTaskView, buttonGroup);

        root.getChildren().addAll(splitPane, elementsGroup);
        primaryStage.setScene(signUpScene);
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