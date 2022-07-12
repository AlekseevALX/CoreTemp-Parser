module com.coretempparcer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;


    opens com.coretempparcer to javafx.fxml;
    exports com.coretempparcer;
}