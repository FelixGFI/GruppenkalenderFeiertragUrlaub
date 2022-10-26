module com.example.gruppenkalenderfeiertragurlaub {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.gruppenkalenderfeiertragurlaub to javafx.fxml;
    exports com.example.gruppenkalenderfeiertragurlaub;
}