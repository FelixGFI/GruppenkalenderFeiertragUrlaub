package com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen;

import java.sql.*;

public class DatenbankCommunicator {

    private Connection conn;
    private String url = "jdbc:mariadb://localhost/verpflegungsgeld";
    private String user = "root";
    private String password = "";

    public DatenbankCommunicator() {
    }


    public boolean establishConnection() {
        //create connection for a server installed in localhost, with a user "root" with no password
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            this.conn = conn;
            return true;

        } catch (SQLException e) {
            System.out.println("Database not found. Please make sure the correct Database is available");
            return false;
        }
    }
}
