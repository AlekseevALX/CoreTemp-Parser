package main;

import java.util.Date;
import java.util.HashMap;

public class FileData {
    private String fileName = "";
    private String CPUID = "";
    private String processor = "";
    private String platform = "";
    private String revision = "";
    private String lithography = "";
    private Date sessionStart;
    private int columnCount;
    private HashMap<Integer, String> columns = new HashMap<>();
    private HashMap<Integer, String[]> strings = new HashMap<>();

    public FileData(String nameFile){
        this.fileName = nameFile;
    }

    public void setCPUID(String CPUID) {
        this.CPUID = CPUID;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public void setLithography(String lithography) {
        this.lithography = lithography;
    }

    public void setSessionStart(Date sessionStart) {
        this.sessionStart = sessionStart;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public void addColumn(Integer num, String str) {
        this.columns.put(num, str);
    }

    public void addString(Integer num, String[] str){
        this.strings.put(num, str);
    }

    public String getCPUID() {
        return CPUID;
    }

    public String getProcessor() {
        return processor;
    }

    public String getPlatform() {
        return platform;
    }

    public String getRevision() {
        return revision;
    }

    public String getLithography() {
        return lithography;
    }

    public Date getSessionStart() {
        return sessionStart;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public HashMap<Integer, String[]> getStrings() {
        return strings;
    }
}
