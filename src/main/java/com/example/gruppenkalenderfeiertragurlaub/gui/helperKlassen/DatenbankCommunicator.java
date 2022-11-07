package com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen;

import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.BetriebsurlaubsTag;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.GruppenKalenderTag;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.KuechenKalenderTag;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class DatenbankCommunicator {

    private static Connection conn;
    private static String url = "jdbc:mariadb://localhost/verpflegungsgeld";
    private static String user = "root";
    private static String password = "";


    /**
     * Establishes a conection to the database with the information specified in the url, user and password global variables
     * @return if conection was succesfully established, returns true, otherwise false
     */
    public static boolean establishConnection() {
        //create connection for a server installed in localhost, with a user "root" with no password
        try {
            conn = DriverManager.getConnection(url, user, password);

            return true;

        } catch (SQLException e) {
            System.out.println("Database not found. Please make sure the correct Database is available");
            return false;
        }
    }
    public static ArrayList<KuechenKalenderTag> readKuechenKalenderTage() throws SQLException {
        ArrayList<KuechenKalenderTag> kuechenKalenderTagListe = new ArrayList<>();

        try (Statement stmt = conn.createStatement()) {
            try(ResultSet rs = stmt.executeQuery("SELECT * FROM kuechenplanung")) {
                while(rs.next()) {
                    LocalDate datum = LocalDate.parse(rs.getDate("datum").toString());
                    Boolean kuecheOffen = rs.getBoolean("gooeffnet");
                    KuechenKalenderTag kuechenTag = new KuechenKalenderTag(datum, kuecheOffen);
                    kuechenKalenderTagListe.add(kuechenTag);
                }
            }
        }
        return kuechenKalenderTagListe;
    }

    public static ArrayList<BetriebsurlaubsTag> readBetriebsurlaubTage() throws SQLException {
        ArrayList<BetriebsurlaubsTag> betriebsurlaubsTagListe = new ArrayList<>();

        try (Statement stmt = conn.createStatement()) {
            try(ResultSet rs = stmt.executeQuery("select\n" +
                    "\tk.datum as 'normalDatum',\n" +
                    "\tb.datum as 'betiebsurlaubsDatum'\n" +
                    "FROM \n" +
                    "\tkuechenplanung k left join betriebsurlaub b on k.datum = b.datum;")) {
                while(rs.next()) {
                    LocalDate datum = LocalDate.parse(rs.getDate("normalDatum").toString());
                    boolean isBetiebsurlaub = false;
                    if(rs.getDate("betiebsurlaubsDatum") != null) {
                        isBetiebsurlaub = true;
                    }

                    BetriebsurlaubsTag betriebsurlaub = new BetriebsurlaubsTag(datum, isBetiebsurlaub);
                    System.out.println(betriebsurlaub);
                    betriebsurlaubsTagListe.add(betriebsurlaub);

                }
            }
        }
        return betriebsurlaubsTagListe;
    }

    public static ArrayList<GruppenKalenderTag> readGruppenKalenderTage() throws SQLException {

        ArrayList<GruppenKalenderTag> kalenderTagListe = new ArrayList<>();
        try (Statement stmt = conn.createStatement()) {
            try(ResultSet rs = stmt.executeQuery("SELECT * FROM gruppenkalender")) {
                while(rs.next()) {
                    LocalDate datum = LocalDate.parse(rs.getDate("datum").toString());
                    Boolean kuecheOffen = rs.getBoolean("essensangebot");
                    Integer gruppen_id =  rs.getInt("gruppe_id");
                    Character gruppenstatus = rs.getString("gruppenstatus").toCharArray()[0];
                    System.out.println(datum);
                    System.out.println(gruppen_id);
                    System.out.println(kuecheOffen);
                    System.out.println(gruppenstatus);
                    kalenderTagListe.add(new GruppenKalenderTag(gruppen_id, datum, gruppenstatus, kuecheOffen));
                }
            }
        }

        return kalenderTagListe;
    }
}
