package com.coretempparcer;

import javafx.scene.shape.Path;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainClass {

    public static String computerName;
    public static volatile boolean dbChecked = false;
    public static volatile int currentWorkingThread = 0;
    public static volatile int countOfThreads = 0;
    public static volatile boolean done = true;
    public static boolean auto;
    public static volatile String log = "";
    private static HashMap<String, String> userSettingsMap = new HashMap<>();
    private static HashMap<String, String> systemPropertiesMap = new HashMap<>();
    private static String ver = "1.0";
    private static int firstStringOfData;
    private static int[] columns;
    private static String userSettings = "properties/UserSettings.properties";
    private static String systemProperties = "properties/SystemProperties.properties";
    private static boolean propertiesLoaded = false;
    public static Connection connectionToDB = null;
    private static boolean logging = false;
    private static Cache cache = new Cache();
    public static void setLogging(boolean logging) {
        MainClass.logging = logging;
    }

    public static String getVer() {
        return ver;
    }

    public static HashMap<String, String> getUserSettingsMap() {
        return userSettingsMap;
    }

    public static HashMap<String, String> getSystemPropertiesMap() {
        return systemPropertiesMap;
    }

    private static void initializeComputerName() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            computerName = address.getHostName();
        } catch (UnknownHostException e) {
            addToLog("Can't get the computer name!");
            computerName = "noname";
        }
    }

    public static String getComputerName() {
        return computerName;
    }

    public static String getUrlDB() {
        String res = new String();
        res = res.concat("jdbc:");

        if (gettypeDB().toUpperCase().equals("PG")) {
            res = res.concat("postgresql://");
        } else if (gettypeDB().toUpperCase().equals("MSQL")) {
            res = res.concat("postgresql://"); //write right value later
        }

        res = res.concat(getIPDB())
                .concat(":")
                .concat(getportDB())
                .concat("/")
                .concat(getDBName());
        return res;
    }

    public static String getIPDB() {
        return userSettingsMap.get("IPDB");
    }

    public static String getDBName() {
        return userSettingsMap.get("DBName");
    }

    public static String getportDB() {
        return userSettingsMap.get("portDB");
    }

    public static String gettypeDB() {
        return userSettingsMap.get("typeDB");
    }

    public static String getLoginDB() {
        return userSettingsMap.get("loginDB");
    }

    public static String getPasswordDB() {
        return userSettingsMap.get("passwordDB");
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

    public static String getColdb_core() {
        return systemPropertiesMap.get("db_core");
    }

    public static String getColdb_cpupower() {
        return systemPropertiesMap.get("db_cpuPower");
    }

    public static String getColf_time() {
        return systemPropertiesMap.get("f_time");
    }

    public static String getColf_core() {
        return systemPropertiesMap.get("f_core");
    }

    public static String getColf_temp() {
        return systemPropertiesMap.get("f_temp");
    }

    public static String getColf_load() {
        return systemPropertiesMap.get("f_load");
    }

    public static String getColf_speed() {
        return systemPropertiesMap.get("f_speed");
    }

    public static String getColf_cpupower() {
        return systemPropertiesMap.get("f_cpuPower");
    }

    public static String getTableName() {
        return userSettingsMap.get("tableName");
    }

    public static Integer getCountMinutesPerAutoGraphic() {
        return Integer.parseInt(userSettingsMap.get("countMinutesPerAutoGraphic"));
    }

    public static Integer getMaxParcingThreads() {
        return Integer.parseInt(userSettingsMap.get("maxParcingThreads"));
    }

    public static Integer getCountOfCharPoint() {
        return Integer.parseInt(userSettingsMap.get("countOfCharPoint"));
    }

    public static int getCountOfCores(String compName) {
        return cache.getCountOfCoresForOneComputer(compName);
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

    public static int[] getColumns() {
        return columns;
    }

    public static void setColumns(int[] columns) {
        MainClass.columns = columns;
    }

    public static String[] getColNames(String compName) {
        if (compName.equals("")){
            compName = MainClass.getComputerName();
        }
        return cache.getColNamesForOneComputer(compName);
    }

    public static void setColNames(String compName, String[] colNames) {
        cache.setColNamesForOneComputer(compName, colNames);
    }

    public static String getColCompName() {
        return "CompName";
    }

    static void loadProperties() {

        Properties properties = new Properties();
        //for debug
//        properties.load(new FileInputStream(userSettings));
        try {
            properties.load(new FileInputStream(userSettings));
        } catch (Exception e) {
            try {
                properties.load(new FileInputStream("target/" + userSettings));
            } catch (Exception e1) {
                //none
            }
        }

        for (String name : properties.stringPropertyNames()) {
            userSettingsMap.put(name, properties.getProperty(name));
        }

        properties = new Properties();
        //for debug
//        properties.load(new FileInputStream(systemProperties));

        try {
            properties.load(new FileInputStream(systemProperties));
        } catch (Exception e) {
            try {
                properties.load(new FileInputStream("target/" + systemProperties));
            } catch (Exception e1) {
                //none
            }
        }

        for (String name : properties.stringPropertyNames()) {
            systemPropertiesMap.put(name, properties.getProperty(name));
        }

        initializeComputerName();

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

    public static void main(String[] args) throws IOException {
        if (!propertiesLoaded) loadProperties();

        if (args.length == 0) {
            System.out.println("Start CoreTemp parcer without arguments");
        }

        if (args.length > 0 && args[0].toUpperCase().equals("-a".toUpperCase())) {
            System.out.println("Start auto parcing");
            MainClass mainClass = new MainClass();
            mainClass.autoParcing();
        }

        if (args.length > 0 && args[0].toUpperCase().equals("-p".toUpperCase())) {
            System.out.println("Start single parcing");
            MainClass mainClass = new MainClass();
            mainClass.startParceSession(false);
        }

    }

    public void autoParcing() {
        startParceSession(true);
    }

    public void startParceSession(boolean auto) {
        done = true;

        if (connectionToBase()) new ParcingSession_thread(auto).start();
    }

    public static void checkDB() {
        DBChecker dbChecker = new DBChecker();

        try {
            dbChecker.checkDB();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        MainClass.dbChecked = true;
    }

    public static void deleteBase() {
        if (connectionToBase()) deleteDB();
    }

    public static void deleteDB() {
        Statement stm;
        String tableName = getTableName();
        try {
            stm = connectionToDB.createStatement();
        } catch (SQLException e) {
            MainClass.addToLog(String.valueOf(e.getStackTrace()));
            return;
        }
        try {
            stm.execute("DROP TABLE IF EXISTS " + tableName);
            MainClass.dbChecked = false;
        } catch (SQLException e) {
            MainClass.addToLog(String.valueOf(e.getStackTrace()));
            return;
        }
        MainClass.addToLog("Base " + tableName + " is deleted!");
    }

    public static void addToLog(String log) {
        if (!logging) return;
        MainClass.log = MainClass.log.concat(log).concat(System.lineSeparator());
    }

    public static void clearLog() {
        log = "";
    }

    public static synchronized boolean connectionToBase() {
        String urlDB = getUrlDB();
        String loginDB = getLoginDB();
        String passwordDB = getPasswordDB();

        try {
            if (connectionToDB != null && connectionToDB.isValid(3)) {
                return true;
            }
        } catch (SQLException e) {
            MainClass.addToLog("Exception in DB connection validation");
            e.printStackTrace();
            return false;
        }

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            MainClass.addToLog("Don't find class org.postgresql.Driver!");
            MainClass.addToLog(String.valueOf(e.getStackTrace()));
            MainClass.done = true;
            return false;
        }

        try {
            connectionToDB = DriverManager.getConnection(urlDB, loginDB, passwordDB);
        } catch (SQLException e) {
            e.printStackTrace();
            MainClass.addToLog("Don't have connection to database!");
            MainClass.addToLog(String.valueOf(e.getStackTrace()));
            MainClass.done = true;
            return false;
        }
        return true;
    }

    public static void closeConnection() {

        try {
            if (connectionToDB != null && !connectionToDB.isClosed()) {
                connectionToDB.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static File[] getListLogFiles() {
        String ext = "csv";
        String directory = MainClass.getDirectoryWithCTLogs();
        File file = new File(directory);

        if (directory.equals("")) {
            MainClass.addToLog("Directory to parce is not defined!");
            MainClass.done = true;
            return new File[0];
        }

        if (!file.exists()) {
            MainClass.addToLog("Directory " + directory + " doesn't exist!");
            MainClass.done = true;
            return new File[0];
        }

        File[] listFiles = file.listFiles(new MainClass.FileFilter(ext));

        if (listFiles.length == 0) {
            MainClass.addToLog("Finded 0 files in directory " + directory);
            MainClass.done = true;
            return listFiles;
        } else {
            MainClass.addToLog("Finded " + listFiles.length + " files.");
        }

        return listFiles;
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

    private static Date lastDate = new Date(); //last date, which has been written in database
    private static HashMap<Date, String> mapFiles = new HashMap<>();
    private static String directory = "";

    public ParcingSession_thread(boolean auto) {
        MainClass.auto = auto;
    }

    public void run() {
        do {

            if (!MainClass.done) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }

            MainClass.done = false;

            int maxParcingThreads = MainClass.getMaxParcingThreads();

            Calendar cal = new GregorianCalendar();
            MainClass.addToLog("Start: " + cal.getTime());
            long lastTimeInBase = 0;
            long timeOfStartingFile = Long.MAX_VALUE;
            Date currentDateOfStartingFile = new Date();
            int a = 0;
            mapFiles.clear();
            boolean dbDefined;

            ArrayList<Thread> threads = new ArrayList<>();

            File[] listFiles = MainClass.getListLogFiles();

            if (listFiles.length == 0) return;

            if (!MainClass.connectionToBase()) {
                MainClass.addToLog("failed to run thread ParcingSession_thread - don't connection to base");
                return;
            }

            dbDefined = DBChecker.dbIsDefined();

            if (dbDefined) {
                findLastDateInBase();
                if (lastDate != null) {
                    MainClass.addToLog("Last record in the database is " + lastDate);
                    lastTimeInBase = lastDate.getTime();
                } else {
                    MainClass.addToLog("No records in database from computer " + MainClass.getComputerName() + " yet ");
                }


            }

            for (File f : listFiles) {
                Date dateFile = parseFileNameToDate(f.getPath());
                mapFiles.put(dateFile, f.getPath());
                if (dbDefined && (lastTimeInBase - dateFile.getTime() >= 0) && (lastTimeInBase - dateFile.getTime() < timeOfStartingFile)) {
                    timeOfStartingFile = lastTimeInBase - dateFile.getTime();
                    currentDateOfStartingFile = dateFile;
                }
            }

            if (dbDefined && lastTimeInBase > 0) {
                Iterator iterator = mapFiles.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<Date, String> entry = (Map.Entry<Date, String>) iterator.next();
                    if (entry.getKey().getTime() < currentDateOfStartingFile.getTime()) {
                        iterator.remove();
                    }
                }
            }

            DBChecker.checkDBColumns();

            for (Map.Entry<Date, String> entry : mapFiles.entrySet()) {

                ParcingFile_thread strtSc = new ParcingFile_thread(entry.getValue(), "Parcer " + a);

                threads.add(strtSc);
                a += 1;
                MainClass.countOfThreads += 1;
            }

            for (Thread t : threads) {
                while (MainClass.currentWorkingThread >= maxParcingThreads) {
                    //wait
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {

                    }
                }
                t.start();
                MainClass.currentWorkingThread += 1;

            }

            if (MainClass.countOfThreads == 0) {
                MainClass.done = true;
            }
        }
        while (MainClass.auto);

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
            stm = MainClass.connectionToDB.createStatement();
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
            if (resultSet.next()) {
                lastDate = resultSet.getTimestamp(1);
            } else {
                lastDate = null;
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
        String colTime = MainClass.getColdb_time();
        String colComp = MainClass.getColCompName();
        String tableName = MainClass.getTableName();
        String thisCompName = MainClass.getComputerName();
        String text = "";

        text = text.concat("SELECT ")
                .concat(colTime)
                .concat(" FROM ")
                .concat(tableName)
                .concat(" WHERE ")
                .concat(colComp)
                .concat(" = ")
                .concat("'")
                .concat(thisCompName)
                .concat("'")
                .concat(" ORDER BY time DESC LIMIT 1");

        return text;
    }
}