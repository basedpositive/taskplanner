package com.taskplanner;

import javafx.scene.control.Alert;
import java.sql.*;


// https://github.com/abhishekmahajan98/Java_PostGreSQL-CRUD (youtube)


public class DatabaseConnector {
    // Подключение к бд
    public Connection connect_to_db(String dbname, String user, String pass) {
        Connection connect = null;
        try {
            connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + dbname, user, pass); // почему-то при "localhost/5432" вызывало ошибку
            if(connect != null){ System.out.println("Connection Established."); }
            else System.out.println("Connection Failed.");
        } catch(Exception e) { e.printStackTrace(); }

        return connect;
    }


    // Создание таблицы
    public void createTable(Connection connect, String table_name){
        Statement statement;
        try{
            String query = "CREATE TABLE " + table_name + " (" +
                    "id SERIAL PRIMARY KEY," +
                    "title VARCHAR(200)," +
                    "description VARCHAR(200)," +
                    "status BOOLEAN," +
                    "createdAt TIMESTAMP," +
                    "dueDate TIMESTAMP" +
                    ");";
            statement=connect.createStatement();
            statement.executeUpdate(query);
            System.out.println("Table Created.");
        } catch (Exception e){ e.printStackTrace(); }
    }


    // Добавление информации в таблицу
    public void insert_row(Connection connection, String tableName, String title, String description, String status, Timestamp createdAt, Timestamp dueDate, String trelloCardId) {
        if (isValidTitle(title) && isValidDescription(description)) {
            String sql = "INSERT INTO " + tableName + " (title, description, status, createdAt, dueDate, trello_card_id) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, title);
                statement.setString(2, description);
                statement.setString(3, status);
                statement.setTimestamp(4, createdAt);
                statement.setTimestamp(5, dueDate);
                statement.setString(6, trelloCardId);
                statement.executeUpdate();
                System.out.println("Row Inserted.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Некорректные значения.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка создания задачи.");
            alert.setHeaderText("Title <= 15, description <= 50.");
            alert.showAndWait();
        }
    }


    // Добавление информации в таблицу после регистрации
    public void insert_registration_row(Connection connect, String table_name, String userEmail, String userName, String userPassword) {
        try {
            String query = String.format("INSERT INTO %s (email, username, password) VALUES (?, ?, ?)", table_name);
            PreparedStatement insertUserStatement = connect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            insertUserStatement.setString(1, userEmail);
            insertUserStatement.setString(2, userName);
            insertUserStatement.setString(3, userPassword);

            int affectedRows = insertUserStatement.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("Создание пользователя провалена.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Регистрация провалена.");
                alert.setHeaderText("Некорректные значения.");
                alert.showAndWait();
            }

            ResultSet generatedKeys = insertUserStatement.getGeneratedKeys();
            int user_id = -1;
            if (generatedKeys.next()) {
                user_id = generatedKeys.getInt(1);
            } else {
                System.out.println("Регистрация провалена.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Регистрация провалена.");
                alert.setHeaderText("Некорректные значения.");
                alert.showAndWait();
            }

            String giveRoleQuery = "insert into user_role(user_id, role_id) values(?, ?)";
            PreparedStatement giveRoleStatement = connect.prepareStatement(giveRoleQuery);
            giveRoleStatement.setInt(1, user_id);
            giveRoleStatement.setInt(2, 2);
            giveRoleStatement.executeUpdate();

            System.out.println("User Inserted.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Валидация названия и описания
    private boolean isValidTitle(String title) {
        return title != null && !title.isEmpty() && title.length() <= 15;
    }
    private boolean isValidDescription(String description) {
        return description != null && description.length() <= 50;
    }


    // Чтение бд
    public void read_data(Connection connect, String table_name){
        Statement statement;
        ResultSet rs = null;
        try {
            String query = String.format("select * from %s",table_name);
            statement = connect.createStatement();
            rs = statement.executeQuery(query);
            while(rs.next()){
                System.out.print(rs.getString("id")+" ");
                System.out.print(rs.getString("title")+" ");
                System.out.print(rs.getString("description")+" ");
                System.out.println(rs.getBoolean("status")+" ");
            }
        }
        catch (Exception e){ e.printStackTrace(); }
    }


    // Реализация изменения
    public void update_title(Connection connect, String table_name, int taskId, String new_title) {
        Statement statement;
        if (isValidTitle(new_title)) {
            try {
                String query = String.format("update %s set title='%s' where id=%d", table_name, new_title, taskId);
                statement = connect.createStatement();
                statement.executeUpdate(query);
                System.out.print("Data Updated *Title Updated");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Некорректные значения.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка создания задачи.");
            alert.setHeaderText("Title <= 15, description <= 50.");
            alert.showAndWait();
        }
    }
    public void update_description(Connection connect, String table_name, int taskId, String new_description) {
        Statement statement;
        if (isValidDescription(new_description)) {
            try {
                String query = String.format("update %s set description='%s' where id=%d", table_name, new_description, taskId);
                statement = connect.createStatement();
                statement.executeUpdate(query);
                System.out.println("Data Updated *Description Updated");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Некорректные значения.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка создания задачи.");
            alert.setHeaderText("Title <= 15, description <= 50.");
            alert.showAndWait();
        }
    }


    // УДАЛЕНИЕ по id
    public void delete_row_by_id(Connection connect,String table_name, int id){
        Statement statement;
        if (id > 0) {
            try{
                String query=String.format("delete from %s where id= %s",table_name,id);
                statement = connect.createStatement();
                statement.executeUpdate(query);
                System.out.println("Data Deleted");
            } catch (Exception e){ e.printStackTrace(); }
        }
        else { System.out.println("Неккоректые значения."); }
    }
    // УДАЛЕНИЕ таблицы
    public void delete_table(Connection connect, String table_name){
        Statement statement;
        try {
            String query= String.format("drop table %s",table_name);
            statement=connect.createStatement();
            statement.executeUpdate(query);
            System.out.println("Table Deleted");
        } catch (Exception e){ e.printStackTrace(); }
    }
}