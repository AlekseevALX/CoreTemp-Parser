package main;
//start repository
import java.io.*;
import java.time.Month;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class start {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello! This is parcer for CoreTemp");

//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//
//        String file = new String();
//
//        file = bufferedReader.readLine();
//
//        FileData fd = parceFile(file);
//        writeToBase(fd);


        testBase();
    }

    public static FileData parceFile(String file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

        FileData dataOfFile = new FileData(file);

        String oneString = "";
        String[] spltStrCol;
        boolean readTitle = true;
        boolean readColumns = false;
        boolean readStrings = false;
        String[] spltStr;
        int i = 0;

        while (bufferedReader.ready()){

            oneString = bufferedReader.readLine();

            if (oneString.equals("")) continue;

            if (readStrings){
                spltStr = oneString.split(",");
                dataOfFile.addString(i, spltStr);
                i += 1;
            }

            if (readColumns){

                spltStrCol = oneString.split(",");
                int a = 0;
                for (String s:spltStrCol) {
                    dataOfFile.addColumn(a, s);
                    a += 1;
                }
                dataOfFile.setColumnCount(a);

                readColumns = false;
                readStrings = true;
            }

            if (readTitle){
                spltStr = oneString.split(",");
                if (spltStr[0].equals("CPUID:")){
                    dataOfFile.setCPUID(spltStr[1]);
                }

                if (spltStr[0].equals("Processor:")){
                    dataOfFile.setProcessor(spltStr[1]);
                }

                if (spltStr[0].equals("Platform:")){
                    dataOfFile.setPlatform(spltStr[1]);
                }

                if (spltStr[0].equals("Revision:")){
                    dataOfFile.setRevision(spltStr[1]);
                }

                if (spltStr[0].equals("Lithography:")){
                    dataOfFile.setLithography(spltStr[1]);
                }

                if (spltStr[0].equals("Session start:")){

                    Date sesTart = parseSessionStart(spltStr[1]);
                    dataOfFile.setSessionStart(sesTart);

                    readTitle = false;
                    readColumns = true;
                }
            }


        }
        return dataOfFile;
    }

    public static Date parseSessionStart(String sessionStart){
        String[] spltDate = sessionStart.split(" ");
        String[] spltTime = spltDate[0].split(":");
        Integer hour = Integer.parseInt(spltTime[0]);
        Integer minute = Integer.parseInt(spltTime[1]);
        Integer second = Integer.parseInt(spltTime[2]);
        Integer month = Month.valueOf(spltDate[2].toUpperCase(Locale.ROOT)).getValue();
        Integer date = Integer.parseInt(spltDate[3]);
        Integer year = Integer.parseInt(spltDate[5]);
        GregorianCalendar calendar = new GregorianCalendar(); ///.set(year, month, date, hour, minute, second);
        calendar.set(year, month - 1, date, hour, minute, second);
        Date res = calendar.getTime();
//        int mo = Month.valueOf()
        return res;
    }

    public static void writeToBase(FileData fd) {
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (Exception e){

        }
        finally {

        }
    }

    public static void testBase() {
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/TestDBforJava";
            String login = "postgres";
            String password = "postgres";
            Connection con = DriverManager.getConnection(url, login, password);
            Statement stm = con.createStatement();
            ResultSet res = stm.executeQuery("SELECT * from JavaTest");
            while (res.next()){
                System.out.println(res.getString(1) + " " + res.getString(2) + " " + res.getString(3));
            }

            stm.execute("INSERT INTO JavaTest (FIRST_NAME, LAST_NAME) VALUES ('Homyachok','Homa')");


            res = stm.executeQuery("SELECT * from JavaTest");
            while (res.next()){
                System.out.println(res.getString(1) + " " + res.getString(2) + " " + res.getString(3));
            }

            res.close();
            stm.close();
            con.close();

        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {

        }
    }
}
