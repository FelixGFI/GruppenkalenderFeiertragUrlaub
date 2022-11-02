package com.example.gruppenkalenderfeiertragurlaub.speicherklassen;

import java.time.LocalDate;

public class BetriebsurlaubsTag {

    LocalDate datum;
    Boolean isBetriebsurlaub;

    public BetriebsurlaubsTag(LocalDate datum, Boolean isBetriebsurlaub) {
        this.datum = datum;
        this.isBetriebsurlaub = isBetriebsurlaub;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

    public Boolean getIsBetriebsurlaub() {
        return isBetriebsurlaub;
    }

    public void setIsBetriebsurlaub(Boolean isBetriebsurlaub) {
        this.isBetriebsurlaub = isBetriebsurlaub;
    }
}
