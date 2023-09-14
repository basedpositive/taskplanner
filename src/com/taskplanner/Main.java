package com.taskplanner;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.awt.*;
import java.sql.Connection;

public class Main extends Application{

    public static void main(String[] args) {
        DatabaseConnector db = new DatabaseConnector();
        Connection connect = db.connect_to_db("schema", "postgres", "#SHKM277");
        // db.createTable(connect, "task");
        // db.insert_row(connect,"task","Some title","Some description",true);
        // db.update_title(connect,"task","Some title","Car");
        // db.update_description(connect,"task","Some description","wash");
        // db.delete_row_by_id(connect, "task", 4);
        db.read_data(connect,"task");

        launch(args);
    }

    @Override
    public void start(Stage stage) {

        Text text = new Text("CREATE");
        text.setLayoutY(158);
        text.setLayoutX(228);

        Button createTaskButton = new Button("Создать задачу");
        Button viewTaskButton = new Button("Посмотреть задачу");

        Group buttonGroup = new Group(createTaskButton, viewTaskButton);

        createTaskButton.setLayoutX(90);
        createTaskButton.setLayoutY(320);

        viewTaskButton.setLayoutX(290);
        viewTaskButton.setLayoutY(320);

        Group group = new Group(text, buttonGroup);

        Scene scene = new Scene(group);
        stage.setScene(scene);
        stage.setTitle("Task Manager");
        stage.setWidth(516);
        stage.setHeight(516);
        stage.setResizable(false);
        stage.show();
    }
}