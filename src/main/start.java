package main;
//start repository

import java.io.*;
import java.time.Month;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.regex.Pattern;

public class start {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello! This is parcer for CoreTemp");

        FileData fd = FileParcer.parceFile();

        DBChecker dbChecker = new DBChecker(fd.getColumns());

        dbChecker.checkDB();

        DBWriter dbWriter = new DBWriter(fd);

    }




    public static void writeToBase(FileData fd) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {

        } finally {

        }
    }

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
}
