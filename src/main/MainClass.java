package main;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

public class MainClass {

    public static boolean dbChecked = false;

    public static void main(String[] args) {


        Calendar cal = new GregorianCalendar();
        System.out.println("Start: " + cal.getTime());
        Date strt = cal.getTime();

        ArrayList<Start> threads = new ArrayList<>();

        String fileName;
        String directory;
        String ext;

//        directory = "C:\\1\\CoreTemp";
        directory = "E:\\Core Temp";
        ext = "csv";

        File file = new File(directory);

        if (!file.exists()) {
            System.out.println("Directory " + directory + " doesn't exist!");
            return;
        }

        File[] listFiles = file.listFiles(new FileFilter(ext));

        if (listFiles.length == 0) {
            System.out.println("Finded 0 files in directory " + directory);
            return;
        } else {
            System.out.println("Finded " + listFiles.length + " files.");
        }

        for (File f : listFiles) {

            threads.add(new Start(f.getPath()));

//            new Start(f.getPath());

        }

        for (Start t : threads) {
            t.run();
        }

        System.out.println("Start: " + strt);
        cal = new GregorianCalendar();
        System.out.println("Finish: " + cal.getTime());

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
