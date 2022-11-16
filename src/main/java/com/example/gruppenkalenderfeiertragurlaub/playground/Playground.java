package com.example.gruppenkalenderfeiertragurlaub.playground;
import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.DatenbankCommunicator;
import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.UsefulConstants;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.GruppeFuerKalender;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.GruppenFamilieFuerKalender;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Skin;
import javafx.scene.control.skin.DatePickerSkin;

import java.sql.*;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Era;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.time.chrono.Chronology;
import java.util.List;
import java.util.Map;


public class Playground {
    public static void main(String[] args) throws SQLException {
        //dataBaseReadDeleteInsertUpdateTest();





    }

    private static void dataBaseReadDeleteInsertUpdateTest() throws SQLException {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mariadb://localhost/verpflegungsgeld",
                "root",
                "")) {
            // create a Statement
            try (Statement stmt = conn.createStatement()) {
                //execute query
                try (ResultSet rs = stmt.executeQuery("SELECT * FROM gruppenurlaub")) {
                    while (rs.next()) {
                        String gruppe_id = rs.getString("gruppe_id");
                        String datum = rs.getString("datum");
                        System.out.println("gruppe_id = " + gruppe_id + ", " + "datum=  " + datum);
                    }
                }
                String idString = "2" ;
                String dateString = "2022-10-28";
                String dateString2 = "2021-10-28";
                System.out.println(stmt.execute("DELETE FROM gruppenurlaub WHERE gruppe_id = " + idString + " AND datum = '" + dateString +"' ;"));

                System.out.println(stmt.execute("INSERT INTO gruppenurlaub (gruppe_id, datum) VALUES (" + idString + ", '" + dateString2 + "');"));
                System.out.println(stmt.execute("UPDATE gruppenurlaub SET datum = '" + dateString + "' WHERE datum = '" + dateString2 + "' AND gruppe_id =" + idString + ";"));
                stmt.getUpdateCount();
                stmt.getResultSet();
                ResultSet rs2 = stmt.getResultSet();
            }
        }
    }
}
