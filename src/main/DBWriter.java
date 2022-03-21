package main;

import java.io.*;
import java.sql.*;

public class DBWriter {
    private FileData fileData;

    public DBWriter(FileData fileData) {
        this.fileData = fileData;
    }

    public FileData getFileData() {
        return fileData;
    }

    public void setFileData(FileData fileData) {
        this.fileData = fileData;
    }

    public static void writeToBase() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String url = "jdbc:postgresql://localhost:5432/TestDBforJava";
        String login = "postgres";
        String password = "postgres";

        Connection con = null;
        try {
            con = DriverManager.getConnection(url, login, password);
            Statement stm = con.createStatement();
//            ResultSet res = stm.executeQuery("SELECT * from JavaTest");  //examples
//            stm.execute("INSERT INTO JavaTest (FIRST_NAME, LAST_NAME) VALUES ('Homyachok','Homa')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        res.close();
//        stm.close();
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
