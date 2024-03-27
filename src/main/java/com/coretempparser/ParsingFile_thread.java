package com.coretempparser;
//start repository

import java.io.*;
import java.util.GregorianCalendar;

public class ParsingFile_thread extends Thread {
    private final String fileName;

    private final long[] lastFile;

    public ParsingFile_thread(String fileName, long[] lastFile, String threadName) {
        super(threadName);
        this.fileName = fileName;
        this.lastFile = lastFile;
    }

    @Override
    public void run() {

        if (fileName.equals("")) {
            endOfThread();
            return;
        }

        GregorianCalendar cal = new GregorianCalendar();
        MainClass.addToLog("Start parsing " + fileName + " Thread " + Thread.currentThread().getName() + " " + cal.getTime());

        FileData fd = null;

        try {
            fd = FileParser.parseFile(fileName, lastFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fd != null){
            MainClass.addToLog("Readed " + fd.getStringCount() + " records in file " + fileName);
        }

        if (fd == null || fd.getStringCount() == 0) {
            endOfThread();
            return;
        }

        synchronized (ParsingFile_thread.class) {
            if (!MainClass.dbChecked) {
                MainClass.checkDB();
            }
        }

        DBWriter dbWriter = new DBWriter(fd);

        synchronized (ParsingFile_thread.class) {
            try {
                dbWriter.writeToBase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        endOfThread();

    }

    void endOfThread() {
        synchronized (ParsingFile_thread.class) {
            GregorianCalendar cal = new GregorianCalendar();
            MainClass.addToLog("Stop parsing " + fileName + " Thread " + Thread.currentThread().getName() + " " + cal.getTime());
            MainClass.decreaseCurrentWorkingThread();
            MainClass.decreaseCountOfThreads();
            if (MainClass.getCountOfThreads() == 0) {
                MainClass.done = true;
            }
        }
    }
}
