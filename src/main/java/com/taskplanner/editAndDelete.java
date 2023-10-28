package com.taskplanner;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.ApacheHttpClient;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;

import java.sql.Connection;

public class editAndDelete {

    private static final String trelloKey = "84fd2b76e3e5735fa526b461efff968f";
    private static final String trelloToken = "ATTA2e2a3aa6e0b4c362481388f991503745cc603ac044bb322706e2239a8b66421034955A22";
    private final String BOARDID = "652fca1d642b944cbef18aa3";

    private final String DOINGLISTID = "652fcab0efc370b9ef82fb36";
    private final String DONELISTID = "652fcacf87a42c468a9a93b2";
    private final String TODOLISTID = "652fca7c69fa49c1861f53c9";
    private final String DEFLISTID = TODOLISTID;

    static final HttpClient httpClient = HttpClients.custom()
            .setDefaultRequestConfig(RequestConfig.custom()
                    .setCookieSpec(CookieSpecs.STANDARD).build())
            .build();

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

            String trelloCardId = selectedTask.getTrelloCardId();
            Trello trello = new TrelloImpl(trelloKey, trelloToken, new ApacheHttpClient(httpClient));

            Card card = trello.getCard(trelloCardId);
            card.setName(new_title);
            card.setDesc(new_description);
            card.update();

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

        String trelloCardId = selectedTask.getTrelloCardId();
        Trello trello = new TrelloImpl(trelloKey, trelloToken, new ApacheHttpClient(httpClient));
        Card card = trello.getCard(trelloCardId);
        card.delete();

        db.delete_row_by_id(connect, "task", taskId);
    }
}
