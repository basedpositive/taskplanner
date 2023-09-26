package com.taskplanner;

import java.sql.*;

public class DatabaseConnector {
    public Connection connect_to_db(String dbname, String user, String pass) {
        Connection connect = null;
        try {
            connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + dbname, user, pass); // почему-то при "localhost/5432" вызывало ошибку
            if(connect != null){ System.out.println("Connection Established."); }
            else System.out.println("Connection Failed.");
        } catch(Exception e) { e.printStackTrace(); }

        return connect;
    }

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

    public void insert_row(Connection connect, String table_name, String title, String description, String status, Timestamp createdAt, Timestamp dueDate) {
        Statement statement;
        if (isValidTitle(title) && isValidDescription(description)) {
            try {
                String query = String.format("INSERT INTO %s (title, description, status, createdAt, dueDate) VALUES ('%s', '%s', '%s', '%s', '%s');", table_name, title, description, status, createdAt, dueDate);
                statement = connect.createStatement();
                statement.executeUpdate(query);
                System.out.println("Row Inserted.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Некорректные значения.");
        }
    }

    private boolean isValidTitle(String title) {
        return title != null && !title.isEmpty() && title.length() <= 15; // sql...
    }
    private boolean isValidDescription(String description) {
        return description != null && description.length() <= 50; // sql...
    }

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
        }
    }


    // DELETION
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