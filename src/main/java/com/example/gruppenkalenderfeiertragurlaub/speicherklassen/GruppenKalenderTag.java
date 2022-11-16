package com.example.gruppenkalenderfeiertragurlaub.speicherklassen;

import java.time.LocalDate;

public class GruppenKalenderTag  extends TagBasisKlasse {
    private final Integer gruppenID;
    private Character gruppenstatus;
    private Boolean essenFuerGruppeVerfuegbar;

    public GruppenKalenderTag(Integer gruppenID, LocalDate datum, Character gruppenstatus, Boolean essenFuerGruppeVerfuegbar) {
        this.gruppenID = gruppenID;
        this.datum = datum;
        this.gruppenstatus = gruppenstatus;
        this.essenFuerGruppeVerfuegbar = essenFuerGruppeVerfuegbar;
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
