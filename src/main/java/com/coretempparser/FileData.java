package com.coretempparser;

import java.util.HashMap;

public class FileData {
    private final String fileName;
    private int stringCount;
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
        this.stringCount = strings.size();
    }

    public FileData(String nameFile) {
        this.fileName = nameFile;
    }

    public int getStringCount() {
        return stringCount;
    }

    public void setStringCount(int stringCount) {
        this.stringCount = stringCount;
    }

    public String getFileName() {
        return fileName;
    }

    public HashMap<Integer, String[]> getStrings() {
        return strings;
    }
}
