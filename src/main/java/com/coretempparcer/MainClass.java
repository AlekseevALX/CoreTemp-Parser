package com.coretempparcer;

import java.io.*;
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
    private static HashMap<String, String> userSettingsMap = new HashMap<>();
    private static HashMap<String, String> systemPropertiesMap = new HashMap<>();
    private static String ver = "1.0";
    private static int firstStringOfData;
    private static HashMap<Integer, String> columnsLocations = new HashMap<>();
    private static boolean columnsSettingsIsReaded = false;

    private static String userSettings = "C:\\Pet\\CoreTempParcer\\src\\main\\java\\com\\coretempparcer\\properties\\UserSettings.properties";
    private static String systemProperties = "C:\\Pet\\CoreTempParcer\\src\\main\\java\\com\\coretempparcer\\properties\\SystemProperties.properties";

    private static boolean propertiesLoaded = false;
    public static Connection con = null;

    private static int countOfCores;

    public static String getVer() {
        return ver;
    }

    public static HashMap<String, String> getUserSettingsMap() {
        return userSettingsMap;
    }

    public static void setUserSettingsMap(HashMap<String, String> userSettingsMap) {
        MainClass.userSettingsMap = userSettingsMap;
    }

    public static HashMap<String, String> getSystemPropertiesMap() {
        return systemPropertiesMap;
    }

    public static String getUrlDB() {
        return userSettingsMap.get("urlDB");
    }

    public static void setUrlDB(String urlDB) {
        userSettingsMap.put("urlDB", urlDB);
    }

    public static String getLoginDB() {
        return userSettingsMap.get("loginDB");
    }

    public static void setLoginDB(String loginDB) {
        userSettingsMap.put("loginDB", loginDB);
    }

    public static String getPasswordDB() {
        return userSettingsMap.get("passwordDB");
    }

    public static void setPasswordDB(String passwordDB) {
        userSettingsMap.put("passwordDB", passwordDB);
    }

    public static String getDirectoryWithCTLogs() {
        return userSettingsMap.get("directoryWithCTLogs");
    }

    public static String getColdb_time() {
        return systemPropertiesMap.get("db_time");
    }

    public static String getColdb_temp() {
        return systemPropertiesMap.get("db_temp");
    }

    public static String getColdb_load() {
        return systemPropertiesMap.get("db_load");
    }

    public static String getColdb_speed() {
        return systemPropertiesMap.get("db_speed");
    }

    public static String getColdb_cpu() {
        return systemPropertiesMap.get("db_cpu");
    }

    public static String getColdb_core() {
        return systemPropertiesMap.get("db_core");
    }

    public static String getTableName() {
        return userSettingsMap.get("tableName");
    }

    public static Integer getCountOfCharPoint() {
        return Integer.parseInt(userSettingsMap.get("countOfCharPoint"));
    }

    public static Integer getCountMinutesPerAutoGraphic() {
        return Integer.parseInt(userSettingsMap.get("countMinutesPerAutoGraphic"));
    }

    public static Integer getMaxParcingThreads() {
        return Integer.parseInt(userSettingsMap.get("maxParcingThreads"));
    }

    public static int getCountOfCores() {
        return countOfCores;
    }

    public static void setCountOfCores(int countOfCores) {
        MainClass.countOfCores = countOfCores;
    }

    public static boolean isPropertiesLoaded() {
        return propertiesLoaded;
    }

    public static int getFirstStringOfData() {
        return firstStringOfData;
    }

    public static void setFirstStringOfData(int firstStringOfData) {
        MainClass.firstStringOfData = firstStringOfData;
    }

    public static HashMap<Integer, String> getColumnsLocations() {
        return columnsLocations;
    }

    public static void setColumnsLocations(HashMap<Integer, String> columnsLocations) {
        MainClass.columnsLocations = columnsLocations;
    }

    public static boolean isColumnsSettingsIsReaded() {
        return columnsSettingsIsReaded;
    }

    public static void setColumnsSettingsIsReaded(boolean columnsSettingsIsReaded) {
        MainClass.columnsSettingsIsReaded = columnsSettingsIsReaded;
    }

    static void loadProperties() throws IOException {

        Properties properties = new Properties();
        properties.load(new FileInputStream(userSettings));

        for (String name : properties.stringPropertyNames()) {
            userSettingsMap.put(name, properties.getProperty(name));
        }

        properties = new Properties();
        properties.load(new FileInputStream(systemProperties));

        for (String name : properties.stringPropertyNames()) {
            systemPropertiesMap.put(name, properties.getProperty(name));
        }

        propertiesLoaded = true;
    }

    static void saveProperties() throws IOException {
        Properties properties = new Properties();
        properties.putAll(userSettingsMap);
        properties.store(new FileOutputStream(userSettings), null);

        properties = new Properties();
        properties.putAll(systemPropertiesMap);
        properties.store(new FileOutputStream(systemProperties), null);
    }

    public void main(String[] args) throws IOException {
        if (!propertiesLoaded) loadProperties();
        if (connectionToBase()) new ParcingSession_thread(args).start();

    }

    public static void deleteBase() {
        if (connectionToBase()) deleteDB();
    }

    public static void deleteDB() {
        Statement stm = null;
        String tableName = getTableName();
        try {
            stm = con.createStatement();
        } catch (SQLException e) {
            MainClass.writeToLog(String.valueOf(e.getStackTrace()));
            return;
        }
        try {
            stm.execute("DROP TABLE IF EXISTS " + tableName);
        } catch (SQLException e) {
            MainClass.writeToLog(String.valueOf(e.getStackTrace()));
            return;
        }
        MainClass.writeToLog("Base " + tableName + " is deleted!");
    }

    public static void writeToLog(String log) {
        System.out.println(log); //D-D
        MainClass.log = MainClass.log.concat(log).concat(System.lineSeparator());
    }

    public static synchronized boolean connectionToBase() {
        String urlDB = getUrlDB();
        String loginDB = getLoginDB();
        String passwordDB = getPasswordDB();

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
            con = DriverManager.getConnection(urlDB, loginDB, passwordDB);
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

class ParcingSession_thread extends Thread {

    public static Date lastDate = new Date(); //last date, which has been written in database
    public static HashMap<Date, String> mapFiles = new HashMap<>();

    String[] args;
    public static String directory = "";


    public ParcingSession_thread(String[] args) {
        this.args = args;
    }

    public void run() {

        int maxParcingThreads = MainClass.getMaxParcingThreads();

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
            MainClass.writeToLog("Directory to parce is not defined!");
            MainClass.done = true;
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

        if (!MainClass.connectionToBase()) {
            MainClass.writeToLog("failed to run thread ParcingSession_thread - don't connection to base");
            return;
        }

        dbDefined = DBChecker.dbIsDefined();

        if (dbDefined) {
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

        //HERE realize to define column properties

        for (Map.Entry<Date, String> entry : mapFiles.entrySet()) {

            ParcingFile_thread strtSc = new ParcingFile_thread(entry.getValue(), "Parcer " + a);

            threads.add(strtSc);
            a += 1;
            MainClass.countOfThreads += 1;
        }

        for (Thread t : threads) {
            while (MainClass.currentWorkingThread >= maxParcingThreads) {
                //wait
            }
            t.start();
            MainClass.currentWorkingThread += 1;

        }

        if (MainClass.countOfThreads == 0) {
            MainClass.done = true;
        }

    }

    public static Date parseFileNameToDate(String s) {
        String[] spltStr = s.split("CT-Log");
        spltStr[1] = spltStr[1].substring(1, spltStr[1].length());
        Integer hour = Integer.parseInt(spltStr[1].substring(11, 13));
        Integer minute = Integer.parseInt(spltStr[1].substring(14, 16));
        Integer second = Integer.parseInt(spltStr[1].substring(17, 19));

        Integer month = Integer.parseInt(spltStr[1].substring(5, 7));
        Integer date = Integer.parseInt(spltStr[1].substring(8, 10));
        Integer year = Integer.parseInt(spltStr[1].substring(0, 4));

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
            while (resultSet.next()) {
                lastDate = resultSet.getTimestamp(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getQueryText_FindLastDate() {
        String text = "SELECT time FROM CORETEMP " + "ORDER BY time DESC LIMIT 1";
        return text;
    }
}