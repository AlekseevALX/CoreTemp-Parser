module com.coretempparser {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires java.desktop;
    requires org.apache.commons.csv;
    requires mysql.connector.j;

    opens com.coretempparser to javafx.fxml;
    exports com.coretempparser;
}