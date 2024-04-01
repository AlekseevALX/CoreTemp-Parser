package com.coretempparser;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainClass {

    public static MainClass mainObject;
    public static String computerName;
    public static volatile boolean dbChecked = false;
    private static volatile int currentWorkingThread = 0;
    private static volatile int countOfThreads = 0;
    public static volatile boolean done = true;
    public static boolean auto;
    public static volatile String log = "";
    private static final HashMap<String, String> userSettingsMap = new HashMap<>();
    private static final HashMap<String, String> systemPropertiesMap = new HashMap<>();
    private static int firstStringOfData;
    private static int[] columns;
    private static final String userSettings = "properties/UserSettings.properties";
    private static final String systemProperties = "properties/SystemProperties.properties";
    private static boolean propertiesLoaded = false;
    public static Connection connectionToDB = null;
    private static boolean logging = false;
    private static volatile Cache cache = new Cache();

    public static MainClass getInstance() {
        return Objects.requireNonNullElseGet(mainObject, MainClass::new);
    }

    public static void setLogging(boolean logging) {
        MainClass.logging = logging;
    }

    public static String getVer() {
        return "1.0";
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
            System.out.println("Can't get the computer name!");
            computerName = "noname";
        }
    }

    synchronized static void increaseCurrentWorkingThreads() {
        currentWorkingThread++;
    }

    synchronized static void decreaseCurrentWorkingThread() {
        currentWorkingThread--;
    }

    synchronized static void setCurrentWorkingThread(int a) {
        currentWorkingThread = a;
    }

    public static int getCurrentWorkingThread(){
        return currentWorkingThread;
    }

    synchronized static void increaseCountOfThreads() {
        countOfThreads++;
    }

    synchronized static void decreaseCountOfThreads() {
        countOfThreads--;
    }

    synchronized static void setCountOfThreads(int a) {
        countOfThreads = a;
    }

    public static int getCountOfThreads() {
        return countOfThreads;
    }

    public static String getComputerName() {
        return computerName;
    }

    public static String getUrlDB() {
        String res = "";
        res = res.concat("jdbc:");

        if (getTypeDB().equalsIgnoreCase("PG")) {
            res = res.concat("postgresql://");
        } else if (getTypeDB().equalsIgnoreCase("MSQL")) {
            res = res.concat("mysql://");
        }

        res = res.concat(getIPDB())
                .concat(":")
                .concat(getPortDB())
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

    public static String getPortDB() {
        return userSettingsMap.get("portDB");
    }

    public static String getTypeDB() {
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

    public static String getColdb_cpuPower() {
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

    public static String getColf_cpuPower() {
        return systemPropertiesMap.get("f_cpuPower");
    }

    public static String getTableName() {
        return userSettingsMap.get("tableName");
    }

    public static Integer getCountMinutesPerAutoGraphic() {
        return Integer.parseInt(userSettingsMap.get("countMinutesPerAutoGraphic"));
    }

    public static Integer getMaxParsingThreads() {
        return Integer.parseInt(userSettingsMap.get("maxParsingThreads"));
    }

    public static Integer getCountOfCharPoint() {
        return Integer.parseInt(userSettingsMap.get("countOfCharPoint"));
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
        if (compName.equals("")) {
            compName = MainClass.getComputerName();
        }
        return cache.getColNamesForOneComputer(compName);
    }

    public static void clearCache() {
        cache.clearCache();
    }

    public static void setLastFile(Long fileTime, Long position) {
        cache.setLastFile(fileTime, position);
    }

    public static long[] getLastFileFromCache() {
        return cache.getLastFile();
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
                properties.load(new FileInputStream("CTP/" + userSettings));
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
                properties.load(new FileInputStream("CTP/" + systemProperties));
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

    static void saveProperties() {
        Properties properties = new Properties();
        properties.putAll(userSettingsMap);
        try {
            properties.store(new FileOutputStream(userSettings), null);
        } catch (Exception e) {
            try {
                properties.store(new FileOutputStream("CTP/" + userSettings), null); //DEBUG
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        properties = new Properties();
        properties.putAll(systemPropertiesMap);

        try {
            properties.store(new FileOutputStream(systemProperties), null);
        } catch (Exception e) {
            try {
                properties.store(new FileOutputStream("CTP/" + systemProperties), null);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        if (!propertiesLoaded) loadProperties();

        if (args.length == 0) {
            System.out.println("Start CoreTemp parser without arguments");
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("-a")) {
            System.out.println("Start auto parsing");
            MainClass mainClass = new MainClass();
            mainClass.autoParsing();
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("-p")) {
            System.out.println("Start single parsing");
            MainClass mainClass = new MainClass();
            mainClass.startParseSession(false);
        }

    }

    public void autoParsing() {
        startParseSession(true);
    }

    public void startParseSession(boolean auto) {
        done = true;

        if (connectionToBase()) new ParsingSession_thread(auto).start();
    }

    public static void checkDB() {
        DBChecker dbChecker = new DBChecker();

        try {
            MainClass.dbChecked = dbChecker.checkDB();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void deleteBase() {
        if (connectionToBase()) deleteDB();
    }

    public static void deleteDB() {
        Statement stm;
        String tableName = getTableName();
        String queryText = QueryTextGenerator.getQueryText_DeleteBase(tableName);

        try {
            stm = connectionToDB.createStatement();
        } catch (SQLException e) {
            MainClass.addToLog(Arrays.toString(e.getStackTrace()));
            return;
        }
        try {
            stm.execute(queryText);
            MainClass.dbChecked = false;
        } catch (SQLException e) {
            MainClass.addToLog(Arrays.toString(e.getStackTrace()));
            return;
        }
        MainClass.addToLog("Table " + tableName + " is deleted!");
    }

    public static synchronized void addToLog(String log) {
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
            connectionToDB = DriverManager.getConnection(urlDB, loginDB, passwordDB);
        } catch (SQLException e) {
            e.printStackTrace();
            MainClass.addToLog("Don't have connection to database!");
            MainClass.addToLog(Arrays.toString(e.getStackTrace()));
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
            MainClass.addToLog("Directory to parse is not defined!");
            MainClass.done = true;
            return new File[0];
        }

        if (!file.exists()) {
            MainClass.addToLog("Directory " + directory + " doesn't exist!");
            MainClass.done = true;
            return new File[0];
        }

        File[] listFiles = file.listFiles(new MainClass.FileFilter(ext));

        if (listFiles != null && listFiles.length == 0) {
            MainClass.addToLog("Found 0 files in directory " + directory);
            MainClass.done = true;
            return listFiles;
        } else if (listFiles != null) {
            MainClass.addToLog("Found " + listFiles.length + " files.");
        } else {
            MainClass.addToLog("Found 0 files in directory " + directory);
        }

        return listFiles;
    }

    public static class FileFilter implements FilenameFilter {

        private final String ext;

        public FileFilter(String ext) {
            this.ext = ext;
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(ext);
        }
    }

}

class ParsingSession_thread extends Thread {

    private static Date lastDate = new Date(); //last date, which has been written in database

    private static final HashMap<Long, String> mapFiles = new HashMap<>();

    public ParsingSession_thread(boolean auto) {
        MainClass.auto = auto;
    }

    private static void prepareFilesToParse(long[] lastFile) {
        boolean dbDefined;
        long lastTimeInBase = 0;
        long timeOfStartingFile = Long.MAX_VALUE;
        long currentDateOfStartingFile = 0;
        mapFiles.clear();
        dbDefined = DBChecker.dbIsDefined();
        File[] listFiles = MainClass.getListLogFiles();

        if (listFiles.length == 0) return;

        if (lastFile[0] == 0) {
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
                long fTime = parseFileNameToDate(f.getPath());
                mapFiles.put(fTime, f.getPath());
                if (dbDefined && (lastTimeInBase - fTime >= 0) && (lastTimeInBase - fTime < timeOfStartingFile)) {
                    timeOfStartingFile = lastTimeInBase - fTime;
                    currentDateOfStartingFile = fTime;
                }
            }

            if (dbDefined && lastTimeInBase > 0) {
                Iterator<Map.Entry<Long, String>> iterator = mapFiles.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<Long, String> entry = iterator.next();
                    if (entry.getKey() < currentDateOfStartingFile) {
                        iterator.remove();
                    }
                }
            }
        } else {
            for (File f : listFiles) {
                long fTime = parseFileNameToDate(f.getPath());
                if (fTime >= lastFile[0]) {
                    mapFiles.put(fTime, f.getPath());
                }

            }
        }
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
            MainClass.addToLog("************************************************** ");
            MainClass.addToLog("Start new parsing session ************************ ");
            MainClass.done = false;

            int maxParsingThreads = MainClass.getMaxParsingThreads();

            long[] lastFile = MainClass.getLastFileFromCache();

            prepareFilesToParse(lastFile);

            if (mapFiles.isEmpty()) {
                MainClass.done = true;
                return;
            }

            Calendar cal = new GregorianCalendar();
            MainClass.addToLog("Start: " + cal.getTime());

            int a = 0;

            ArrayList<Thread> threads = new ArrayList<>();

            if (!MainClass.connectionToBase()) {
                MainClass.addToLog("failed to run thread ParsingSession_thread - don't connection to base");
                return;
            }

            DBChecker.checkDBColumns();

            for (Map.Entry<Long, String> entry : mapFiles.entrySet()) {

                ParsingFile_thread fileParseThread = new ParsingFile_thread(entry.getValue(), lastFile, "ParserThread " + a);

                threads.add(fileParseThread);
                a += 1;
                MainClass.increaseCountOfThreads();
            }

            for (Thread t : threads) {
                while (MainClass.getCurrentWorkingThread() >= maxParsingThreads) {
                    //wait
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                t.start();
                MainClass.increaseCurrentWorkingThreads();

            }

            if (MainClass.getCountOfThreads() == 0) {
                MainClass.done = true;
                MainClass.closeConnection();
            }
        }
        while (MainClass.auto);

    }

    public static long parseFileNameToDate(String s) {
        String[] spltStr = s.split("CT-Log");
        spltStr[1] = spltStr[1].substring(1);
        int hour = Integer.parseInt(spltStr[1].substring(11, 13));
        int minute = Integer.parseInt(spltStr[1].substring(14, 16));
        int second = Integer.parseInt(spltStr[1].substring(17, 19));

        int month = Integer.parseInt(spltStr[1].substring(5, 7));
        int date = Integer.parseInt(spltStr[1].substring(8, 10));
        int year = Integer.parseInt(spltStr[1].substring(0, 4));

        GregorianCalendar calendar = new GregorianCalendar(); ///.set(year, month, date, hour, minute, second);
        calendar.set(year, month - 1, date, hour, minute, second);

        return calendar.getTimeInMillis() / 1000;
    }

    public static void findLastDateInBase() {
        Statement stm;
        ResultSet resultSet = null;

        try {
            stm = MainClass.connectionToDB.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        String queryText = QueryTextGenerator.getQueryText_FindLastDate();

        try {
            resultSet = stm.executeQuery(queryText);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (resultSet != null && resultSet.next()) {
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

}