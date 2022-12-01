module com.example.gruppenkalenderfeiertragurlaub {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires layout;
    requires kernel;


    opens com.example.gruppenkalenderfeiertragurlaub to javafx.fxml;
    exports com.example.gruppenkalenderfeiertragurlaub;
    exports com.example.gruppenkalenderfeiertragurlaub.speicherklassen;
    opens com.example.gruppenkalenderfeiertragurlaub.speicherklassen to javafx.fxml;
    exports com.example.gruppenkalenderfeiertragurlaub.gui;
    opens com.example.gruppenkalenderfeiertragurlaub.gui to javafx.fxml;
    exports com.example.gruppenkalenderfeiertragurlaub.playground;
    opens com.example.gruppenkalenderfeiertragurlaub.playground to javafx.fxml;
}