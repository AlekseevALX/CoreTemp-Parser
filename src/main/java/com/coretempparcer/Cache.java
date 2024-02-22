package com.coretempparcer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Cache {
    private HashMap<String, String[]> colNames = new HashMap<>();

    private HashMap<String, Integer> countOfCores = new HashMap<>();

    public String[] getColNamesForOneComputer(String compName) {
        String[] res = colNames.get(compName);

        if (res == null || res.length == 0) {
            if (compName.equals(MainClass.getComputerName())) {
                findColNamesInLogs();
                res = colNames.get(compName);
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

    private void findColNamesInLogs() {
        File[] listFiles = MainClass.getListLogFiles();
        try {
            FileParcer.readColumnSettingsFromFile(listFiles[0].getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
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

}
