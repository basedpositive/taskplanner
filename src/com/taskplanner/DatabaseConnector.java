package com.taskplanner;

import java.sql.*;

public class DatabaseConnector {
    public Connection connect_to_db(String dbname, String user, String pass) {
        Connection connect = null;
        try {
            Class.forName("org.postgresql.Driver");
            connect = DriverManager.getConnection("jdbc:postgresql://localhost/" + dbname, user, pass);
            if(connect != null){ System.out.println("Connection Established."); }
            else System.out.println("Connection Failed");
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return connect;
    }
    public void createTable(Connection connect, String table_name){
        Statement statement;
        try{
            String query="create table "+ table_name +"(id SERIAL,title varchar(200),description varchar(200), status boolean ,duedate varchar(200));";
            statement=connect.createStatement();
            statement.executeUpdate(query);
            System.out.println("Table Created.");
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void insert_row(Connection connect, String table_name, String title, String description, boolean status){
        Statement statement;
        try {
            String query = String.format("INSERT INTO %s (title, description, status) VALUES ('%s', '%s', %b);", table_name, title, description, status);
            statement = connect.createStatement();
            statement.executeUpdate(query);
            System.out.println("Row Inserted.");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void read_data(Connection connect, String table_name){
        Statement statement;
        ResultSet rs = null;
        try {
            String query = String.format("select * from %s",table_name);
            statement = connect.createStatement();
            rs=statement.executeQuery(query);
            while(rs.next()){
                System.out.print(rs.getString("id")+" ");
                System.out.print(rs.getString("title")+" ");
                System.out.print(rs.getString("description")+" ");
                System.out.println(rs.getBoolean("status")+" ");
            }

        }
        catch (Exception e){
            System.out.println(e);
        }
    }
    public void update_title(Connection connect, String table_name, String old_title, String new_title){
        Statement statement;
        try {
            String query = String.format("update %s set title='%s' where title='%s'",table_name,new_title,old_title);
            statement = connect.createStatement();
            statement.executeUpdate(query);
            System.out.print("Data Updated");
            System.out.println(" *Title Updated");
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void update_description(Connection connect, String table_name, String old_description, String new_description){
        Statement statement;
        try {
            String query = String.format("update %s set description='%s' where description='%s'",table_name,new_description,old_description);
            statement = connect.createStatement();
            statement.executeUpdate(query);
            System.out.print("Data Updated");
            System.out.println(" *Description Updated");
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void delete_row_by_id(Connection connect,String table_name, int id){
        Statement statement;
        try{
            String query=String.format("delete from %s where id= %s",table_name,id);
            statement = connect.createStatement();
            statement.executeUpdate(query);
            System.out.println("Data Deleted");
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void delete_table(Connection connect, String table_name){
        Statement statement;
        try {
            String query= String.format("drop table %s",table_name);
            statement=connect.createStatement();
            statement.executeUpdate(query);
            System.out.println("Table Deleted");
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
