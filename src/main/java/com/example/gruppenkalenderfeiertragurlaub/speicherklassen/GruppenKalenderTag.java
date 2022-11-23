package com.example.gruppenkalenderfeiertragurlaub.speicherklassen;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.UsefulConstants;

import java.time.LocalDate;
import java.util.ArrayList;

public class GruppenKalenderTag  extends TagBasisKlasse {
    private final Integer gruppenID;
    private Character gruppenstatus;
    private Boolean kuecheGeoeffnet;
    private Boolean essenFuerGruppeVerfuegbar;
    private Boolean isBetriebsurlaub;
    private Boolean tagWasEdited;

    public GruppenKalenderTag(Integer gruppenID, LocalDate datum, Character gruppenstatus, Boolean kuecheGeoeffnet, Boolean isBetriebsurlaub) {
        this.gruppenID = gruppenID;
        this.datum = datum;
        this.gruppenstatus = gruppenstatus;
        this.kuecheGeoeffnet = kuecheGeoeffnet;
        this.isBetriebsurlaub = isBetriebsurlaub;
        aktualisiereEssenFuerGruppeVerfuegbar();
        tagWasEdited = false;
    }

    public Boolean getBetriebsurlaub() {
        return isBetriebsurlaub;
    }
    public Integer getGruppenID() {return gruppenID;}
    public Character getGruppenstatus() {
        return gruppenstatus;
    }
    public void setGruppenstatus(Character gruppenstatus) {
        this.gruppenstatus = gruppenstatus;
        tagWasEdited = true;
        aktualisiereEssenFuerGruppeVerfuegbar();
    }
    public Boolean getEssenFuerGruppeVerfuegbar() {
        return essenFuerGruppeVerfuegbar;
    }

    /**
     * wenn abegrufen, überprüft und akktuallisiert ob für die gruppe Essen verfügbar ist anhand des Gruppenstatus und ob die
     * Küche für diesen Tag geöffnet ist.
     */
    protected void aktualisiereEssenFuerGruppeVerfuegbar() {
        ArrayList<Character> statusListe = UsefulConstants.getStatusListCharacterFormat();
        this.essenFuerGruppeVerfuegbar = ((this.gruppenstatus == statusListe.get(0) || this.gruppenstatus == statusListe.get(5) && this.kuecheGeoeffnet));
    }
    public Boolean getTagWasEdited() {
        return tagWasEdited;
    }
}
