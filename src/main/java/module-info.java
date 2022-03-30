module com.coretempparcer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.coretempparcer to javafx.fxml;
    exports com.coretempparcer;
}