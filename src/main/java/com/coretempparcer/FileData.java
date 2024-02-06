package com.coretempparcer;

import java.util.Date;
import java.util.HashMap;

public class FileData {
    private String fileName = "";
    private int stringcount;
    private HashMap<Integer, String[]> strings = new HashMap<>();

    public void setStrings(HashMap<Integer, String[]> strings) {
        this.strings = strings;
        this.stringcount = strings.size();
    }

    public FileData(String nameFile){
        this.fileName = nameFile;
    }

    public int getStringcount() {
        return stringcount;
    }

    public void setStringcount(int stringcount) {
        this.stringcount = stringcount;
    }

    public String getFileName() {
        return fileName;
    }

    public HashMap<Integer, String[]> getStrings() {
        return strings;
    }
}
