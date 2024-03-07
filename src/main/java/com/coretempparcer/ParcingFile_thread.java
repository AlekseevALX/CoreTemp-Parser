package com.coretempparcer;
//start repository

import java.io.*;
import java.sql.*;
import java.util.GregorianCalendar;

public class ParcingFile_thread extends Thread {
    private String fileName = "";

    private long[] lastFile;

    public ParcingFile_thread(String fileName, long[] lastFile, String threadName) {
        super(threadName);
        this.fileName = fileName;
        this.lastFile = lastFile;
    }

    @Override
    public void run() {

        if (fileName.equals("")) {
            endOfthread();
            return;
        }

        GregorianCalendar cal = new GregorianCalendar();
        MainClass.addToLog("Start parcing " + fileName + " Thread " + Thread.currentThread().getName() + " " + cal.getTime());

        FileData fd = null;

        try {
            fd = FileParcer.parceFile(fileName, lastFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fd == null || fd.getStringcount() == 0) {
            endOfthread();
            return;
        }

        synchronized (ParcingFile_thread.class) {
            if (!MainClass.dbChecked) {
                MainClass.checkDB();
            }
        }

        DBWriter dbWriter = new DBWriter(fd);

        synchronized (ParcingFile_thread.class) {
            try {
                dbWriter.writeToBase();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        endOfthread();

    }

    void endOfthread() {
        synchronized (ParcingFile_thread.class) {
            GregorianCalendar cal = new GregorianCalendar();
            MainClass.addToLog("Stop parcing " + fileName + " Thread " + Thread.currentThread().getName() + " " + cal.getTime());
            MainClass.currentWorkingThread -= 1;
            MainClass.countOfThreads -= 1;
            if (MainClass.countOfThreads == 0) {
                MainClass.done = true;
            }
        }
    }
}
