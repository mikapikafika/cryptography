module com.example.krypto_aes {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.krypto_aes to javafx.fxml;
    exports com.example.krypto_aes;
}