package com.example.gruppenkalenderfeiertragurlaub;
import java.sql.*;
public class Playground {
    public static void main(String[] args) throws SQLException {
        // launch();
        //create connection for a server installed in localhost, with a user "root" with no password
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
