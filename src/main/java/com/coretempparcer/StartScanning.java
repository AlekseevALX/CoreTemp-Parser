package com.coretempparcer;
//start repository

import javafx.scene.control.Label;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.sql.*;

public class StartScanning extends Thread {
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String fileName = "";

    public StartScanning(String fileName, String threadName) {
        super(threadName);
        this.fileName = fileName;
    }

    //testing functions+
    public static void testBase() {
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/TestDBforJava";
            String login = "postgres";
            String password = "postgres";
            Connection con = DriverManager.getConnection(url, login, password);
            Statement stm = con.createStatement();
            ResultSet res = stm.executeQuery("SELECT * from JavaTest");
            while (res.next()) {
                System.out.println(res.getString(1) + " " + res.getString(2) + " " + res.getString(3));
            }

            stm.execute("INSERT INTO JavaTest (FIRST_NAME, LAST_NAME) VALUES ('Homyachok','Homa')");


            res = stm.executeQuery("SELECT * from JavaTest");
            while (res.next()) {
                System.out.println(res.getString(1) + " " + res.getString(2) + " " + res.getString(3));
            }

            res.close();
            stm.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    public static void deleteBase() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://localhost:5432/TestDBforJava";
        String login = "postgres";
        String password = "postgres";
        Connection con = DriverManager.getConnection(url, login, password);
        Statement stm = con.createStatement();
        stm.execute("DROP TABLE IF EXISTS CoreTemp");
    }
    //testing functions-

    @Override
    public void run() {

        if (fileName.equals("")){
            endOfthread();
            return;
        }

        FileData fd = null;

        try {
            fd = FileParcer.parceFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fd.getStringcount() == 0) {
//            System.out.println("File " + fileName + " don't have any strings");
            endOfthread();
            return;
        }
        synchronized (StartScanning.class) {
            if (!MainClass.dbChecked) {
                DBChecker dbChecker = new DBChecker(fd.getColumns());

                dbChecker.checkDB();

                MainClass.dbChecked = true;
            }
        }

        DBWriter dbWriter = new DBWriter(fd);

        synchronized (StartScanning.class) {
            try {
                dbWriter.writeToBase();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        endOfthread();

    }

    void endOfthread(){
        synchronized (StartScanning.class){
            MainClass.currentWorkingThread -= 1;
            MainClass.countOfThreads -= 1;
            if (MainClass.countOfThreads == 0){
                MainClass.done = true;
                System.out.println("Done");
            }
        }
    }
}
