package com.example.gruppenkalenderfeiertragurlaub.speicherklassen;

import java.time.LocalDate;

public class TagBasisKlasse {
    protected LocalDate datum;
    protected Boolean isFeiertag;
    public LocalDate getDatum() {
        return datum;
    }
    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }
    public Boolean getFeiertag() {
        return isFeiertag;
    }
}
