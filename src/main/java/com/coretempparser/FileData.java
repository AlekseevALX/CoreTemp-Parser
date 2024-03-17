package com.coretempparser;

import java.util.HashMap;

public class FileData {
    private String fileName = "";
    private int stringcount;
    private HashMap<Integer, String[]> strings = new HashMap<>();
    private boolean needsToFindExistingRecords = true;

    public boolean isNeedsToFindExistingRecords() {
        return needsToFindExistingRecords;
    }

    public void setNeedsToFindExistingRecords(boolean needsToFindExistingRecords) {
        this.needsToFindExistingRecords = needsToFindExistingRecords;
    }

    public void setStrings(HashMap<Integer, String[]> strings) {
        this.strings = strings;
        this.stringcount = strings.size();
    }

    public FileData(String nameFile) {
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
