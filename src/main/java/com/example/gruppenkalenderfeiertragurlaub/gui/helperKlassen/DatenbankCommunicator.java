package com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen;

import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class DatenbankCommunicator {

    private static Connection conn;
    private static final String url = "jdbc:mariadb://localhost/verpflegungsgeld";
    private static final String user = "root";
    private static final String password = "";


    /**
     * Establishes a conection to the database with the information specified in the url, user and password global variables
     */
    public static void establishConnection() {
        //create connection for a server installed in localhost, with a user "root" with no password
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            //TODO implement AlertBox for Warning
            System.out.println("Database not found. Please make sure the correct Database is available");
        }
    }

    /**
     * Liest alle werte aus der Tabelle kuechenplanung für das übergebene Jahr und speichert diese in eine ArrayList von KüchenkalenderTag objekten
     * Überprüft außerdem ob Daten für diese jahr in der Datenbank vorhanden sind, wenn nicht generiert es diese für das übergebene jahr und
     * fährt dan mit dem einlesen fort.
     * @param jahr
     * @return Liste aller datenbankeinträge in kuechenplanung für das übergebene jahr als
     * @throws SQLException
     */
    public static ArrayList<KuechenKalenderTag> readKuechenKalenderTage(Integer jahr) throws SQLException {

        generateKuechenDatensaetzeIfMissing(jahr);
        ArrayList<KuechenKalenderTag> kuechenKalenderTagListe = new ArrayList<>();

        kuechenDatenSatzVorhanden(jahr);
        try (Statement stmt = conn.createStatement()) {
            try(ResultSet rs = stmt.executeQuery("SELECT * FROM kuechenplanung WHERE kuechenplanung.datum >= '"
                    + jahr + "-01-01' AND kuechenplanung.datum <= '" + jahr + "-12-31'")) {
                while(rs.next()) {
                    LocalDate datum = LocalDate.parse(rs.getDate("datum").toString());
                    Boolean kuecheOffen = rs.getBoolean("geoeffnet");
                    KuechenKalenderTag kuechenTag = new KuechenKalenderTag(datum, kuecheOffen);
                    kuechenKalenderTagListe.add(kuechenTag);
                }
            }
        }
        return kuechenKalenderTagListe;
    }

    /**
     * überprüft Mittels Methodenaufruf ob für en bestimmtes jahr der erste Werktag des jahres in der Datenbanktabelle kuechenplanung vorhandne ist.
     * wenn dies der Fall ist geht die Methode davon aus das alle nötigen einträge dieses jahres bereits in der Datenbank vorhanden sind.
     * Wenn nicht generiert die Methode eine ArrayList<LocalDate> von allen Werktagen des Jahres und schreibt zu jedem Datum einen eintrag
     * in die Tabelle gruppenkalender.
     * @param jahr
     * @throws SQLException
     */
    private static void generateKuechenDatensaetzeIfMissing(Integer jahr) throws SQLException {
        if(kuechenDatenSatzVorhanden(jahr)) {
             return;
        }
        try(Statement stmt = conn.createStatement()) {
            ArrayList<LocalDate> generatedWerktagsdatumListe = getListOfLocalDatesForAllWerktageOfGivenYear(jahr);
            for (LocalDate dat : generatedWerktagsdatumListe) {
                stmt.execute("INSERT INTO kuechenplanung (datum, geoeffnet)" +
                        " VALUES ('" + dat.toString() + "', true);");
            }
        }
    }

    /**
     * ermittelt den ersten werktag eines Jahres. Überprüft ob ein eintrg für diesen, für dieses Jahr in der Datenbanktabelle kuechenplanung vorhanden ist.
     * @param jahr
     * @return true wenn datensatz für ersten werktag für gegebenes jahr vorhanden ist, false wenn nicht
     * @throws SQLException
     */
    private static Boolean kuechenDatenSatzVorhanden(Integer jahr) throws SQLException {
        LocalDate datum = LocalDate.parse(jahr + "-01-01");
        while(!datumIstWerktag(datum)){
            datum = datum.plusDays(1);
        }
        try (Statement stmt = conn.createStatement()) {
            try(ResultSet rs = stmt.executeQuery("SELECT EXISTS (SELECT * FROM kuechenplanung k WHERE k.datum = '" + datum + "') as kuechenEintragVorhanden;")) {
                rs.next();
                return rs.getBoolean("kuechenEintragVorhanden");

            }
        }

    }

    /**
     * Überprüft ob in der kuechenplanung tabelle einträge für das übergebene jahr vorhanden sind. Wenn nicht werden diese genneriert
     * Liest für das übergebene Jahr alle einträge in der Spalte datum der kuechenplanung tabelle. Für Jedes Gelesene Datum sucht es in der Tabelle
     * betriebsurlaub nach einem identischen datum. Wenn ein datum gefunden wird ist klar das es sich bie dem gelesenen Datum um einen bereits festgelgten
     * Betriebsurlaubstag handelt, (ein objekt Betriebsurlaubstag wird erstellt mit dem Booleanwert istBetriebsurlaubstag auf true)
     * wird kein entsprechendes Datum gefunden so ist der Tag kein betriebsurlaubstag (Betriebsulraubstag wird mit Boolean wert auf False erstellt)
     * die Estellten Betriebsurlaubstagsobjekte werden in einer Arraylist zurückgegeben.
     * @param jahr
     * @return Liste aller datenbankeinträge datum aus kuechenplanung und betriebsurlaub für das übergebene jahr
     * @throws SQLException
     */
    public static ArrayList<BetriebsurlaubsTag> readBetriebsurlaubTage(Integer jahr) throws SQLException {
        generateKuechenDatensaetzeIfMissing(jahr);
        ArrayList<BetriebsurlaubsTag> betriebsurlaubsTagListe = new ArrayList<>();

        try (Statement stmt = conn.createStatement()) {
            try(ResultSet rs = stmt.executeQuery(
                    "select k.datum as 'datum', b.datum as 'bdatum', f.datum as 'fdatum' " +
                    "from kuechenplanung k left join betriebsurlaub b on k.datum = b.datum " +
                    "                      left join feiertag f on k.datum = f.datum " +
                    "WHERE k.datum >= '" + jahr + "-01-01' AND k.datum <= '" + jahr + "-12-31'")) {
                while(rs.next()) {
                    LocalDate datum = LocalDate.parse(rs.getDate("datum").toString());
                    Boolean isBetriebsurlaub = (rs.getDate("bdatum") != null);
                    Boolean isFeiertag = (rs.getDate("fdatum") != null);
                    if(isBetriebsurlaub) {System.out.println("isBetriebsurlaub");}
                    if(isFeiertag) {System.out.println("isFeiertag");}
                    BetriebsurlaubsTag betriebsurlaub = new BetriebsurlaubsTag(datum, isBetriebsurlaub, isFeiertag);
                    betriebsurlaubsTagListe.add(betriebsurlaub);

                }
            }
        }
        return betriebsurlaubsTagListe;
    }

    /**
     * Liest alle Einträge für das gegebene Jahr und die gegebene Gruppe bzw Gruppenfamilie
     * aus der Datenbanktabelle Gruppenkalender und speichert diese in einer ArrayListe von
     * GruppenKalenderTag objekten. Liest außerdme aus, ob ein bestimmter Tag ein Betriebsurlabs- oder Feiertag ist.
     * Setzt die Diesbezüglichen Boolean Attribute des GruppenkalenderTages Entsprechend
     * @param jahr
     * @return Liste aller datenbankeinträge in gruppenkalender für das übergebene jahr
     * @throws SQLException
     */
    public static ArrayList<GruppenKalenderTag> readGruppenKalenderTage(Integer jahr, Object gruppeOderFamilie) throws SQLException {
        StringBuilder gruppeOderFamilieSelectedBedinung = new StringBuilder(" AND ");
        if(gruppeOderFamilie.getClass() == GruppeFuerKalender.class) {
            gruppeOderFamilieSelectedBedinung.append("g.gruppe_id = ").append(((GruppeFuerKalender) gruppeOderFamilie).getGruppeId());
            generateTageIfMissing((GruppeFuerKalender) gruppeOderFamilie, jahr);
        } else {
            boolean isFirstGruppInArray = true;
            gruppeOderFamilieSelectedBedinung.append("(");
            for (GruppeFuerKalender gr : ((GruppenFamilieFuerKalender)gruppeOderFamilie).getGruppenDerFamilie() ) {
                if(!isFirstGruppInArray) {
                    gruppeOderFamilieSelectedBedinung.append(" OR ");
                }
                gruppeOderFamilieSelectedBedinung.append("g.gruppe_id = ").append(gr.getGruppeId());
                generateTageIfMissing(gr, jahr);
                isFirstGruppInArray = false;
            }
            gruppeOderFamilieSelectedBedinung.append(")");
        }

        ArrayList<GruppenKalenderTag> kalenderTagListe = new ArrayList<>();
        try (Statement stmt = conn.createStatement()) {
            try(ResultSet rs = stmt.executeQuery("select g.*, b.datum as bdatum, f.datum as fdatum from gruppenkalender g\n" +
                    "left join betriebsurlaub b on g.datum = b.datum \n" +
                    "left join feiertag f on g.datum = f.datum\n" +
                    "where g.datum >= '" + jahr + "-01-01' and g.datum <= '" + jahr + "-12-31'" + gruppeOderFamilieSelectedBedinung)) {
                while(rs.next()) {
                    LocalDate datum = LocalDate.parse(rs.getDate("datum").toString());
                    Boolean kuecheOffen = rs.getBoolean("essensangebot");
                    Integer gruppen_id =  rs.getInt("gruppe_id");
                    Character gruppenstatus = rs.getString("gruppenstatus").toCharArray()[0];
                    Boolean isBetriebsurlaub = (rs.getDate("bdatum") != null);
                    Boolean isFeiertag = (rs.getDate("fdatum") != null);
                    kalenderTagListe.add(new GruppenKalenderTag(gruppen_id, datum, gruppenstatus, kuecheOffen, isBetriebsurlaub, isFeiertag));
                }
            }
        }

        return kalenderTagListe;
    }

    /**
     * Fragt aus der Datenabnk alle Gruppenfamilien und Dazu Gehörigen Gruppen ab, Erstellt aus ihnen GruppenFamilieFuerKalender und GruppeFuerKalender
     * Objekte, schreibt die Gruppen in die Gruppenliste ihrer jeweiligen Familie und die Familien in die Arraylist gruppenFamilieListe
     * @return eine ArrayListe aller in der Datenbank Vorhandenen Gruppenfamilien (welche die Zugehörigen Gruppen enthalten)
     * @throws SQLException
     */
    public static ArrayList<GruppenFamilieFuerKalender> getAllGruppenFamilienUndGruppen() throws SQLException {
        ArrayList<GruppenFamilieFuerKalender> gruppenFamilieListe = new ArrayList<>();


        try(Statement stmt = conn.createStatement()) {
            try(ResultSet rs = stmt.executeQuery("select f.id as 'familienId', f.name as 'familienName', g.id 'gruppeId', g.name as 'gruppeName' \n" +
                    "from gruppenfamilie f inner join gruppe g on f.id = g.gruppenfamilie_id;")){
                while (rs.next()){
                    Integer familienId = rs.getInt("familienId");
                    String familienName = rs.getString("familienName");
                    Integer gruppeId = rs.getInt("gruppeId");
                    String gruppeName = rs.getString("gruppeName");

                    GruppeFuerKalender neueGruppe = new GruppeFuerKalender(gruppeId, gruppeName, familienId);

                    boolean gruppeExists = false;
                    for (GruppenFamilieFuerKalender familie : gruppenFamilieListe) {
                        if(Objects.equals(familie.getFamilieId(), familienId)) {
                            gruppeExists = true;
                            familie.getGruppenDerFamilie().add(neueGruppe);
                            break;
                        }
                    }
                    if(!gruppeExists) {
                        GruppenFamilieFuerKalender neueFamilie = new GruppenFamilieFuerKalender(familienId, familienName, new ArrayList<>());
                        neueFamilie.getGruppenDerFamilie().add(neueGruppe);
                        gruppenFamilieListe.add(neueFamilie);
                    }
                }
            }
        }

        return gruppenFamilieListe;
    }

    /**
     * überprüft mittels Methodenaufruf ob für ein bestimmtes jahr und eine bestimmte gruppe der Erste Werktag des Jahres in der Datenbanktabelle
     * gruppenkalender vorhanden ist. Wenn dies der Fall ist geht die Methode davon aus das alle nötigen eitnräge dieses jahres bereits in der
     * Datenbank sind, wenn nicht generiert die Methode ein ArrayList<LocalDate> von allen Werktagen des jahres und erzeugt mit der Nummer der
     * übergebenen Gruppe alle entsprechenden Einträge in der Datenbank (Tabelle: gruppenkalender)
     * @param gr
     * @param jahr
     * @throws SQLException
     */

    static void generateTageIfMissing(GruppeFuerKalender gr, Integer jahr) throws SQLException {
        if(!tagDatenSatzVorhanden(gr, jahr)) {
            try(Statement stmt = conn.createStatement()) {
                ArrayList<LocalDate> generierteTageListe = getListOfLocalDatesForAllWerktageOfGivenYear(jahr);

                for (LocalDate dat : generierteTageListe) {
                    stmt.execute("INSERT INTO gruppenkalender (gruppe_id, datum, essensangebot, gruppenstatus)" +
                            " VALUES (" + gr.getGruppeId() + ", '" + dat.toString() + "', true, '" + UsefulConstants.getDefaultStatus() +"');");
                }
            }
        }
    }

    /**
     * ermittelt für das gegebene Jahr alle Werktage als LocalDate und speichert diese in einer ArrayList<LocalDate> die zurückgegeben wird.
     * @param jahr
     * @return ArrayList<LocalDate> welches alle Werktage des gegebenen Jahres als LocalDate enthält
     */
    private static ArrayList<LocalDate> getListOfLocalDatesForAllWerktageOfGivenYear(Integer jahr) {
        ArrayList<LocalDate> generierteTageListe = new ArrayList<>();
        LocalDate upcountDatum = LocalDate.parse(jahr + "-01-01");
        while(upcountDatum.getYear() == jahr) {
            if(datumIstWerktag(upcountDatum)) {
                generierteTageListe.add(upcountDatum);
            }
            upcountDatum = upcountDatum.plusDays(1);
        }
        return generierteTageListe;
    }

    /**
     * Überprüft ob der Erste Werktag des Gegebnen jahres für die gegebne Gruppe als Eintrag in der Datenbank Existiert.
     * (Das Programm soll nur ganze jahre für eine gruppe gleichzeitig generieren, und nur werktage. Deswegen reicht es aus
     * nach dem Ersten Werktag des Jahres zu suchen um festzustellen ob daten für das gegebene Jahr in der Datenbank vorhanden sind
     * @param gr
     * @param jahr
     * @return true wenn der Tag in der Datenbank vorhanden ist, False wenn er nicht vorhanden ist
     * @throws SQLException
     */
    private static Boolean tagDatenSatzVorhanden(GruppeFuerKalender gr, Integer jahr) throws SQLException {
        LocalDate datum = LocalDate.parse(jahr + "-01-01");
        datum = getNextWerktag(datum);
        try (Statement stmt = conn.createStatement()) {
            try(ResultSet rs = stmt.executeQuery("SELECT EXISTS (SELECT * FROM gruppenkalender g WHERE g.datum = '" + datum.toString() + "' AND g.gruppe_id = " + gr.getGruppeId() +") as dayExists;")) {
                rs.next();
                return rs.getBoolean("dayExists");
            }
        }

    }

    /**
     * ermittelt ob es sich beim Übergebenen LocalDate datum um einen Werktag (tag von Montag bis Freitag) handelt. (Feiertage werden nicht
     * berücksichtigt, nur Wochenende)
     * @param datum
     * @return true wenn es sich beim Übergebenen LocalDate um einen Werktag handelt (ausgenommen feiertag) False wenn nicht
     */
    public static Boolean datumIstWerktag(LocalDate datum) {
        return UsefulConstants.getTageListInLocalDateFormat().contains(datum.getDayOfWeek().toString());
    }

    /**
     * Erhält eine ArrayList<GruppenFamilieFuerKalender. Jedes GruppenFamilieFuerKalender Object enthält eine ArrayList
     * welche alle zu diser Gruppenfamilie gehörenden Gruppen enthält. Liest all diese Arraylisten aus den Gruppenfamilien
     * aus und fügt alle gruppen aller Familien zu einer ArrayList<GruppeFuerKalender> hinzu.
     * @param gruppenFamilienListe
     * @return ArrayList<GruppeFuerKalender> welche alle gruppen aller Familien in der übergebnen ArrayList enthält.
     */
    public static ArrayList<GruppeFuerKalender> getAlleGruppenAusFamilien(ArrayList<GruppenFamilieFuerKalender> gruppenFamilienListe) {
        ArrayList<GruppeFuerKalender> gruppenListe = new ArrayList<>();
        for (GruppenFamilieFuerKalender grFa : gruppenFamilienListe) {
            gruppenListe.addAll(grFa.getGruppenDerFamilie());
        }
        return gruppenListe;
    }

    /**
     * findet heraus welches, vom gegebenen Datum heraus gerechnet der nächste Wekrtag ist (berücksichtigt nicht Feiertage)
     * Wenn das übergebne Datum ein Werktag ist, gibt die Methode besagtes Datum unverändert zurück, ansonsten den nächsten Gefundenen
     * Werktag.
     * @param datum
     * @return den Nächsten Werktag als LocalDate
     */
    public static LocalDate getNextWerktag(LocalDate datum) {
        while(!datumIstWerktag(datum)) {
            datum = datum.plusDays(1);
        }
        return datum;
    }
}
