package com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen;

import java.sql.*;

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
}
