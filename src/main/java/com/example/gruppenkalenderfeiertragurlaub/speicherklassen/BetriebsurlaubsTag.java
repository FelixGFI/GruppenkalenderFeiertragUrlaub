package com.example.gruppenkalenderfeiertragurlaub.speicherklassen;

import java.time.LocalDate;

public class BetriebsurlaubsTag extends TagBasisKlasse {
    private Boolean isBetriebsurlaub;

    public BetriebsurlaubsTag(LocalDate datum, Boolean isBetriebsurlaub, Boolean isFeiertag) {
        this.isFeiertag = isFeiertag;
        this.datum = datum;
        this.isBetriebsurlaub = isBetriebsurlaub;
    }

    public Boolean getIsBetriebsurlaub() {
        return isBetriebsurlaub;
    }

    public void setIsBetriebsurlaub(Boolean isBetriebsurlaub) {
        this.isBetriebsurlaub = isBetriebsurlaub;
    }
}
