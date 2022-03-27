package main;

public class MainClass {

    public static void main(String[] args) {
        String fileName = "C:\\1\\CoreTemp\\CT-Log 2020-08-14 22-45-20.csv";
        start mainThred = new start();
        mainThred.setFileName(fileName);
        mainThred.run();
    }

}
