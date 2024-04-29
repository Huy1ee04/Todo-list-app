module com.example.sampletodolist {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.sampletodolist to javafx.fxml;
    exports com.example.sampletodolist;
}