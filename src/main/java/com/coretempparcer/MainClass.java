package com.coretempparcer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

public class MainClass {

    public static volatile boolean dbChecked = false;
    public static volatile int currentWorkingThread = 0;


    public static void main(String[] args) {

        new justDoIt(args).start();

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

class justDoIt extends Thread {

    String[] args;
    public static String directory = "";


    public justDoIt(String[] args) {
        this.args = args;
    }

    @Override
    public void run() {

        if (args.length != 0) {
            directory = args[0];
        }

        Calendar cal = new GregorianCalendar();
        System.out.println("Start: " + cal.getTime());
        Date strt = cal.getTime();

        ArrayList<Thread> threads = new ArrayList<>();

        String ext;

        if (directory.equals("")) {
            directory = "C:\\Pet\\CoreTempTestData";
        }

        ext = "csv";

        File file = new File(directory);

        if (!file.exists()) {
            System.out.println("Directory " + directory + " doesn't exist!");
            return;
        }

        File[] listFiles = file.listFiles(new MainClass.FileFilter(ext));

        if (listFiles.length == 0) {
            System.out.println("Finded 0 files in directory " + directory);
            return;
        } else {
            System.out.println("Finded " + listFiles.length + " files.");
        }

        int a = 0;

        for (File f : listFiles) {

            threads.add(new StartScanning(f.getPath(), "thread " + a));
            a += 1;

        }

        for (Thread t : threads) {
            while (MainClass.currentWorkingThread > 50){
                //wait
            }
            t.start();
            MainClass.currentWorkingThread += 1;

        }

        System.out.println("Start: " + strt);
        cal = new GregorianCalendar();
        System.out.println("Finish: " + cal.getTime());
    }
}