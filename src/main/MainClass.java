package main;

import java.io.File;
import java.io.FileFilter;

public class MainClass {

    public static boolean dbChecked = false;

    public static void main(String[] args) {

        String[] files = new String[5];
        String fileName;
        String directory;

        directory = "C:\\1\\CoreTemp\\";
        

        files[0] = "C:\\1\\CoreTemp\\CT-Log 2020-08-12 23-39-05.csv";
        files[1] = "C:\\1\\CoreTemp\\CT-Log 2020-08-13 09-25-29.csv";
        files[2] = "C:\\1\\CoreTemp\\CT-Log 2020-08-13 23-33-18.csv";
        files[3] = "C:\\1\\CoreTemp\\CT-Log 2022-03-06 16-07-01.csv";
        files[4] = "C:\\1\\CoreTemp\\CT-Log 2022-03-25 08-49-09.csv";

        for (int i = 0; i < 5; i++) {
            fileName = files[i];
            start mainThread = new start();
            mainThread.setFileName(fileName);
            mainThread.run();
        }

    }

}
