module krypto.aes {
    requires javafx.controls;
    requires javafx.fxml;


    opens krypto.aes to javafx.fxml;
    exports krypto.aes;
}