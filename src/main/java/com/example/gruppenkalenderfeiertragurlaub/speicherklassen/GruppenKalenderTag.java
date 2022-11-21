package com.example.gruppenkalenderfeiertragurlaub.speicherklassen;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.UsefulConstants;

import java.time.LocalDate;
import java.util.ArrayList;

public class GruppenKalenderTag  extends TagBasisKlasse {
    private final Integer gruppenID;
    private Character gruppenstatus;
    private Boolean kuecheGeoeffnet;
    private Boolean essenFuerGruppeVerfuegbar;
    //TODO Implement mechanic to determin of day is Feiertag or Urlaubstag
    private Boolean isBetriebsurlaub;

    public GruppenKalenderTag(Integer gruppenID, LocalDate datum, Character gruppenstatus, Boolean kuecheGeoeffnet, Boolean isBetriebsurlaub) {
        this.gruppenID = gruppenID;
        this.datum = datum;
        this.gruppenstatus = gruppenstatus;
        this.kuecheGeoeffnet = kuecheGeoeffnet;
        this.isBetriebsurlaub = isBetriebsurlaub;
        aktualisiereEssenFuerGruppeVerfuegbar();
    }

    public Boolean getBetriebsurlaub() {
        return isBetriebsurlaub;
    }

    public void setBetriebsurlaub(Boolean betriebsurlaub) {
        isBetriebsurlaub = betriebsurlaub;
    }

    public Integer getGruppenID() {return gruppenID;}

    public Character getGruppenstatus() {
        return gruppenstatus;
    }

    public void setGruppenstatus(Character gruppenstatus) {
        this.gruppenstatus = gruppenstatus;
        aktualisiereEssenFuerGruppeVerfuegbar();
    }
    public Boolean getEssenFuerGruppeVerfuegbar() {
        return essenFuerGruppeVerfuegbar;
    }
    public Boolean getKuecheGeoeffnet() {
        return kuecheGeoeffnet;
    }
    protected void aktualisiereEssenFuerGruppeVerfuegbar() {
        ArrayList<Character> statusListe = UsefulConstants.getStatusListCharacterFormat();
        this.essenFuerGruppeVerfuegbar = (this.gruppenstatus == statusListe.get(0) || this.gruppenstatus == statusListe.get(5));
    }
}
