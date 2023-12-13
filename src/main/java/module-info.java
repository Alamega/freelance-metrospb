module com.example.graf {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.graf to javafx.fxml;
    exports com.example.graf;
}