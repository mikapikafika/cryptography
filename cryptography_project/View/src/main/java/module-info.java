module com.example.view {
    requires javafx.controls;
    requires javafx.fxml;
    requires Model;


    opens com.example.view to javafx.fxml;
    exports com.example.view;
}