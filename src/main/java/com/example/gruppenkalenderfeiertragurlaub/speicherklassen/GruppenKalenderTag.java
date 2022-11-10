package com.example.gruppenkalenderfeiertragurlaub.speicherklassen;

import java.time.LocalDate;

public class GruppenKalenderTag {
    private Integer gruppenID;
    private LocalDate datum;
    private Character gruppenstatus;
    private Boolean essenFuerGruppeVerfuegbar;

    public GruppenKalenderTag(Integer gruppenID, LocalDate datum, Character gruppenstatus, Boolean essenFuerGruppeVerfuegbar) {
        this.gruppenID = gruppenID;
        this.datum = datum;
        this.gruppenstatus = gruppenstatus;
        this.essenFuerGruppeVerfuegbar = essenFuerGruppeVerfuegbar;
    }

    public Integer getGruppenID() {return gruppenID;}

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
