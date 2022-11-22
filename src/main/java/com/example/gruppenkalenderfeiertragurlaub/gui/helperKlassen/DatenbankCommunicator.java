package com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen;

import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.*;
import javafx.collections.ObservableList;

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
     * Liest alle werte aus der Tabelle kuechenplanung für das übergebene Jahr und speichert diese in eine ArrayList
     * von KüchenkalenderTag objekten. Liest außerdem aus ob es sich bei einem Tag um einen Feiertag handelt.
     * Überprüft ob Daten für diese jahr in der Datenbank vorhanden sind, wenn nicht generiert es diese für das übergebene jahr und
     * fährt dan mit dem einlesen fort. Übergibt die Eingelesenen Daten als ArrayList von KuechenKalenderTagen.
     * @param jahr
     * @return Liste aller datenbankeinträge in kuechenplanung für das übergebene jahr als
     * @throws SQLException wird bei Fehlern mit dem datenbankzugriff Mittels SQL Statment ausgelöst
     */
    public static ArrayList<KuechenKalenderTag> readKuechenKalenderTage(Integer jahr) throws SQLException {

        generateKuechenDatensaetzeIfMissing(jahr);
        ArrayList<KuechenKalenderTag> kuechenKalenderTagListe = new ArrayList<>();

        kuechenDatenSatzVorhanden(jahr);
        try (Statement stmt = conn.createStatement()) {
            try(ResultSet rs = stmt.executeQuery("select k.*, f.datum as 'fdatum' " +
                    "from kuechenplanung k left join feiertag f on k.datum = f.datum " +
                    "WHERE k.datum >= '" + jahr + "-01-01' AND k.datum <= '" + jahr + "-12-31'")) {
                while(rs.next()) {
                    LocalDate datum = LocalDate.parse(rs.getDate("datum").toString());
                    Boolean kuecheOffen = rs.getBoolean("geoeffnet");
                    Boolean isFeiertag = (rs.getDate("fdatum") != null);
                    Integer kuecheOffenAsInteger = 0;
                    if(kuecheOffen) {kuecheOffenAsInteger = 1;}
                    if(isFeiertag) {kuecheOffenAsInteger = 2;}
                    KuechenKalenderTag kuechenTag = new KuechenKalenderTag(datum, kuecheOffenAsInteger);
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
     * @param jahr das Auszulesende Jahr als integer
     * @throws SQLException wird bei Fehlern mit dem datenbankzugriff Mittels SQL Statment ausgelöst
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
     * betriebsurlaub nach einem identischen datum sowie in der Tabelle Feiertag nach einem Feiertag mit entsprechendem Datum.
     * Wenn ein datum gefunden wird ist klar das es sich bie dem gelesenen Datum um einen bereits festgelgten Betriebsurlaubstag
     * bzw. einen Feirtag handelt. wird kein entsprechendes Datum gefunden so ist der Tag kein betriebsurlaubstag unda auch kein
     * Feiertag. Für Jeden Ausgelesenen Tag wird ein Object der Klasse BetriebsurlaubsTag erzeugt. Ob es sich um einen
     * Betriebsurlaub(1) einen Feiertag(2) oder um keins von beidem(0) handelt wird mittels des Integer attributs betriebsurlaub
     * gespeichert.
     *
     * die Estellten Betriebsurlaubstagsobjekte werden in einer Arraylist zurückgegeben.
     * @param jahr
     * @return Liste aller datenbankeinträge datum aus kuechenplanung und betriebsurlaub für das übergebene jahr
     * @throws SQLException wird bei Fehlern mit dem datenbankzugriff Mittels SQL Statment ausgelöst
     */
    public static ArrayList<BetriebsurlaubsTag> readBetriebsurlaubTage(Integer jahr) throws SQLException {
        generateKuechenDatensaetzeIfMissing(jahr);
        ArrayList<BetriebsurlaubsTag> betriebsurlaubsTagListe = new ArrayList<>();
        //TODO evtl. Don't display tag at all if it is a Feiertag
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

                    Integer isBetriebsurlaubAsInteger = 0;
                    if(isBetriebsurlaub) {isBetriebsurlaubAsInteger = 1;}
                    if(isFeiertag) {isBetriebsurlaubAsInteger = 2;}

                    BetriebsurlaubsTag betriebsurlaub = new BetriebsurlaubsTag(datum, isBetriebsurlaubAsInteger);
                    betriebsurlaubsTagListe.add(betriebsurlaub);
                }
            }
        }
        return betriebsurlaubsTagListe;
    }
    /**
     * Liest alle Einträge für das gegebene Jahr und die gegebene Gruppe bzw Gruppenfamilie
     * aus der Datenbanktabelle Gruppenkalender und speichert diese in einer ArrayListe von
     * GruppenKalenderTag objekten. Liest außerdme aus, ob ein bestimmter Tag ein Betriebsurlabs- oder Feiertag ist
     * und speichert diese Daten für jeden eintrag in einem Objekt der Klasse GruppenKalenderTag.
     * Wenn keine daten für eine Gruppe und das entsprechende Jahr vorhanden sind werdn diese Generiert
     * @param gruppeOderFamilie enthält entweder ein Object GruppeFuerkalender oder GruppenFamilieFuerKalender.
     *                          Letzeres Enthält eine Arraylist mit allen Gruppen diese Gruppenfamilie.
     * @param jahr das gegebene jahr als Integer
     * @return Liste aller datenbankeinträge in gruppenkalender für das übergebene jahr
     * @throws SQLException wird bei Fehlern mit dem datenbankzugriff Mittels SQL Statment ausgelöst
     */
    public static ArrayList<GruppenKalenderTag> readGruppenKalenderTage(Integer jahr, Object gruppeOderFamilie) throws SQLException {
        if(gruppeOderFamilie.getClass() == GruppeFuerKalender.class) {
            generateTageIfMissing((GruppeFuerKalender) gruppeOderFamilie, jahr);
        } else {
            for (GruppeFuerKalender gr : ((GruppenFamilieFuerKalender) gruppeOderFamilie).getGruppenDerFamilie() ) {
                generateTageIfMissing(gr, jahr);
            }
        }

        StringBuilder gruppeOderFamilieSelectedBedingung = getSQLExpressionChoosingGroups(gruppeOderFamilie);

        ArrayList<GruppenKalenderTag> kalenderTagListe = readGruppenKalenderTageDatenbankzugriff(jahr, gruppeOderFamilieSelectedBedingung);

        return kalenderTagListe;
    }

    /**
     * Der zweck dieser methode ist anhand eines Übergebenen Gruppe oder Gruppenfamilie einen StringBuilder zu erzeugen welcher
     * SQL Anweisungen enhält welche Spezifizieren für Welche Gruppen Daten Abgerufen werden sollen. Wird eine Gruppe übergeben
     * so wird diese in der SQL anweisung berücksichtigt, wird eine Gruppenfamilie angegeben so werden alle zu ihr gehörigen Gruppen
     * Aus der Selben gelesen und in der SQL anweisung berücksichtigt.
     * @param gruppeOderFamilie ein Object entweder vom Typ GruppeFuerKalender oder GruppenFamilieFuerKalender. Für diese
     *                          Übergebene Gruppe oder alle Gruppen die in einer übergebenen Gruppenfamilie enthalten sind
     *                          sollen Daten aus der Datenbank ausgelesen werden
     * @return StringBuilder welche SQL anweisungen enthält welche Spezifizieren für Welche Gruppen Daten Abegrufen werden sollen
     * @throws SQLException wird bei Fehlern mit dem datenbankzugriff Mittels SQL Statment ausgelöst
     */
    private static StringBuilder getSQLExpressionChoosingGroups(Object gruppeOderFamilie) throws SQLException {
        StringBuilder gruppeOderFamilieSelectedBedingung = new StringBuilder(" and ");
        if(gruppeOderFamilie.getClass() == GruppeFuerKalender.class) {
            gruppeOderFamilieSelectedBedingung.append("g.gruppe_id = ").append(((GruppeFuerKalender) gruppeOderFamilie).getGruppeId());
        } else {
            boolean isFirstGruppInArray = true;
            gruppeOderFamilieSelectedBedingung.append("(");
            for (GruppeFuerKalender gr : ((GruppenFamilieFuerKalender) gruppeOderFamilie).getGruppenDerFamilie() ) {
                if(!isFirstGruppInArray) {
                    gruppeOderFamilieSelectedBedingung.append(" or ");
                }
                gruppeOderFamilieSelectedBedingung.append("g.gruppe_id = ").append(gr.getGruppeId());
                isFirstGruppInArray = false;
            }
            gruppeOderFamilieSelectedBedingung.append(")");
        }
        return gruppeOderFamilieSelectedBedingung;
    }

    /**
     * Erhält ein jahr sowie einen StringBuilder welcher zusätzlichen SQL bedingungen Enthält welche definieren für Welche gruppe
     * oder welche Gruppen Daten Ausgelesen werden sollen. Für das Übergebene Jahr und die Spezifizierten Gruppen werden aus der
     * Datenbank nun alle enträge aus der Tabelle gruppenkalender ausgelesen. Außerdem wird für Jeden Ausgelesenen Eintrag
     * ob die Küche an dem Entsprechenden Datum als Offen eingetragen ist, sowie ob es sich um einen Feiertag handelt oder
     * Betriebsurlaub für besagtes Datum angesetzt ist. Aus diesen Daten wird für jeden Eintrag ein GruppenKalenderTag erstellt
     * und in eine Entsprechende Arralyist hinzugefügt welche zurückgegeben wird.
     * @param jahr jahr für welches Daten ausgelesen werden sollen
     * @param gruppeOderFamilieSelectedBedignung String mit bedingungen welche spezifizieren für Weleche Gruppen Daten ausgelesen
     *                                           werden sollen. Wird ans ende des Strings mit SQL anweisungen des ausgeführten
     *                                           Statments angehängt.
     * @return eine ArrayList<GruppenKalenderTag> welche alle Ausgelesenen Tage enthält
     * @throws SQLException wird bei Fehlern mit dem datenbankzugriff Mittels SQL Statment ausgelöst
     */
    private static ArrayList<GruppenKalenderTag> readGruppenKalenderTageDatenbankzugriff(Integer jahr, StringBuilder gruppeOderFamilieSelectedBedignung) throws SQLException {
        ArrayList<GruppenKalenderTag> kalenderTagListe = new ArrayList<>();
        try (Statement stmt = conn.createStatement()) {
            try(ResultSet rs = stmt.executeQuery("" +
                    "select g.*, k.geoeffnet as kgeoeffnet ,b.datum as bdatum, f.datum as fdatum from gruppenkalender g " +
                    "left join kuechenplanung k on g.datum = k.datum " +
                    "left join betriebsurlaub b on g.datum = b.datum " +
                    "left join feiertag f on g.datum = f.datum " +
                    "where g.datum >= '" + jahr + "-01-01' and g.datum <= '" + jahr +"-12-31'" + gruppeOderFamilieSelectedBedignung)) {
                while(rs.next()) {
                    LocalDate datum = LocalDate.parse(rs.getDate("datum").toString());
                    Integer gruppen_id =  rs.getInt("gruppe_id");
                    Character gruppenstatus = rs.getString("gruppenstatus").toCharArray()[0];
                    Boolean isBetriebsurlaub = (rs.getDate("bdatum") != null);
                    Boolean isFeiertag = (rs.getDate("fdatum") != null);
                    Boolean kuechheOffen = rs.getBoolean("kgeoeffnet");
                    if(isFeiertag) {gruppenstatus = UsefulConstants.getStatusListCharacterFormat().get(6);}
                    GruppenKalenderTag tag = new GruppenKalenderTag(gruppen_id, datum, gruppenstatus, kuechheOffen, isBetriebsurlaub);
                    kalenderTagListe.add(tag);
                }
            }
        }
        return kalenderTagListe;
    }

    /**
     * Fragt aus der Datenabnk alle Gruppenfamilien und Dazu Gehörigen Gruppen ab, Erstellt aus ihnen GruppenFamilieFuerKalender und GruppeFuerKalender
     * Objekte, schreibt die Gruppen in die Gruppenliste ihrer jeweiligen Familie und die Familien in die Arraylist gruppenFamilieListe
     * @return eine ArrayListe aller in der Datenbank Vorhandenen Gruppenfamilien (welche die Zugehörigen Gruppen enthalten)
     * @throws SQLException wird bei Problemen mit dem Datenbankzugriff mittels SQL geworfen
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
     * @param gr Gruppe für die neue Einträge Generiert werden sollen
     * @param jahr jahr für welches einträge Generiert werden sollen
     * @throws SQLException Wird geworfen wenn fehler beim Ausführne des SQL statments auftreten
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
     * @param jahr gegebenes Jahr als Integer
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
     * @param gr Gruppe für welche das vorhandenseiens eines Datensatzes für den ersten werktag des jahres überprüft werden soll
     * @param jahr jahr für welches sich der erste Werktag bezieht.
     * @return true wenn der Tag in der Datenbank vorhanden ist, fallse wenn er nicht vorhanden ist
     * @throws SQLException wird geworfen wenn Fehler mit der Ausführugn des SQL statments auftreten
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
     * @param datum Datum für welches Überprüft werden soll ob es sich um einen Werktag handelt
     * @return true wenn es sich beim Übergebenen LocalDate um einen Werktag handelt (ausgenommen feiertag) False wenn nicht
     */
    public static Boolean datumIstWerktag(LocalDate datum) {
        return UsefulConstants.getTageListInLocalDateFormat().contains(datum.getDayOfWeek().toString());
    }

    /**
     * Erhält eine ArrayList<GruppenFamilieFuerKalender. Jedes GruppenFamilieFuerKalender Object enthält eine ArrayList
     * welche alle zu diser Gruppenfamilie gehörenden Gruppen enthält. Liest all diese Arraylisten aus den Gruppenfamilien
     * aus und fügt alle gruppen aller Familien zu einer ArrayList<GruppeFuerKalender> hinzu.
     * @param gruppenFamilienListe liste von Gruppenfamilien denen Die zu ihnen gehörenden Gruppen entnommen werden sollen
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
     * @param datum datum von Welchem aus der Nächste Werktag ermittelt werden soll.
     * @return den Nächsten Werktag als LocalDate
     */
    public static LocalDate getNextWerktag(LocalDate datum) {
        while(!datumIstWerktag(datum)) {
            datum = datum.plusDays(1);
        }
        return datum;
    }

    //TODO Add Documentation
    public static void saveBetriebsurlaub(ObservableList<BetriebsurlaubsTag> betriebsurlaubsTagListe) throws SQLException {
        if(betriebsurlaubsTagListe.isEmpty()) return;
        Statement stmt = conn.createStatement();
        for (BetriebsurlaubsTag tag : betriebsurlaubsTagListe) {
            Boolean beganAsBetriebsurlaub = tag.getBeganAsBetriebsurlaub();
            Boolean isCurrentlyBetriebsurlaub = (tag.getIsCurrentlyBetriebsurlaub() == 1);
            if(isCurrentlyBetriebsurlaub == beganAsBetriebsurlaub) continue;
            if(isCurrentlyBetriebsurlaub) {
                try{
                    stmt.execute("insert into betriebsurlaub (datum) values ('" + tag.getDatum().toString() + "')");
                    System.out.println("saveBetriebsurlaub() new betriebsurlaub Added");
                } catch (Exception e) {
                    //TODO Fehlermeldung
                }
            }
            System.out.println("saveBetriebsurlaub()" + beganAsBetriebsurlaub);
            if(beganAsBetriebsurlaub){
                try{
                    stmt.execute("delete from betriebsurlaub where datum = '" + tag.getDatum().toString() + "'");
                    System.out.println("saveBetriebsurlaub() Alter Betriebsurlaub removed");
                } catch (Exception e) {
                    //TODO Fehlermeldung
                }
            }
        }

    }

    //TODO Add Documentation
    public static void saveKuechenKalender(ObservableList<KuechenKalenderTag> kuechenTagesListe) throws SQLException {
        if(kuechenTagesListe.isEmpty()) return;
        Statement stmt = conn.createStatement();

        for (KuechenKalenderTag tag : kuechenTagesListe) {
            Boolean kuecheGeoffnet = (tag.getKuecheCurrentlyGeoeffnet() == 1);
            try {
                stmt.execute("update kuechenplanung set geoeffnet = " + kuecheGeoffnet + " WHERE datum = '" + tag.getDatum().toString() + "'");
            } catch (Exception e) {
                //TODO Fehlermeldung
            }
        }
    }

    //TODO Add Documentation
    public static void saveGruppenKalender(ObservableList<GruppenKalenderTag> gruppenkalnderTagesListe) throws SQLException {
        if(gruppenkalnderTagesListe.isEmpty()) return;
        Statement stmt = conn.createStatement();
        for (GruppenKalenderTag tag : gruppenkalnderTagesListe) {
            try {
                stmt.execute("update gruppenkalender " +
                        "set essensangebot = " + tag.getEssenFuerGruppeVerfuegbar() + ", gruppenstatus = '" + tag.getGruppenstatus() + "' " +
                        "where gruppe_id = " + tag.getGruppenID() + " and datum = '" + tag.getDatum().toString() + "'");
            } catch (Exception e) {
                //TODO Fehlermeldung
            }
        }
    }
}
