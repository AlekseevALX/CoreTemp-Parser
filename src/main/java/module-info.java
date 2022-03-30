module com.coretempparcer {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.coretempparcer to javafx.fxml;
    exports com.coretempparcer;
}