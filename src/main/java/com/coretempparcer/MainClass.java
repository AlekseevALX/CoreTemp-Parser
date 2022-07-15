package com.coretempparcer;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class MainClass {

    public static volatile boolean dbChecked = false;
    public static volatile int currentWorkingThread = 0;
    public static volatile int countOfThreads = 0;
    public static volatile boolean done = false;

    public static String url = "jdbc:postgresql://localhost:5432/TestDBforJava";
    public static String login = "postgres";
    public static String password = "postgres";
    public static Connection con = null;


    public void main(String[] args) {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Don't find class org.postgresql.Driver!");
            e.printStackTrace();
            MainClass.done = true;
            return;
        }

        try {
            con = DriverManager.getConnection(url, login, password);
        } catch (SQLException e) {
            System.out.println("Don't have connection to database!");
            e.printStackTrace();
            MainClass.done = true;
            return;
        }

        new justDoIt(args).start();

    }

    public static class FileFilter implements FilenameFilter {

        private String ext;

        public FileFilter(String ext) {
            this.ext = ext;
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(ext);
        }
    }

}

class justDoIt extends Thread {

    public static Date lastDate = new Date(); //last date, which has been written in database
    public static HashMap<Date, String> mapFiles = new HashMap<>();

    String[] args;
    public static String directory = "";


    public justDoIt(String[] args) {
        this.args = args;
    }

    public void run() {

        if (args.length != 0) {
            directory = args[0];
        }

        Calendar cal = new GregorianCalendar();
        System.out.println("Start: " + cal.getTime());
        Date strt = cal.getTime();

        ArrayList<Thread> threads = new ArrayList<>();

        String ext;

        if (directory.equals("")) {
            directory = "C:\\Pet\\CoreTempTestData";
        }

        ext = "csv";

        File file = new File(directory);

        if (!file.exists()) {
            System.out.println("Directory " + directory + " doesn't exist!");
            MainClass.done = true;
            return;
        }

        File[] listFiles = file.listFiles(new MainClass.FileFilter(ext));

        if (listFiles.length == 0) {
            System.out.println("Finded 0 files in directory " + directory);
            MainClass.done = true;
            return;
        } else {
            System.out.println("Finded " + listFiles.length + " files.");
        }

        int a = 0;

        for (File f : listFiles) {

//            Date dateFile = parseFileNameToDate(f.getPath());
//            mapFiles.put(dateFile, f.getPath());

            StartScanning strtSc = new StartScanning(f.getPath(), "Parcer " + a);

            threads.add(strtSc);
            a += 1;
            MainClass.countOfThreads += 1;

        }

        for (Thread t : threads) {
            while (MainClass.currentWorkingThread > 50){
                //wait
            }
            t.start();
            MainClass.currentWorkingThread += 1;

        }

    }

    public static Date parseFileNameToDate(String s){
        String[] spltStr = s.split("CT-Log");
        spltStr[1] = spltStr[1].substring(1, spltStr[1].length());
        Integer hour = Integer.parseInt(spltStr[1].substring(11,13));
        Integer minute = Integer.parseInt(spltStr[1].substring(14,16));
        Integer second = Integer.parseInt(spltStr[1].substring(17,19));

        Integer month = Integer.parseInt(spltStr[1].substring(5,7));
        Integer date = Integer.parseInt(spltStr[1].substring(8,10));
        Integer year = Integer.parseInt(spltStr[1].substring(0,4));

        GregorianCalendar calendar = new GregorianCalendar(); ///.set(year, month, date, hour, minute, second);
        calendar.set(year, month - 1, date, hour, minute, second);
        Date res = calendar.getTime();

        return res;
    }
}