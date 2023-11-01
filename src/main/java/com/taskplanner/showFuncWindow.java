package com.taskplanner;

import javafx.stage.Stage;

// JavaFX
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

// Time
import java.time.LocalDate;

// Collections
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// SQL
import java.sql.*;
import java.sql.Connection;

public class showFuncWindow {
    private Task selectedTask;

    public showFuncWindow(Connection connection, Stage primaryStage) {

        primaryStage.setTitle("Task Planner");

        Group root = new Group();
        Scene funcScene = new Scene(root, 1247, 558, Color.WHITE);


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
            // *должна быть информация пользователя
        Text textUsername = new Text("Username");
        textUsername.setLayoutX(29);
        textUsername.setLayoutY(43);
        textUsername.setStyle("-fx-font: 20 arial;");
        Text textUserEmail = new Text("sample@sample.com");
        textUserEmail.setLayoutX(29);
        textUserEmail.setLayoutY(73);
        textUserEmail.setStyle("-fx-font: 12 arial;");


        // Кнопка создания задачи
        Button createTaskButton = new Button("CREATE TASK");
        createTaskButton.setLayoutX(134);
        createTaskButton.setLayoutY(252);
        createTaskButton.setStyle("-fx-background-color: #4a90e2; " +
                "-fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-padding: 5 10; " +
                "-fx-background-radius: 5; -fx-border-radius: 5");

            // UI
        createTaskButton.setOnMouseEntered(event -> createTaskButton.setStyle("-fx-background-color: #357ae8; " + "-fx-text-fill: white; -fx-font-size: 14px; " + "-fx-padding: 5 10; " + "-fx-background-radius: 5; -fx-border-radius: 5"));
        createTaskButton.setOnMouseExited(event -> createTaskButton.setStyle("-fx-background-color: #4a90e2; " + "-fx-text-fill: white; -fx-font-size: 14px; " + "-fx-padding: 5 10; " + "-fx-background-radius: 5; -fx-border-radius: 5"));
            // Открытие окна для создания задачи
        createTaskButton.setOnAction(e -> new showCreateTaskWindow(connection));

        Group elementsGroup = new Group(textUsername, textUserEmail,
                createTaskButton,
                groupLine);

        // Контекстное меню для реализаций изменение и удаление
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editMenuItem = new MenuItem("Изменить");
        MenuItem deleteMenuItem = new MenuItem("Удалить");
        contextMenu.getItems().addAll(editMenuItem, deleteMenuItem);

        // Реализация изменения
        editMenuItem.setOnAction(event -> {
            if (selectedTask != null) {
                editAndDelete.editTask(selectedTask);
            }
        });

        // Реализация удаления
        deleteMenuItem.setOnAction(event -> {
            if (selectedTask != null) {
                editAndDelete.deleteTask(selectedTask);
            }
        });


        // Макет задач
        VBox taskContainer = new VBox();
        taskContainer.setSpacing(10);
        
        taskContainer.setLayoutX(380);
        taskContainer.setLayoutY(14);


        // Сетка (GridPane)
        GridPane taskGrid = new GridPane();
        taskGrid.setHgap(10); // Отступ
        taskGrid.setVgap(10); // Отступ
        taskGrid.setLayoutX(14);
        taskGrid.setLayoutY(14);


        // Кнопки (All, current, past, future)
        Button viewAllTaskButton = new Button("Все задачи");
        Button viewCurrentTaskButton = new Button("Текущие");
        Button viewPastTaskButton = new Button("Прошедшие");
        Button viewFutureTaskButton = new Button("Будущие");
            // Координаты (All)
        viewAllTaskButton.setLayoutX(400);
        viewAllTaskButton.setLayoutY(450);
            // Координаты (Current)
        viewCurrentTaskButton.setLayoutX(500);
        viewCurrentTaskButton.setLayoutY(450);
            // Координаты (Past)
        viewPastTaskButton.setLayoutX(575);
        viewPastTaskButton.setLayoutY(450);
            // Координаты (Future)
        viewFutureTaskButton.setLayoutX(670);
        viewFutureTaskButton.setLayoutY(450);
        Group buttonGroup = new Group(viewAllTaskButton, viewCurrentTaskButton, viewPastTaskButton, viewFutureTaskButton);


        // (ObservableList) упрощает, так как автоматически обновляет и обрабатывает изменения данных
        ObservableList<Task> tasks = fetchTasksFromDatabase(connection);


        // Дефолтный показ всех задач
        int column = 0;
        int row = 0;

        for (Task task : tasks) {
            Pane cardPane = createCardPane(task);

            // Реализация контекстного меню при нажатиях
            cardPane.setOnContextMenuRequested(event -> {
                contextMenu.show(cardPane, event.getScreenX(), event.getScreenY());
                    //
                selectedTask = task;
            });

            taskGrid.add(cardPane, column, row);
            row++;

            if (row >= 3) {
                row = 0;
                column++;
            }
        }


        // Кнопка "Все задачи"
        viewAllTaskButton.setOnAction(event -> {
                // Все задачи хранятся в allTaskList...
            ObservableList<Task> allTaskList = FXCollections.observableArrayList(fetchTasksFromDatabase(connection));
                // Очистка, чтобы обновить
            taskGrid.getChildren().clear();

            int columnIndex = 0;
            int rowIndex = 0;

                // Перебор всех задач
            for (Task task : allTaskList) {
                Pane cardPane = createCardPane(task);
                taskGrid.add(cardPane, columnIndex, rowIndex);
                rowIndex++;

                    // В три ряда, дальше колонки
                if (rowIndex >= 3) {
                    rowIndex = 0;
                    columnIndex++;
                }
            }
        });

        // Кнопка "Текущие"
        viewCurrentTaskButton.setOnAction(event -> {
                //
            ObservableList<Task> currentTasks = filterTasksByDate(tasks, LocalDate.now());
                // Очистка, чтобы обновить
            taskGrid.getChildren().clear();

            int columnIndex = 0;
            int rowIndex = 0;

                // Перебор всех задач
            for (Task task : currentTasks) {
                Pane cardPane = createCardPane(task);
                taskGrid.add(cardPane, columnIndex, rowIndex);
                rowIndex++;

                if (rowIndex >= 3) {
                    rowIndex = 0;
                    columnIndex++;
                }
            }
        });

        // Кнопка "Прошедшие"
        viewPastTaskButton.setOnAction(event -> {
                //
            ObservableList<Task> pastTasks = filterPastTasksByDate(tasks, LocalDate.now());
                // Очистка, чтобы обновить
            taskGrid.getChildren().clear();

            int columnIndex = 0;
            int rowIndex = 0;

                // Перебор всех задач
            for (Task task : pastTasks) {
                Pane cardPane = createCardPane(task);
                taskGrid.add(cardPane, columnIndex, rowIndex);
                rowIndex++;

                if (rowIndex >= 3) {
                    rowIndex = 0;
                    columnIndex++;
                }
            }
        });

        // Кнопка "Будущие"
        viewFutureTaskButton.setOnAction(event -> {
                //
            ObservableList<Task> futureTasks = filterFutureTasksByDate(tasks, LocalDate.now());
                // Очистка, чтобы обновить
            taskGrid.getChildren().clear();

            int columnIndex = 0;
            int rowIndex = 0;

                // Перебор всех задач
            for (Task task : futureTasks) {
                Pane cardPane = createCardPane(task);
                taskGrid.add(cardPane, columnIndex, rowIndex);
                rowIndex++;

                if (rowIndex >= 3) {
                    rowIndex = 0;
                    columnIndex++;
                }
            }
        });

        
        // Ползунок, что снизу
        ScrollPane scrollPane = new ScrollPane(taskGrid);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setPrefWidth(808);
        scrollPane.setPrefHeight(410);

        
        // Добавление элементов в макет задач (VBOX taskContainer)
        taskContainer.getChildren().addAll(taskGrid, scrollPane);
        // Отображение элементов
        root.getChildren().addAll(elementsGroup, taskContainer, buttonGroup);

        // Настройка
        primaryStage.setScene(funcScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    // Метод для извлечения данных из бд
    private ObservableList<Task> fetchTasksFromDatabase(Connection connection) {
        ObservableList<Task> taskList = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String query = "SELECT id, title, description, status, createdAt, dueDate, trello_card_id FROM task";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String status = resultSet.getString("status");
                Timestamp createdAt = resultSet.getTimestamp("createdAt");
                Timestamp dueDate = resultSet.getTimestamp("dueDate");
                String trelloCardId = resultSet.getString("trello_card_id");
                taskList.add(new Task(id, title, description, status, createdAt, dueDate, trelloCardId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally { // Закрытие
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return taskList;
    }


    // Метод для сортировки данных по дате (Current)
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

    // Метод для сортировки данных по дате (Past)
    private ObservableList<Task> filterPastTasksByDate(ObservableList<Task> taskList, LocalDate date) {
        return taskList.filtered(task -> {
            Timestamp dueDate = task.getDueDate();
            if (dueDate == null) {
                return false;
            }
            LocalDate taskDueDate = dueDate.toLocalDateTime().toLocalDate();
            return taskDueDate.isBefore(date.minusDays(1));
        });
    }

    // Метод для сортировки данных по дате (Future)
    private ObservableList<Task> filterFutureTasksByDate(ObservableList<Task> taskList, LocalDate date) {
        return taskList.filtered(task -> {
            Timestamp dueDate = task.getDueDate();
            if (dueDate == null) {
                return false;
            }
            LocalDate taskDueDate = dueDate.toLocalDateTime().toLocalDate();
            return taskDueDate.isAfter(date.plusDays(1));
        });
    }


    // UI (Pane)
    private Pane createCardPane(Task task) {
        Pane cardPane = new Pane();
        cardPane.setPrefWidth(179);
        cardPane.setPrefHeight(122);

        cardPane.setStyle("-fx-border-color: #4a90e2; -fx-background-color: white;");

        Label nameLabel = new Label(task.getTitle());
        nameLabel.setPrefWidth(154);
        nameLabel.setPrefHeight(17);
        nameLabel.setLayoutX(13);
        nameLabel.setLayoutY(6);

        Text desText = new Text(task.getDescription());
        desText.setLayoutX(13);
        desText.setLayoutY(47);

        Text dueDateText = new Text(task.getDueDate().toString());
        dueDateText.setLayoutX(56);
        dueDateText.setLayoutY(113);

        cardPane.getChildren().addAll(nameLabel, desText, dueDateText);

        return cardPane;
    }

}