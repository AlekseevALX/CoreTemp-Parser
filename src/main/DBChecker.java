package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class DBChecker {
    private static HashMap<Integer, String> columns;

    public DBChecker(HashMap<Integer, String> columns) {
        this.columns = columns;
    }

    private static String getExecuteText() {
        String text = "";
        String colName = "";

        text = text.concat("CREATE TABLE IF NOT EXISTS " + "CoreTemp(");
        text = text.concat(columns.get(0) + " " + "timestamp UNIQUE, ");

        int ch = columns.size();

        for (int i = 1; i < ch; i++) {
            colName = columns.get(i);
            if (colName.equals("")) continue;

            text = text.concat(colName + " varchar(10), ");
        }

        text = text.substring(0, text.length() - 2) + ");";

        return text;
    }

    public void checkDB() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        String url = "jdbc:postgresql://localhost:5432/TestDBforJava";
        String login = "postgres";
        String password = "postgres";

        Connection con = null;

        try {
            con = DriverManager.getConnection(url, login, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        Statement stm;

        try {
            stm = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        String queryText = getExecuteText();

        try {
            stm.execute(queryText);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
