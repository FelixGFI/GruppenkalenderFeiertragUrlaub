package com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen;

import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.*;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
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
     * Stellt die Verbindung mit der Datenbank her deren daten in globalen Variable definiert sind. Verursacht ein Popup
     * sollte die verbindung nicht möglich sein
     */
    public static void establishConnection() {
        //create connection for a server installed in localhost, with a user "root" with no password
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setTitle("Fehler");
            alert.setHeaderText("Datenbankverbindung Fehlgeschlagen");
            alert.setContentText("Bitte Stellen Sie sicher das das Programm zugriff auf die Datenbank hat. " +
                    "[" + url + "]");
            alert.showAndWait();
        }
    }

    /**
     * Liest alle werte aus der Tabelle kuechenplanung für das übergebene Jahr und speichert diese in eine ArrayList
     * von KüchenkalenderTag objekten. Liest außerdem aus, ob es sich bei einem Tag um einen Feiertag handelt.
     * Überprüft ob Daten für dieses Jahr in der Datenbank vorhanden sind, wenn nicht, generiert es diese für das übergebene jahr und
     * fährt dan mit dem einlesen fort. Übergibt die eingelesenen Daten als ArrayList von KuechenKalenderTagen.
     * @param jahr jahr für welches daten ausgelesen werden sollen
     * @return Liste aller datenbankeinträge in kuechenplanung für das übergebene jahr als
     * @throws SQLException wird bei Fehlern mit dem Datenbankzugriff Mittels SQL Statement ausgelöst
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
                    boolean kuecheOffen = rs.getBoolean("geoeffnet");
                    boolean isFeiertag = (rs.getDate("fdatum") != null);
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
     * Überprüft mittels Methodenaufruf, ob für ein bestimmtes Jahr der erste Werktag des Jahres in der Datenbanktabelle kuechenplanung vorhanden ist.
     * wenn dies der Fall ist, geht die Methode davon aus, das alle nötigen Einträge dieses Jahres bereits in der Datenbank vorhanden sind.
     * Wenn nicht, generiert die Methode eine ArrayList<LocalDate> von allen Werktagen des Jahres und schreibt zu jedem Datum einen eintrag
     * in die Tabelle gruppenkalender.
     * @param jahr das Auszulesende Jahr als integer
     * @throws SQLException wird bei Fehlern mit dem Datenbankzugriff Mittels SQL Statement ausgelöst
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
     * ermittelt den ersten werktag eines Jahres. Überprüft, ob ein Eintrag für diesen, für dieses Jahr in der Datenbanktabelle kuechenplanung vorhanden ist.
     * @param jahr jahr für das überprüft werden soll ob der küchendatensatz vorhanden ist
     * @return True, wenn ein Datensatz für den ersten werktag für das gegebene Jahr vorhanden ist, false wenn nicht.
     * @throws SQLException wird bei Fehlern mit dem Datenbankzugriff Mittels SQL Statement ausgelöst
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
     * Überprüft ob, in der kuechenplanung tabelle Einträge für das übergebene Jahr vorhanden sind. Wenn nicht, werden diese generiert.
     * Liest für das übergebene Jahr alle einträge in der Spalte Datum der kuechenplanung tabelle. Für jedes gelesene Datum sucht es in der Tabelle
     * Betriebsurlaub nach einem identischen Datum sowie in der Tabelle Feiertag nach einem Feiertag mit entsprechendem Datum.
     * Wenn, ein datum gefunden wird ist klar, das es sich bie dem gelesenen Datum um einen bereits festgelegten Betriebsurlaubstag
     * bzw. einen Feiertag handelt. Wird kein entsprechendes Datum gefunden, so ist der Tag kein Betriebsurlaubstag und auch kein
     * Feiertag. Für jeden ausgelesenen Tag wird ein Object der Klasse BetriebsurlaubsTag erzeugt. Ob es sich um einen
     * Betriebsurlaub(1) einen Feiertag(2) oder um keins von Beidem(0) handelt, wird mittels des Integer attributs Betriebsurlaub
     * gespeichert. Die erstellten Betriebsurlaubstage werden in einer Arraylist zurückgegeben.
     * @param jahr Jahr für welches der Betriebsurlaub ausgelesen werden soll
     * @return Liste aller Datenbankeinträge datum aus kuechenplanung und Betriebsurlaub für das übergebene Jahr
     * @throws SQLException wird bei Fehlern mit dem Datenbankzugriff mittels SQL Statement ausgelöst
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
                    boolean isBetriebsurlaub = (rs.getDate("bdatum") != null);
                    boolean isFeiertag = (rs.getDate("fdatum") != null);

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
     * GruppenKalenderTag objekten. Liest außerdem aus, ob ein bestimmter Tag ein Betriebsurlaubs- oder Feiertag ist
     * und speichert diese Daten für jeden Eintrag in einem Objekt der Klasse GruppenKalenderTag.
     * Wenn, keine Daten für eine Gruppe und das entsprechende Jahr vorhanden sind, werden diese generiert.
     * @param gruppeOderFamilie enthält entweder ein Object GruppeFuerkalender oder GruppenFamilieFuerKalender.
     *                          Letzteres enthält eine Arraylist mit allen Gruppen diese Gruppenfamilie.
     * @param jahr das gegebene Jahr als Integer
     * @return Liste aller datenbankeinträge in Gruppenkalender für das übergebene Jahr
     * @throws SQLException wird bei Fehlern mit dem Datenbankzugriff mittels SQL Statement ausgelöst
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
        return readGruppenKalenderTageDatenbankzugriff(jahr, gruppeOderFamilieSelectedBedingung);
    }

    /**
     * Der Zweck dieser Methode ist anhand einer übergebenen Gruppe oder Gruppenfamilie einen StringBuilder zu erzeugen welcher
     * SQL Anweisungen enthält welche spezifizieren für welche Gruppen Daten abgerufen werden sollen. Wird eine Gruppe übergeben
     * so wird diese in der SQL Anweisung berücksichtigt, wird eine Gruppenfamilie angegeben so werden alle zu ihr gehörigen Gruppen
     * aus derselben gelesen und in der SQL Anweisung berücksichtigt.
     * @param gruppeOderFamilie ein Object entweder vom Typ GruppeFuerKalender oder GruppenFamilieFuerKalender. Für diese
     *                          übergebene Gruppe oder alle Gruppen die in einer übergebenen Gruppenfamilie enthalten sind
     *                          sollen Daten aus der Datenbank ausgelesen werden
     * @return StringBuilder welche SQL anweisungen enthält welche spezifizieren für welche Gruppen Daten abgerufen werden sollen
     */
    private static StringBuilder getSQLExpressionChoosingGroups(Object gruppeOderFamilie) {
        StringBuilder gruppeOderFamilieSelectedBedingung = new StringBuilder(" and ");
        if(gruppeOderFamilie.getClass() == GruppeFuerKalender.class) {
            gruppeOderFamilieSelectedBedingung.append("g.gruppe_id = ").append(((GruppeFuerKalender) gruppeOderFamilie).getGruppeId());
        } else {
            boolean isFirstGruppeInArray = true;
            gruppeOderFamilieSelectedBedingung.append("(");
            for (GruppeFuerKalender gr : ((GruppenFamilieFuerKalender) gruppeOderFamilie).getGruppenDerFamilie() ) {
                if(!isFirstGruppeInArray) {
                    gruppeOderFamilieSelectedBedingung.append(" or ");
                }
                gruppeOderFamilieSelectedBedingung.append("g.gruppe_id = ").append(gr.getGruppeId());
                isFirstGruppeInArray = false;
            }
            gruppeOderFamilieSelectedBedingung.append(")");
        }
        return gruppeOderFamilieSelectedBedingung;
    }

    /**
     * Erhält ein Jahr sowie einen StringBuilder welcher zusätzlichen SQL Bedingungen enthält welche definieren für welche Gruppe
     * oder welche Gruppen Daten ausgelesen werden sollen. Für das übergebene Jahr und die spezifizierten Gruppen werden aus der
     * Datenbank nun alle Einträge aus der Tabelle gruppenkalender ausgelesen. Außerdem wird für jeden ausgelesenen Eintrag überprüft,
     * ob die Küche an dem entsprechenden Datum als offen eingetragen ist, sowie ob es sich um einen Feiertag handelt oder
     * Betriebsurlaub für besagtes Datum angesetzt ist. Aus diesen Daten wird für jeden Eintrag ein GruppenKalenderTag erstellt
     * und in eine entsprechende Arraylist hinzugefügt welche zurückgegeben wird.
     * @param jahr Jahr, für welches Daten ausgelesen werden sollen.
     * @param gruppeOderFamilieSelectedBedingung String mit Bedingungen welche spezifizieren für welche Gruppen Daten ausgelesen
     *                                           werden sollen. Wird ans Ende des Strings mit SQL Anweisungen des ausgeführten
     *                                           Statements angehängt.
     * @return eine ArrayList<GruppenKalenderTag> welche alle ausgelesenen Tage enthält
     * @throws SQLException wird bei Fehlern mit dem Datenbankzugriff mittels SQL Statement ausgelöst
     */
    private static ArrayList<GruppenKalenderTag> readGruppenKalenderTageDatenbankzugriff(Integer jahr, StringBuilder gruppeOderFamilieSelectedBedingung) throws SQLException {
        ArrayList<GruppenKalenderTag> kalenderTagListe = new ArrayList<>();
        try (Statement stmt = conn.createStatement()) {
            try(ResultSet rs = stmt.executeQuery("" +
                    "select g.*, k.geoeffnet as kgeoeffnet ,b.datum as bdatum, f.datum as fdatum from gruppenkalender g " +
                    "left join kuechenplanung k on g.datum = k.datum " +
                    "left join betriebsurlaub b on g.datum = b.datum " +
                    "left join feiertag f on g.datum = f.datum " +
                    "where g.datum >= '" + jahr + "-01-01' and g.datum <= '" + jahr +"-12-31'" + gruppeOderFamilieSelectedBedingung)) {
                while(rs.next()) {
                    LocalDate datum = LocalDate.parse(rs.getDate("datum").toString());
                    Integer gruppen_id =  rs.getInt("gruppe_id");
                    Character gruppenstatus = rs.getString("gruppenstatus").toCharArray()[0];
                    Boolean isBetriebsurlaub = (rs.getDate("bdatum") != null);
                    boolean isFeiertag = (rs.getDate("fdatum") != null);
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
     * Fragt aus der Datenbank alle Gruppenfamilien und dazugehörigen Gruppen ab, erstellt aus ihnen GruppenFamilieFuerKalender und GruppeFuerKalender
     * Objekte, schreibt die Gruppen in die Gruppenliste ihrer jeweiligen Familie und die Familien in die Arraylist gruppenFamilieListe
     * @return eine ArrayListe aller in der Datenbank vorhandenen Gruppenfamilien (welche die zugehörigen Gruppen enthalten)
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
     * Überprüft mittels Methodenaufruf, ob für ein bestimmtes Jahr und eine bestimmte Gruppe der erste Werktag des Jahres in der Datenbanktabelle
     * gruppenkalender vorhanden ist. Wenn dies der Fall ist, geht die Methode davon aus das alle nötigen Einträge dieses Jahres bereits in der
     * Datenbank sind, wenn nicht, generiert die Methode ein ArrayList<LocalDate> von allen Werktagen des Jahres und erzeugt mit der Nummer der
     * übergebenen Gruppe alle entsprechenden Einträge in der Datenbank (Tabelle: gruppenkalender)
     * @param gr Gruppe für, die neue Einträge generiert werden sollen
     * @param jahr jahr für, welches einträge generiert werden sollen
     * @throws SQLException Wird geworfen, wenn fehler beim Ausführen des SQL Statements Fehler auftreten
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
     * Ermittelt für das gegebene Jahr alle Werktage als LocalDate und speichert diese in einer ArrayList<LocalDate> die zurückgegeben wird.
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
     * Überprüft, ob der Erste Werktag des gegebenen Jahres für die gegebene Gruppe als Eintrag in der Datenbank existiert.
     * (Das Programm soll nur ganze Jahre für eine Gruppe gleichzeitig generieren, und nur Werktage. Deswegen reicht es aus
     * nach dem ersten Werktag des Jahres zu suchen, um festzustellen, ob daten für das gegebene Jahr in der Datenbank vorhanden sind)
     * @param gr Gruppe für welche das Vorhandensein eines Datensatzes für den ersten werktag des jahres überprüft werden soll
     * @param jahr jahr für welches sich der erste Werktag bezieht.
     * @return True, wenn der Tag in der Datenbank vorhanden ist, false, wenn er nicht vorhanden ist
     * @throws SQLException wird geworfen, wenn Fehler mit der Ausführung des SQL Statements auftreten
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
     * ermittelt, ob es sich beim übergebenen LocalDate datum um einen Werktag (tag von Montag bis Freitag) handelt. (Feiertage werden nicht
     * berücksichtigt, nur Wochenende)
     * @param datum Datum für welches Überprüft werden soll, ob es sich um einen Werktag handelt
     * @return True, wenn es sich beim Übergebenen LocalDate um einen Werktag handelt (ausgenommen feiertag) false, wenn nicht.
     */
    public static Boolean datumIstWerktag(LocalDate datum) {
        return UsefulConstants.getTageListInLocalDateFormat().contains(datum.getDayOfWeek().toString());
    }

    /**
     * Erhält eine ArrayList<GruppenFamilieFuerKalender. Jedes GruppenFamilieFuerKalender Object enthält eine ArrayList
     * welche alle zu dieser Gruppenfamilie gehörenden Gruppen enthält. Liest all diese Arraylists aus den Gruppenfamilien
     * aus und fügt alle gruppen aller Familien zu einer ArrayList<GruppeFuerKalender> hinzu.
     * @param gruppenFamilienListe liste von Gruppenfamilien denen Die zu ihnen gehörenden Gruppen entnommen werden sollen
     * @return ArrayList<GruppeFuerKalender> welche alle gruppen aller Familien in der übergebenen ArrayList enthält.
     */
    public static ArrayList<GruppeFuerKalender> getAlleGruppenAusFamilien(ArrayList<GruppenFamilieFuerKalender> gruppenFamilienListe) {
        ArrayList<GruppeFuerKalender> gruppenListe = new ArrayList<>();
        for (GruppenFamilieFuerKalender grFa : gruppenFamilienListe) {
            gruppenListe.addAll(grFa.getGruppenDerFamilie());
        }
        return gruppenListe;
    }

    /**
     * Findet heraus welches, vom gegebenen Datum aus gerechnet der nächste Werktag ist (berücksichtigt nicht Feiertage).
     * Wenn das übergebene Datum ein Werktag ist, gibt die Methode besagtes Datum unverändert zurück, ansonsten den nächsten gefundenen
     * Werktag.
     * @param datum Datum, von welchem aus der Nächste Werktag ermittelt werden soll.
     * @return den Nächsten Werktag als LocalDate
     */
    public static LocalDate getNextWerktag(LocalDate datum) {
        while(!datumIstWerktag(datum)) {
            datum = datum.plusDays(1);
        }
        return datum;
    }

    /**
     * Überprüft für jeden Tag in der Liste, ob sein Originalzustand mit dem jetzigen Zustand übereinstimmt. Ist ein Tag,
     * der früher Betriebsurlaub war jetzt keiner mehr, so wird sein Eintrag aus der Datenbank gelöscht. Für jeden neuen
     * als Betriebsurlaub generierten Tag wird ein neuer Eintrag erzeugt.
     * @param betriebsurlaubsTagListe ObservableList aller BetriebsurlaubsTage deren Änderungen gespeichert werden sollen
     * @throws SQLException Wird geworfen, wenn Fehler mit der Ausführung des SQL Statements auftreten
     */
    public static void saveBetriebsurlaub(ObservableList<BetriebsurlaubsTag> betriebsurlaubsTagListe) throws SQLException {
        if(betriebsurlaubsTagListe.isEmpty()) return;
        Statement stmt = conn.createStatement();
        for (BetriebsurlaubsTag tag : betriebsurlaubsTagListe) {
            Boolean beganAsBetriebsurlaub = tag.getBeganAsBetriebsurlaub();
            boolean isCurrentlyBetriebsurlaub = (tag.getIsCurrentlyBetriebsurlaub() == 1);
            if(isCurrentlyBetriebsurlaub == beganAsBetriebsurlaub) continue;
            if(isCurrentlyBetriebsurlaub) {
                try{
                    stmt.execute("insert into betriebsurlaub (datum) values ('" + tag.getDatum().toString() + "')");
                } catch (Exception e) {
                    //TODO Fehlermeldung
                }
            }
            if(beganAsBetriebsurlaub){
                try{
                    stmt.execute("delete from betriebsurlaub where datum = '" + tag.getDatum().toString() + "'");
                } catch (Exception e) {
                    //TODO Fehlermeldung
                }
            }
        }
    }

    /**
     * Die Methode erhält eine Liste von KuechenKalenderTagen. Jeder tag an dem während der Bearbeitung durch den Nutzer
     * änderungen vorgenommen wurden, wird in der Datenbank mittels SQL statement aktualisiert.
     * @param kuechenTagesListe Observable List aller vorhanden KuechenKalenderTage deren Änderungen gespeichert werden sollen
     * @throws SQLException Wird geworfen, wenn Fehler mit der Ausführung des SQL Statements auftreten.
     */
    public static void saveKuechenKalender(ObservableList<KuechenKalenderTag> kuechenTagesListe) throws SQLException {
        if(kuechenTagesListe.isEmpty()) return;
        Statement stmt = conn.createStatement();
        for (KuechenKalenderTag tag : kuechenTagesListe) {
            boolean kuecheGeoffnet = (tag.getKuecheCurrentlyGeoeffnet() == 1);
            if(!tag.getTagWasEdited()) continue;
            try {
                stmt.execute("update kuechenplanung set geoeffnet = " + kuecheGeoffnet + " WHERE datum = '" + tag.getDatum().toString() + "'");
            } catch (Exception e) {
                //TODO Fehlermeldung
            }
        }
    }

    /**
     * erhält eine Liste an GruppenkalenderTagen und überprüft für jeden tag ob besagter editiert worden ist. Wenn ja, wird
     * der entsprechende eintrag in der Datenbank aktualisiert.
     * @param gruppenkalnderTagesListe Observable List aller GruppenKalenderTage deren Änderungen gespeichert werden sollen
     * @throws SQLException Wird geworfen, wenn Fehler mit der Ausführung des SQL Statements auftreten
     */
    public static void saveGruppenKalender(ObservableList<GruppenKalenderTag> gruppenkalnderTagesListe) throws SQLException {
        if(gruppenkalnderTagesListe.isEmpty()) return;
        Statement stmt = conn.createStatement();
        for (GruppenKalenderTag tag : gruppenkalnderTagesListe) {
            if(!tag.getTagWasEdited()) continue;
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
