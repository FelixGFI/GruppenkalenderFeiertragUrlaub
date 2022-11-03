package com.example.gruppenkalenderfeiertragurlaub.speicherklassen;

import java.time.LocalDate;

public class GruppenKalenderTag {
    int gruppenID;
    LocalDate datum;
    Character gruppenstatus;
    Boolean essenFuerGruppeVerfuegbar;

    public GruppenKalenderTag(int gruppenID, LocalDate datum, Character gruppenstatus, Boolean essenFuerGruppeVerfuegbar) {
        this.gruppenID = gruppenID;
        this.datum = datum;
        this.gruppenstatus = gruppenstatus;
        this.essenFuerGruppeVerfuegbar = essenFuerGruppeVerfuegbar;
    }

    public int getGruppenID() {
        return gruppenID;
    }

    public void setGruppenID(int gruppenID) {
        this.gruppenID = gruppenID;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

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
