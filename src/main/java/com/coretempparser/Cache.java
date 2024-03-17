package com.coretempparser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Cache {
    private HashMap<String, String[]> colNames = new HashMap<>();

    private HashMap<String, Integer> countOfCores = new HashMap<>();

    private Long elapsedTime = (long) 0;

    //lastFile[0] - file time in millis (Date dateFile.getTime())
    //lastFile[1] - index of position, where file reading had stopped last time
    private long[] lastFile = new long[2];

    public Cache() {
        this.lastFile[0] = 0;
        this.lastFile[1] = 0;
    }

    public void setLastFile(Long filetime, Long position) {
        synchronized (Cache.class) {
            if (filetime >= lastFile[0]) {
                lastFile[0] = filetime;
                lastFile[1] = position;
            }
        }
    }

    public long[] getLastFile() {
        return lastFile;
    }

    public Long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void clearElapsedTime() {
        elapsedTime = (long) 0;
    }

    public String[] getColNamesForOneComputer(String compName) {
        String[] res = colNames.get(compName);

        if (res == null || res.length == 0) {
            if (compName.equals(MainClass.getComputerName())) {
                try {
                    findColNamesInLogs();
                    res = colNames.get(compName);
                } catch (IOException e) {
                    res = findColNamesInDB(compName);
                    colNames.put(compName, res);
                }

            } else {
                res = findColNamesInDB(compName);
                colNames.put(compName, res);
            }
        }
        return res;
    }

    public void setColNamesForOneComputer(String compName, String[] names) {
        colNames.put(compName, names);
    }

    private String[] findColNamesInDB(String compName) {
        DBReader dbReader = new DBReader();

        return dbReader.getColumnNamesFromDataBase(compName);
    }

    private void findColNamesInLogs() throws IOException {

        File[] listFiles = MainClass.getListLogFiles();

        if (listFiles.length > 0) {
            FileParser.readColumnSettingsFromFile(listFiles[0].getAbsolutePath());
        }

    }

    public Integer getCountOfCoresForOneComputer(String compName) {

        Integer count = countOfCores.get(compName);

        if (count == null) {
            count = defineCountOfCores(getColNamesForOneComputer(compName));
            countOfCores.put(compName, count);
        }

        return count;
    }

    private Integer defineCountOfCores(String[] columns) {
        int currCore = 0;
        int countOfCores = 0;
        String colCore = MainClass.getColdb_core();

        if (countOfCores == 0) {
            for (String s : columns) {
                if (s.length() < 5) continue;
                if ((s.toUpperCase().contains(colCore.toUpperCase())) && Integer.parseInt(s.substring(4, 5)) > currCore) {
                    currCore = Integer.parseInt(s.substring(4, 5));
                }
            }
            countOfCores = currCore + 1;
        }
        return countOfCores;
    }

    public void clearCache() {
        colNames.clear();
        countOfCores.clear();
        elapsedTime = (long) 0;
        lastFile[0] = 0;
        lastFile[1] = 0;
    }
}
