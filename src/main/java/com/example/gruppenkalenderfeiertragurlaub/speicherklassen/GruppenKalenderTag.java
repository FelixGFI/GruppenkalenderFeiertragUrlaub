package com.example.gruppenkalenderfeiertragurlaub.speicherklassen;

import java.time.LocalDate;

public class GruppenKalenderTag  extends TagBasisKlasse {
    private final Integer gruppenID;
    private Character gruppenstatus;
    private Boolean kuecheGeoeffnet;
    private Boolean essenFuerGruppeVerfuegbar;
    //TODO Implement mechanic to determin of day is Feiertag or Urlaubstag
    private Boolean isBetriebsurlaub;

    public GruppenKalenderTag(Integer gruppenID, LocalDate datum, Character gruppenstatus, Boolean kuecheGeoeffnet, Boolean essenFuerGruppeVerfuegbar, Boolean isBetriebsurlaub) {
        this.gruppenID = gruppenID;
        this.datum = datum;
        this.gruppenstatus = gruppenstatus;
        this.kuecheGeoeffnet = kuecheGeoeffnet;
        this.essenFuerGruppeVerfuegbar = essenFuerGruppeVerfuegbar;
        this.isBetriebsurlaub = isBetriebsurlaub;
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
    }

    public Boolean getEssenFuerGruppeVerfuegbar() {
        return essenFuerGruppeVerfuegbar;
    }

    public void setEssenFuerGruppeVerfuegbar(Boolean essenFuerGruppeVerfuegbar) {
        this.essenFuerGruppeVerfuegbar = essenFuerGruppeVerfuegbar;
    }
    public Boolean getKuecheGeoeffnet() {
        return kuecheGeoeffnet;
    }
}
