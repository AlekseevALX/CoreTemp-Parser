package main;
//start repository

import java.io.*;
import java.sql.*;
import java.time.Month;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

public class start implements Runnable {
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String fileName = "";

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

    @Override
    public void run() {

        System.out.println("Starting thread to read file " + fileName);

        if (fileName.equals("")) return;

        FileData fd = null;

        try {
            fd = FileParcer.parceFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DBChecker dbChecker = new DBChecker(fd.getColumns());

        dbChecker.checkDB();

        DBWriter dbWriter = new DBWriter(fd);

        dbWriter.writeToBase();
    }
}
