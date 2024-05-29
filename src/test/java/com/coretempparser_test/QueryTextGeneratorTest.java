package com.coretempparser_test;

import com.coretempparser.MainClass;
import com.coretempparser.QueryTextGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;

class QueryTextGeneratorTest {
    private String tableName;

    @BeforeEach
    void setUp() {

        try (MockedStatic<MainClass> main = Mockito.mockStatic(MainClass.class)) {
            main.when(MainClass::getTableName).thenReturn("coretemp");
        }

    }

    @Test
    void getQueryText_CreateIfNotExist() {

        try (MockedStatic<MainClass> main = Mockito.mockStatic(MainClass.class)) {
            main.when(MainClass::getTableName).thenReturn("coretemp");
        }

        String tableName = MainClass.getTableName();

        String[] columns = new String[6];
        columns[0] = "time";
        columns[1] = "compname";
        columns[2] = "core0temp";
        columns[3] = "core0load";
        columns[4] = "core0speedmhz";
        columns[5] = "cpu0power";

        String colCompName = "compname";

        String expected = "CREATE TABLE IF NOT EXISTS " + tableName + "(time timestamp, compname varchar(20), " +
                "core0temp varchar(10), core0load varchar(10), core0speedmhz varchar(10), cpu0power varchar(10), " +
                "PRIMARY KEY (time, compname));";

        String actual = QueryTextGenerator.getQueryText_CreateIfNotExist(columns, colCompName);

        Assertions.assertEquals(expected, actual);

    }

    @Test
    void getQueryText_IncreaseTable() {
        ArrayList<String> newCol = new ArrayList<>();
        newCol.add("core1load");
        newCol.add("core1speedmhz");

        String expected = "ALTER TABLE " + tableName + " ADD core1load varchar(10), ADD core1speedmhz varchar(10)";

        String actual = QueryTextGenerator.getQueryText_IncreaseTable(newCol);

        Assertions.assertEquals(expected, actual);
    }
}
