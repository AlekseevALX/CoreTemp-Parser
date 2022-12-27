package com.coretempparcer;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class MainClass {

    public static volatile boolean dbChecked = false;
    public static volatile int currentWorkingThread = 0;
    public static volatile int countOfThreads = 0;
    public static volatile boolean done = true;
    public static volatile String log = "";

    public static volatile boolean auto = false;

    public static String url = "jdbc:postgresql://localhost:5432/TestDBforJava";
    public static String login = "postgres";
    public static String password = "postgres";
    public static Connection con = null;

    public static Integer countOfCores = 0;

    public static String colTime = "time";
    public static String colTemp = "temp";
    public static String colLoad = "load";
    public static String colSpeed = "speedmhz";
    public static String colCpu = "cpu";

    public static String core = "core";

    public static Integer countOfCharPoint = 30;

    public static Integer countMinutesPerAutoGraphic = 5;


    public void main(String[] args) {

        if (connectionToBase()) new justDoIt(args).start();

    }

    public static void deleteBase(){
        if (connectionToBase()) deleteDB();
    }

    public static void deleteDB(){
        Statement stm = null;
        try {
            stm = con.createStatement();
        } catch (SQLException e) {
            MainClass.writeToLog(String.valueOf(e.getStackTrace()));
            return;
        }
        try {
            stm.execute("DROP TABLE IF EXISTS CoreTemp");
        } catch (SQLException e) {
            MainClass.writeToLog(String.valueOf(e.getStackTrace()));
            return;
        }
        MainClass.writeToLog("Base CoreTemp is deleted!");
    }

    public static void writeToLog(String log){
        System.out.println(log); //D-D
        MainClass.log = MainClass.log.concat(log).concat(System.lineSeparator());
    }

    public static synchronized boolean connectionToBase() {

        MainClass.writeToLog("ConnectionToBase start currentWorkingThread:" + currentWorkingThread);

        try {
            MainClass.writeToLog("connectionToBase con validation start"); //D-D
            if (con != null && con.isValid(3)) {
                MainClass.writeToLog("connectionToBase con validation finish: return true");
                return true;
            }
        } catch (SQLException e) {
            MainClass.writeToLog("Exception in DB connection validation");
            e.printStackTrace();
            return false;
        }

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            MainClass.writeToLog("Don't find class org.postgresql.Driver!");
            MainClass.writeToLog(String.valueOf(e.getStackTrace()));
            MainClass.done = true;
            return false;
        }

        try {
            con = DriverManager.getConnection(url, login, password);
        } catch (SQLException e) {
            MainClass.writeToLog("Don't have connection to database!");
            MainClass.writeToLog(String.valueOf(e.getStackTrace()));
            MainClass.done = true;
            return false;
        }

        MainClass.writeToLog("ConnectionToBase end currentWorkingThread:" + currentWorkingThread);

        return true;
    }

    public static boolean validateColumn(String columnName) {

        if (columnName.length() < 10) return true;

//        if (columnName.substring(columnName.length()-7, columnName.length()).equals("Lowtemp") ||
//                columnName.substring(columnName.length()-8, columnName.length()).equals("Hightemp")) return false;

        if (columnName.endsWith("Lowtemp") || columnName.endsWith("Hightemp")) return false;

        return true;
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
        MainClass.writeToLog("Start: " + cal.getTime());
        long lastTimeInBase = 0;
        long timeOfStartingFile = Long.MAX_VALUE;
        Date currentDateOfStartingFile = new Date();
        int a = 0;
        mapFiles.clear();
        boolean dbDefined = false;

        ArrayList<Thread> threads = new ArrayList<>();

        String ext;

        if (directory.equals("")) {
//            directory = "C:\\Pet\\CoreTempTestData";
            return;
        }

        ext = "csv";

        File file = new File(directory);

        if (!file.exists()) {
            MainClass.writeToLog("Directory " + directory + " doesn't exist!");
            MainClass.done = true;
            return;
        }

        File[] listFiles = file.listFiles(new MainClass.FileFilter(ext));

        if (listFiles.length == 0) {
            MainClass.writeToLog("Finded 0 files in directory " + directory);
            MainClass.done = true;
            return;
        } else {
            MainClass.writeToLog("Finded " + listFiles.length + " files.");
        }

        if (!MainClass.connectionToBase()){
            MainClass.writeToLog("failed to run thread justDoIt");
            return;
        }

        dbDefined = DBChecker.dbIsDefined();

        if (dbDefined){
            findLastDateInBase();
            MainClass.writeToLog("Last record in the database is " + lastDate);
            lastTimeInBase = lastDate.getTime();
        }

        for (File f : listFiles) {
            Date dateFile = parseFileNameToDate(f.getPath());
            mapFiles.put(dateFile, f.getPath());
            if (dbDefined && (lastTimeInBase - dateFile.getTime() >= 0) && (lastTimeInBase - dateFile.getTime() < timeOfStartingFile)) {
                timeOfStartingFile = lastTimeInBase - dateFile.getTime();
                currentDateOfStartingFile = dateFile;
            }
        }

        if (dbDefined) {
            Iterator iterator = mapFiles.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<Date, String> entry = (Map.Entry<Date, String>) iterator.next();
                if (entry.getKey().getTime() < currentDateOfStartingFile.getTime()) {
                    iterator.remove();
                }
            }
        }

        for (Map.Entry<Date, String> entry : mapFiles.entrySet()) {

            StartScanning strtSc = new StartScanning(entry.getValue(), "Parcer " + a);

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

        if (MainClass.countOfThreads == 0){
            MainClass.done = true;
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

    public static void findLastDateInBase() {
        Date res = new Date();
        Statement stm;
        ResultSet resultSet = null;

        try {
            stm = MainClass.con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        String queryText = getQueryText_FindLastDate();

        try {
            resultSet = stm.executeQuery(queryText);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            while (resultSet.next()){
                lastDate = resultSet.getTimestamp(1);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        try {
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getQueryText_FindLastDate(){
        String text = "SELECT time FROM CORETEMP " +
                "ORDER BY time DESC LIMIT 1";
        return text;
    }
}