package com.example.gruppenkalenderfeiertragurlaub.speicherklassen;

import java.time.LocalDate;

public class GruppenKalenderTag  extends TagBasisKlasse {
    private final Integer gruppenID;
    private Character gruppenstatus;
    private Boolean essenFuerGruppeVerfuegbar;
    //TODO Implement mechanic to determin of day is Feiertag or Urlaubstag
    private Boolean isBetriebsurlaub;
    private Boolean isFeiertag;



    public GruppenKalenderTag(Integer gruppenID, LocalDate datum, Character gruppenstatus, Boolean essenFuerGruppeVerfuegbar) {
        this.gruppenID = gruppenID;
        this.datum = datum;
        this.gruppenstatus = gruppenstatus;
        this.essenFuerGruppeVerfuegbar = essenFuerGruppeVerfuegbar;
    }

    public GruppenKalenderTag(Integer gruppenID, LocalDate datum, Character gruppenstatus, Boolean essenFuerGruppeVerfuegbar, Boolean isBetriebsurlaub, Boolean isFeiertag) {
        this.gruppenID = gruppenID;
        this.datum = datum;
        this.gruppenstatus = gruppenstatus;
        this.essenFuerGruppeVerfuegbar = essenFuerGruppeVerfuegbar;
        this.isBetriebsurlaub = isBetriebsurlaub;
        this.isFeiertag = isFeiertag;
    }

    public Boolean getBetriebsurlaub() {
        return isBetriebsurlaub;
    }

    public void setBetriebsurlaub(Boolean betriebsurlaub) {
        isBetriebsurlaub = betriebsurlaub;
    }

    public Boolean getFeiertag() {
        return isFeiertag;
    }

    public void setFeiertag(Boolean feiertag) {
        isFeiertag = feiertag;
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
}
