module com.example.gruppenkalenderfeiertragurlaub {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.gruppenkalenderfeiertragurlaub to javafx.fxml;
    exports com.example.gruppenkalenderfeiertragurlaub;
}