package com.coretempparcer;

import java.sql.*;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class DBReader {
    java.sql.Timestamp tsStart;
    java.sql.Timestamp tsFinish;

    Date dateFrom;

    Date dateTo;

    ResultSet resSel;

    public DBReader() {
    }

    private String tableName = "CoreTemp";

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public HashMap<String, Float> prepareChartData() {
        HashMap<String, Float> chartData = new HashMap<>();

        try {
            readFromDB();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return chartData;
    }

    public void readFromDB() throws SQLException {

        PreparedStatement stm;

        String queryText = getQueryTextRead();

        if (MainClass.connectionToBase()) {

            stm = MainClass.con.prepareStatement(queryText);

            setupParametersToSelect(stm, tsStart, tsFinish);

            resSel = stm.executeQuery();

        }

    }

    private String getQueryTextRead() {
        String text = "";

        text = text.concat("SELECT ");
        text = text.concat("*");
        text = text.concat(" FROM " + tableName);
        text = text.concat(" WHERE ");
        text = text.concat("time" + " >= ?");
        text = text.concat(" and ");
        text = text.concat("time" + " <= ?");

        return text;
    }

    public void setupTimeStamps(Date dateFrom, Date dateTo) {

        tsStart = new java.sql.Timestamp(dateFrom.getTime());
        tsStart.setNanos(0);

        tsFinish = new java.sql.Timestamp(dateTo.getTime());
        tsFinish.setNanos(0);

    }
    private void setupParametersToSelect(PreparedStatement stm, java.sql.Timestamp tsStart, java.sql.Timestamp tsFinish) {
        try {
            stm.setTimestamp(1, tsStart);
            stm.setTimestamp(2, tsFinish);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
