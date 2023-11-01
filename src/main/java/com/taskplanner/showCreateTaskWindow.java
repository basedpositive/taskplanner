package com.taskplanner;

import java.time.LocalDateTime;

// Trello
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.ApacheHttpClient;

// JavaFX
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;

// Apache
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;

// SQL
import java.sql.*;


public class showCreateTaskWindow {

    private static final String trelloKey = "84fd2b76e3e5735fa526b461efff968f";
    private static final String trelloToken = "ATTA2e2a3aa6e0b4c362481388f991503745cc603ac044bb322706e2239a8b66421034955A22";
    // private final String BOARDID = "652fca1d642b944cbef18aa3";

    private final String DOINGLISTID = "652fcab0efc370b9ef82fb36";
    private final String DONELISTID = "652fcacf87a42c468a9a93b2";
    private final String TODOLISTID = "652fca7c69fa49c1861f53c9";
    private final String DEFLISTID = TODOLISTID;


    // stackoverflow q/36473478
    static final HttpClient httpClient = HttpClients.custom()
            .setDefaultRequestConfig(RequestConfig.custom()
                    .setCookieSpec(CookieSpecs.STANDARD).build())
            .build(); // org.apache.http.client.protocol.ResponseProcessCookies WARNING: Invalid cookie header...


    public showCreateTaskWindow(Connection connection) {

        Trello trello = new TrelloImpl(trelloKey, trelloToken , new ApacheHttpClient(httpClient));

        DatabaseConnector db = new DatabaseConnector();

        Stage createTaskStage = new Stage();
        createTaskStage.setTitle("Создать задачу");

        VBox createTaskLayout = new VBox();
        createTaskLayout.setSpacing(10);

        Scene createTaskScene = new Scene(createTaskLayout, 300, 200);
        createTaskStage.setScene(createTaskScene);


        // Надписи
        TextField titleField = new TextField();
        titleField.setText("Title");

        TextField descriptionField = new TextField();
        descriptionField.setText("Description");

        DatePicker dueDatePicker = new DatePicker();

        ChoiceBox<String> choiceStatus = new ChoiceBox<>();
        String[] isStatus = {"Делаю", "Планирую", "Завершено"};
        choiceStatus.getItems().addAll(isStatus);


        // Кнопка сохранить
        Button saveButton = new Button("Сохранить");
        saveButton.setOnAction(e -> {
            String title = titleField.getText();
            String description = descriptionField.getText();
            String status = choiceStatus.getValue();

            Timestamp createdAt = Timestamp.valueOf(LocalDateTime.now());
            Timestamp dueDate = Timestamp.valueOf(dueDatePicker.getValue().atStartOfDay());

            // Синхронизация с Trello
            Card card = new Card();
            card.setName(title);
            card.setDesc(description);

            // Перемещение по спискам
            TList trelloList = switch (status) {
                case "Делаю" -> trello.getList(DOINGLISTID);
                case "Планирую" -> trello.getList(TODOLISTID);
                case "Завершено" -> trello.getList(DONELISTID);
                default -> trello.getList(DEFLISTID);
            };
            Card createdCard = trelloList.createCard(card);

            String trelloCardId = createdCard.getId();

            db.insert_row(connection, "task", title, description, status, createdAt, dueDate, trelloCardId);

            createTaskStage.close();
        });

        createTaskLayout.getChildren().addAll(titleField, descriptionField, dueDatePicker, choiceStatus, saveButton);

        createTaskStage.show();
    }
}